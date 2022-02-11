package io.github.sirvaulterscoff.bookingservice

import io.github.sirvaulterscoff.bookingservice.service.TransactionalOutboxRepository
import io.github.sirvaulterscoff.bookingservice.dao.UserBookingRepository
import io.github.sirvaulterscoff.bookingservice.model.BookingPrescript
import io.github.sirvaulterscoff.bookingservice.model.BookingReceipt
import io.github.sirvaulterscoff.bookingservice.model.BookingRecord
import io.github.sirvaulterscoff.bookingservice.model.BookingRequest
import io.github.sirvaulterscoff.bookingservice.service.PaymentGateway
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

@Component
class TransactionalOutboxBookingListener(
    private val paymentGateway: PaymentGateway,
    private val bookingRepository: UserBookingRepository,
    private val transactionalOutboxService: TransactionalOutboxRepository,
    private val kafkaTemplate: KafkaTemplate<Void, Any>
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val transitAccountId: String = "0000-0000-0000"

    @KafkaListener(groupId = "booking.service", topics = ["booking"])
    fun receiveNewBooking(
        bookingRequest: BookingRequest,
        ack: Acknowledgment
    ) {
        logger.info("Got new booking request from {} for {}", bookingRequest.userId, bookingRequest.bookingObjectId)
        val previousBooking = bookingRepository.findByUserIdAndBookingObjectId(bookingRequest.userId, bookingRequest.bookingObjectId)
            .firstOrNull {
                it.from == bookingRequest.from && it.to == bookingRequest.to
            }
        if (previousBooking != null) {
            logger.info("Previous booking from {} for {} found. Not charging", bookingRequest.userId, bookingRequest.bookingObjectId)
            ack.acknowledge()
            return
        }

        val reservationId = paymentGateway.reserveFunds(bookingRequest.userId, bookingRequest.amount)
        reservationId?.let {
            logger.info("Succesfully reserved {} for account {}. Reservation id {}", bookingRequest.amount, bookingRequest.userId, it )
            val bookingId = saveBookingRecord(bookingRequest)
            logger.info("Saved booking record {}", bookingId)
            runCatching {
                paymentGateway.transferFunds(reservationId, transitAccountId, "Booking #${bookingId} ")
                logger.info("Transferred {} from {} to {}", bookingRequest.amount, bookingRequest.userId, transitAccountId)
            }.onFailure {
                logger.error("Failed to transfer funds for reservation {}, booking id {}", reservationId, bookingId, it)
                paymentGateway.unreserve(reservationId)
                logger.info("Rolled back reservation of {} for account {}", bookingRequest.amount, bookingRequest.userId)
            }.onSuccess {
                transactionalOutboxService.sendLater(
                    BookingPrescript(bookingRequest.bookingObjectId, bookingRequest.from, bookingRequest.to),
                    BookingReceipt(bookingRequest.userId, reservationId, bookingRequest.amount),
                after = { ack.acknowledge() })
            }
        }?: run {
            logger.error("Reserving funds failed")
        }
    }

    private fun saveBookingRecord(bookingRequest: BookingRequest): String {
        return bookingRepository.save(
            BookingRecord(
                UUID.randomUUID().toString(),
                bookingRequest.amount,
                bookingRequest.userId,
                bookingRequest.bookingObjectId,
                bookingRequest.from,
                bookingRequest.to
            )
        ).id
    }
}