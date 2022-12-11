package io.shoppbuddy.vision

import com.google.cloud.vision.v1.AnnotateImageResponse

class ReceiptVisionService(private val visionService: VisionService){
    fun parseReceipt(image: ByteArray, language: String?): List<Item> {
        return visionService.parseImage(image, language ?: DEFAULT_LANG)
            .map(AnnotateImageResponse::toItems)
            .flatten()
    }
}