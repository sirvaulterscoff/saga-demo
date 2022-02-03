package io.github.sirvaulterscoff.bookingservice.model

data class BookingReceipt(
    val userId: String,
    val reservationId: Long?,
    val amount: Long)