package dev.wicke.todo

import com.winterbe.expekt.should
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.spekframework.spek2.Spek

suspend fun asyncTesting() = withContext(Dispatchers.Default) {
    "Hello Kotlin Spring Webflux!".should.be.equal("Hello Kotlin Spring Webflux!")
}

object Testing : Spek({
    test("message") {
        runBlocking {
            asyncTesting()
        }
    }
})