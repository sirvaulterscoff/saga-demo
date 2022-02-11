package io.github.sirvaulterscoff.bookingservice.service

import io.github.sirvaulterscoff.bookingservice.dao.SagaRepository


fun <A: Any?> saga(
    sagaRepository: SagaRepository,
    id: String,
             block: suspend Saga.() -> SagaBuilder<A>): Saga = Saga(sagaRepository, id, block)

class Saga(
    private val sagaRepository: SagaRepository,
    private val id: String, block: suspend Saga.() -> SagaBuilder<out Any?>) {

    fun <A> step(step: String, block: suspend () -> A, compensate: suspend (A) -> Unit) : SagaBuilder<A> {
        return SagaBuilder()
    }

    fun launch() {

    }


}

class SagaBuilder<A> {
    fun <B> then(step: String, block: suspend (A) -> B, compensate: suspend (B) -> Unit) : SagaBuilder<B> {
        //do nothing
        return SagaBuilder<B>()

    }
}

data class SagaRecord (
    val id: String,
    val state : String,
    val step: String,
    val data: String,
)