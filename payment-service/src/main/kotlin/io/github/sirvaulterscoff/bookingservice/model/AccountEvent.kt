package io.github.sirvaulterscoff.bookingservice.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class AccountEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_events")
    val id: Long,
    val account: String,
    val amount: Long,
    val sourceTxId: Long? = null,
    val purpose: String? = null
)
