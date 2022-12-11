package io.shoppbuddy.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentLength)

        allowHeader(HttpHeaders.ContentType)
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}
