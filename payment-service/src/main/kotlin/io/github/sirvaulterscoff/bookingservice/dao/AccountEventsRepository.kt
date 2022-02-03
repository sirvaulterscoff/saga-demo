package io.github.sirvaulterscoff.bookingservice.dao

import io.github.sirvaulterscoff.bookingservice.model.AccountEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AccountEventsRepository : JpaRepository<AccountEvent, Long> {
    @Query("select sum(amount) from AccountEvent where account=:user")
    fun calculateBalance(@Param("user") user: String): Long?
    fun findAllByAccount(user: String): List<AccountEvent>
}