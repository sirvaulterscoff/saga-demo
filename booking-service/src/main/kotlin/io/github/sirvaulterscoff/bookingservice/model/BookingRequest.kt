package io.github.sirvaulterscoff.bookingservice.model

data class BookingRequest (
    val userId: String,
    val amount: Long,
    val bookingObjectId: String,
    val from: String,
    val to: String
)
