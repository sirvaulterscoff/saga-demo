package io.github.sirvaulterscoff.bookingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = [
	"io.github.sirvaulterscoff.bookingservice.model",
	"io.github.sirvaulterscoff.bookingservice.dao"])
class PaymentServiceApplication

fun main(args: Array<String>) {
	runApplication<PaymentServiceApplication>(*args)
}
