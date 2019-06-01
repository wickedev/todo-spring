package dev.wicke.todo

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.netty.http.server.HttpServer


val persons: MutableList<Person> = mutableListOf(
    Person(1, "Ryan"),
    Person(2, "John")
)

object PersonRepository {
    suspend fun allPeople(): Array<Person> {
        return toAsync(*persons.toTypedArray()).await()
    }

    suspend fun getPeople(id: Int): Person {
        val person = persons.find { it.id == id }
        return toAsync(person!!).await()
    }

    suspend fun deletePeople(id: Int): Unit = GlobalScope.async {
        val remove = persons.find { it.id == id }
        persons.remove(remove)
        return@async
    }.await()
}

object PersonController {
    suspend fun getAll(request: ServerRequest): Array<Person> {
        return PersonRepository.allPeople()
    }

    suspend fun get(request: ServerRequest): Person {
        val id = request.pathVariable("id").toInt()
        return PersonRepository.getPeople(id)
    }

    suspend fun delete(request: ServerRequest) {
        val id = request.pathVariable("id").toInt()
        PersonRepository.deletePeople(id)
    }
}

fun routingFunction() = routing {
    GET(PersonController::getAll)
    GET("/{id}", PersonController::get)
    DELETE("/{id}", PersonController::delete)
}

fun main() {
    val routing = routingFunction()
    val handler = toHttpHandler(routing)
    val adapter = ReactorHttpHandlerAdapter(handler)

    HttpServer.create()
        .port(3000)
        .handle(adapter)
        .start()
}

