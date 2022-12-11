package io.shoppbuddy.vision

import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.AnnotateImageResponse
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.ImageContext
import com.google.protobuf.ByteString
import org.koin.core.component.KoinComponent

class VisionService(private val imageAnnotatorClient: ImageAnnotatorClient) : KoinComponent {
    fun parseImage(byteArray: ByteArray, language: String): List<AnnotateImageResponse> {
        val imgBytes: ByteString = ByteString.copyFrom(byteArray)
        val requests = mutableListOf<AnnotateImageRequest>()
        val img: Image = Image.newBuilder()
            .setContent(imgBytes)
            .build()

        val feature: Feature = Feature.newBuilder()
            .setType(Type.DOCUMENT_TEXT_DETECTION)
            .build()

        val imageContext = ImageContext.newBuilder()
            .addLanguageHints(language)
            .build()

        val request: AnnotateImageRequest = AnnotateImageRequest.newBuilder()
            .addFeatures(feature)
            .setImageContext(imageContext)
            .setImage(img)
            .build()

        requests.add(request)
        val response: BatchAnnotateImagesResponse = imageAnnotatorClient.batchAnnotateImages(requests)
        return response.responsesList
    }
}