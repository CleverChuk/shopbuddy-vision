package io.shoppbuddy

import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import io.shoppbuddy.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureReceiptRoute()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}