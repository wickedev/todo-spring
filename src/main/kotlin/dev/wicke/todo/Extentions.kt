package dev.wicke.todo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.netty.http.server.HttpServer
import java.util.concurrent.CountDownLatch
import kotlin.reflect.KSuspendFunction1

fun <T> async(block: suspend CoroutineScope.() -> T?) = GlobalScope
    .mono(GlobalScope.coroutineContext) {
        val t = block.invoke(this)
        ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(BodyInserters.fromObject(t!!)).awaitFirst()
    }

fun HttpServer.start() {
    val latch = CountDownLatch(1)
    val server = bindNow()
    println("Server is running at port ${server.port()}")
    latch.await()
    server.disposeNow()
}

inline fun <reified T> toAsync(vararg data: T): Deferred<Array<T>> {
    return GlobalScope.async {
        return@async arrayOf(*data)
    }
}

inline fun <reified T> toAsync(data: T): Deferred<T> {
    return GlobalScope.async {
        return@async data
    }
}


class Route(var routeBuilder: RouterFunctions.Builder) {
    fun <T> GET(
        suspendFunction: KSuspendFunction1<ServerRequest, T>
    ) {
        this.GET("/", suspendFunction)
    }


    fun <T> GET(
        pattern: String,
        suspendFunction: KSuspendFunction1<ServerRequest, T>
    ) {
        this.routeBuilder = routeBuilder.GET(pattern) {
            async { suspendFunction(it) }
        }
    }

    fun <T> DELETE(
        suspendFunction: KSuspendFunction1<ServerRequest, T>
    ) {
        this.DELETE("/", suspendFunction)
    }

    fun <T> DELETE(
        pattern: String,
        suspendFunction: KSuspendFunction1<ServerRequest, T>
    ) {
        this.routeBuilder = routeBuilder.DELETE(pattern) {
            async { suspendFunction(it) }
        }
    }

    fun build(): RouterFunction<ServerResponse> {
        return this.routeBuilder.build()
    }
}

fun routing(route: Route.() -> Unit): RouterFunction<ServerResponse> {
    return routing("/", route)
}

fun routing(String: String, route: Route.() -> Unit): RouterFunction<ServerResponse> {
    val routeBuilder = Route(RouterFunctions.route())
    route(routeBuilder)
    return RouterFunctions.nest<ServerResponse>(
        RequestPredicates.path("/"),
        routeBuilder.build()
    )
}