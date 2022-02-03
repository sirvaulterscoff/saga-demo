package io.github.sirvaulterscoff.bookingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
@EnableJpaRepositories(basePackages = ["io.github.sirvaulterscoff.bookingservice"])
@EntityScan(basePackages = ["io.github.sirvaulterscoff.bookingservice.model"])
class BookingServiceApplication {
}

fun main(args: Array<String>) {
	runApplication<BookingServiceApplication>(*args)
}
