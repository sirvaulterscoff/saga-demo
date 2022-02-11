package io.github.sirvaulterscoff.bookingservice

import io.github.sirvaulterscoff.bookingservice.dao.SagaRepository
import io.github.sirvaulterscoff.bookingservice.dao.UserBookingRepository
import io.github.sirvaulterscoff.bookingservice.model.BookingPrescript
import io.github.sirvaulterscoff.bookingservice.model.BookingReceipt
import io.github.sirvaulterscoff.bookingservice.model.BookingRecord
import io.github.sirvaulterscoff.bookingservice.model.BookingRequest
import io.github.sirvaulterscoff.bookingservice.service.*
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

@Component
class BookingListener(
    private val paymentGateway: PaymentGateway,
    private val bookingRepository: UserBookingRepository,
    private val kafkaTemplate: KafkaTemplate<Void, Any>,
    private val sagaRepository: SagaRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val transitAccountId: String = "0000-0000-0000"

    @KafkaListener(groupId = "booking.service", topics = ["booking"])
    fun receiveNewBooking(
        bookingRequest: BookingRequest,
        ack: Acknowledgment
    ) {
        logger.info("Got new booking request from {} for {}", bookingRequest.userId, bookingRequest.bookingObjectId)

        saga(sagaRepository, "aasdasd") {
            step("reserveFunds", block = {
                paymentGateway.reserveFunds(bookingRequest.userId, bookingRequest.amount)
            }, compensate =  {
                paymentGateway.unreserve(it!!)
            }).then("saveBookingRecord", block = {
                val bookingId = saveBookingRecord(bookingRequest)
                bookingId to it
            }, compensate =  {
                bookingRepository.deleteById(it.first)
            }).then("transferFunds", block =  { (bookingId, reservationId) ->
                val transfered = paymentGateway.transferFunds(reservationId!!, transitAccountId, "Booking #${bookingId} ")
                Triple(bookingId, reservationId, transfered)
            }, compensate = { (bookingId, _, _) ->
                val compensationReserveId = paymentGateway.reserveFunds(transitAccountId, bookingRequest.amount)
                paymentGateway.transferFunds(compensationReserveId!!, bookingRequest.userId, "Return for booking #${bookingId}")
            }).then("sendToKafka", block = {
                (_, reservationId, _) ->
                kafkaTemplate.send(
                    "book_place",
                    BookingPrescript(bookingRequest.bookingObjectId, bookingRequest.from, bookingRequest.to)
                )
                logger.info("Sent booking notification for {}", bookingRequest.bookingObjectId)
                kafkaTemplate.send(
                    "client_receipt",
                    BookingReceipt(bookingRequest.userId, reservationId, bookingRequest.amount)
                )
                logger.info("Sent receipt to {}", bookingRequest.userId)
                ack.acknowledge()
            }, compensate = {})
        }.launch()
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