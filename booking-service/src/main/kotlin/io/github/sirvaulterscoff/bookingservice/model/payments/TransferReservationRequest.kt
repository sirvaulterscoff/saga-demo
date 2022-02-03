package io.github.sirvaulterscoff.bookingservice.model.payments

data class TransferReservationRequest(
    val purpose: String,
    val accountId: String
)
