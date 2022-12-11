package io.shoppbuddy.vision

import com.google.cloud.vision.v1.ImageAnnotatorClient
import org.koin.dsl.module


val appModule = module {
    single<ImageAnnotatorClient> {
        ImageAnnotatorClient.create()
    }

    single {
        VisionService(get())
    }

    single {
        ReceiptVisionService(get())
    }
}