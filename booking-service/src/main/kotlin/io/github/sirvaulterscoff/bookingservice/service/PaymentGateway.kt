package io.github.sirvaulterscoff.bookingservice.service

import io.github.sirvaulterscoff.bookingservice.model.payments.TransferReservationRequest
import io.github.sirvaulterscoff.bookingservice.model.payments.ReserveFundsRequest
import io.github.sirvaulterscoff.bookingservice.model.payments.ReserveFundsResponse
import io.github.sirvaulterscoff.bookingservice.model.payments.ReserveFundsStatus
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import org.springframework.web.client.postForObject

@Service
class PaymentGateway {

    private val restTemplate = RestTemplate()

    fun reserveFunds(userId: String, amount: Long): Long? {
        val request = ReserveFundsRequest(amount)
        val response = restTemplate
            .postForObject<ReserveFundsResponse>("http://localhost:8282/api/v1/account/$userId/reserve",request)
        return if (response.status == ReserveFundsStatus.RESERVED) {
            response.reservationId
        } else {
            null
        }
    }

    fun transferFunds(reservationId: Long, accountId: String, purpose: String): Boolean {
        val response = restTemplate
            .postForEntity<Void>("http://localhost:8282/api/v1/reserve/{reservation}",
                TransferReservationRequest(purpose, accountId),
                reservationId
            )
        return response.statusCode == HttpStatus.ACCEPTED

    }

    fun unreserve(reservationId: Long) {
        restTemplate.delete("http://localhost:8282/api/v1/reserve/{reservation}", reservationId)
    }
}