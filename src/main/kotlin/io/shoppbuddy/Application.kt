package io.shoppbuddy

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.shoppbuddy.plugins.*
import io.shoppbuddy.vision.appModule
import org.koin.core.context.GlobalContext.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(appModule)
    }
    EngineMain.main(args)
}

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureReceiptRoute()
    configureBarcodeRoute()
}
