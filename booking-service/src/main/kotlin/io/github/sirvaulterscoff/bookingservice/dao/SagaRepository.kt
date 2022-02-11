package io.github.sirvaulterscoff.bookingservice.dao

import io.github.sirvaulterscoff.bookingservice.service.SagaRecord
import org.springframework.data.jpa.repository.JpaRepository

interface SagaRepository : JpaRepository<SagaRecord, String>