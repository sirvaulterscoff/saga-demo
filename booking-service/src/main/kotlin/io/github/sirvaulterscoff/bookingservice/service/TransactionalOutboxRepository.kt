package io.github.sirvaulterscoff.bookingservice.service

import io.github.sirvaulterscoff.bookingservice.model.BookingPrescript
import io.github.sirvaulterscoff.bookingservice.model.BookingReceipt
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class TransactionalOutboxRepository {
    @Transactional
    fun sendLater(bookingPrescript: BookingPrescript, bookingReceipt: BookingReceipt, after: () -> Unit) {
        //save bookingPrescript to DB
        //save bookingReceipt to DB
        after()
    }
}