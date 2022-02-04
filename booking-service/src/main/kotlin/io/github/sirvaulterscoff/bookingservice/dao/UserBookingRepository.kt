package io.github.sirvaulterscoff.bookingservice.dao

import io.github.sirvaulterscoff.bookingservice.model.BookingRecord
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserBookingRepository : CrudRepository<BookingRecord, String> {
    fun findByUserIdAndBookingObjectId(userId: String, bookingObjectId: String): List<BookingRecord>
}