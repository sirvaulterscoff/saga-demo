package io.github.sirvaulterscoff.bookingservice.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class BookingRecord(
    @Id
    val id: String,
    val amount: Long,
    val userId: String,
    val bookingObjectId: String,
    @Column(name  = "dt_from")
    val from: String,
    @Column(name  = "dt_to")
    val to: String
)
