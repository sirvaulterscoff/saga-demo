package io.github.sirvaulterscoff.bookingservice.controller

import io.github.sirvaulterscoff.bookingservice.dao.AccountEventsRepository
import io.github.sirvaulterscoff.bookingservice.model.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class PaymentProcessingController(
    private val accountEventsRepository: AccountEventsRepository
) {
    @RequestMapping("/account/{user}", method = [RequestMethod.HEAD])
    fun getBalance(@PathVariable("user") user: String): ResponseEntity<Void> {
        val balance =  accountEventsRepository.calculateBalance(user) ?: 0L
        return ResponseEntity.noContent().header("BALANCE", balance.toString()).build()
    }
    @GetMapping("/account/{user}")
    fun getTxs(@PathVariable("user") user: String): List<AccountEvent> {
        return accountEventsRepository.findAllByAccount(user)
    }

    @PostMapping("/account/{user}/reserve")
    fun reserveFunds(
        @PathVariable("user") user: String,
        @RequestBody request: ReserveFundsRequest
    ): ReserveFundsResponse {
        val balance = accountEventsRepository.calculateBalance(user) ?: 0L
        if (balance - request.amount < 0) {
            return ReserveFundsResponse(-1, ReserveFundsStatus.NOT_ENOUGH_MONEY)
        }
        val id = accountEventsRepository
            .save(AccountEvent(0, user, -request.amount, purpose = "Reservation of funds"))
            .id
        return ReserveFundsResponse(id, ReserveFundsStatus.RESERVED)
    }

    @PostMapping("/reserve/{reservationId}")
    fun transferFunds(
        @PathVariable("reservationId") reservationId: Long,
        @RequestBody request: TransferReservationRequest
    ) {
        val reservationAmt = accountEventsRepository
            .getById(reservationId)
            .amount
        accountEventsRepository.save(
            AccountEvent(
                0,
                request.accountId,
                -reservationAmt,
                sourceTxId = reservationId,
                purpose = request.purpose
            )
        )
    }

    @DeleteMapping("/reserve/{reservationId}")
    fun unreserveFunds(@PathVariable("reservationId") reservationId: Long) {
        val rolledBackReservation = accountEventsRepository
            .getById(reservationId)

        accountEventsRepository.save(
            AccountEvent(
                0,
                rolledBackReservation.account,
                -rolledBackReservation.amount,
                reservationId,
                "Unreserving funds reserved by txN$reservationId"
            )
        )
    }
}