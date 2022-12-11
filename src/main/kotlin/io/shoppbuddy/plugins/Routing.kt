package io.shoppbuddy.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.shoppbuddy.vision.ReceiptVisionService
import io.shoppbuddy.vision.toByteBuff
import org.koin.ktor.ext.inject

fun Application.configureReceiptRoute() {
    val receiptVisionService by inject<ReceiptVisionService>()
    routing {
        post("/receipt") {
            val multipartData = call.receiveMultipart()
            val buff = multipartData.toByteBuff()
            call.respond(receiptVisionService.parseReceipt(buff.array(), null))
        }
    }
    routing {
        post("/receipt/{lang}") {
            val multipartData = call.receiveMultipart()
            val buff = multipartData.toByteBuff()
            call.respond(receiptVisionService.parseReceipt(buff.array(), call.parameters["lang"]))
        }
    }
}

fun Application.configureBarcodeRoute() {
    routing {
        post("/barcode") {
            val multipartData = call.receiveMultipart()
            val buff = multipartData.toByteBuff()
        }
    }
}
