package io.github.sirvaulterscoff.bookingservice.model


data class ReserveFundsRequest(
    val amount: Long,
)

data class ReserveFundsResponse(
    val reservationId: Long,
    val status: ReserveFundsStatus
)

enum class ReserveFundsStatus {
    RESERVED, NOT_ENOUGH_MONEY,
}

data class TransferReservationRequest(
    val purpose: String?,
    val accountId: String
)
