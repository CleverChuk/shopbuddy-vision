package io.shoppbuddy.vision

import com.google.cloud.vision.v1.AnnotateImageResponse
import com.google.cloud.vision.v1.EntityAnnotation
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled.buffer
import java.awt.Point

const val DEFAULT_LANG = "en"
const val Y_THRESHOLD = 5

val PRICE_REGEX = Regex("\\d{1,2}\\.\\d{2}")
val WORD_REGEX = Regex("\\b[a-zA-Z]+\\b")

val IGNORE_REGEX_EN_US =
    Regex("(Total|Grand|Sales?|Tax|Change|Payment|Credit|Debit|Balance|Due)", RegexOption.IGNORE_CASE)
val ADDRESS_REGEX_EN_US = Regex("\\d{2,}[a-zA-Z ,]+\\d{5,}")

data class VisionFragment(
    val description: String,
    val points: List<Point>,
    val maxCharAscent: Int = points.maxOf { it.y },
    val minCharDescent: Int = points.minOf { it.y },
    override var id: Int = 0,
) : UnionFindable

suspend fun MultiPartData.toByteBuff(): ByteBuf {
    val buff = buffer()
    this.forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                val fileBytes = part.streamProvider().readBytes()
                buff.writeBytes(fileBytes)
            }
            else -> {}
        }
        part.dispose()
    }
    return buff
}

fun EntityAnnotation.toVisionFragment(): VisionFragment {
    return VisionFragment(
        description = description,
        boundingPoly.verticesList
            .map { Point(it.x, it.y) }
            .sortedWith { point1, point2 -> point2.y - point1.y }
            .sortedWith { point1, point2 -> point2.x - point1.x }
    )
}

fun AnnotateImageResponse.toItems(): List<Item> {
    if (textAnnotationsList.size < 2) return emptyList()
    val entityAnnotations = this.textAnnotationsList.subList(1, textAnnotationsList.size)
    val unionFind = UnionFind<VisionFragment>(textAnnotationsList.size - 1)

    val visionFragments = entityAnnotations
        .asSequence()
        .map(EntityAnnotation::toVisionFragment)
        .sortedBy { visionFragment -> visionFragment.points[0].y }
        .mapIndexed { id, frag ->
            frag.id = id
            frag
        }.toList()

    for (i in visionFragments.indices) {
        for (j in i + 1 until visionFragments.size) {
            unionFind.union(visionFragments[i], visionFragments[j]) { first, second ->
                second.minCharDescent - first.minCharDescent < Y_THRESHOLD
            }
        }
    }

    val lines = visionFragments
        .groupBy { it.id }
        .map { (_, words) ->
            words.sortedBy { it.points[0].x }
                .map { it.description }
        }

    val store = StringBuilder()
    val location = StringBuilder()
    var address: String? = null

    for (line in lines) {
        val price = line.filter { PRICE_REGEX.matches(it) }.joinToString("")
        // less is better
        if (line.size <= 2 && store.isEmpty() && WORD_REGEX.find(line.joinToString("")) != null) {
            store.append(line.joinToString(" "))
        }
        if (price.isNotEmpty()) {
            val locationString = location.toString()
            val targetAddress = ADDRESS_REGEX_EN_US.find(locationString)
            address = targetAddress?.value

            if (store.isEmpty()) {
                val range: IntRange? = targetAddress?.range
                WORD_REGEX.findAll(locationString.subSequence(0, range?.first ?: locationString.length))
                    .fold(store) { acc, matchResult ->
                        acc.append(matchResult.value)
                            .append(" ")
                    }
            }
            break
        } else location.append(line.joinToString(" "))
            .append(" ")
    }

    var prev = emptyList<String>()
    return lines
        .mapNotNull { line ->
            val price = line.filter { PRICE_REGEX.matches(it) }.joinToString("")
            val description = prev.plus(line).filter { WORD_REGEX.matches(it) && it.length > 1 }.joinToString(" ")
            val ignore = IGNORE_REGEX_EN_US.find(description) != null

            if (!ignore && price.isNotEmpty() && description.isNotEmpty()) {
                prev = emptyList()
                Item(
                    description,
                    address ?: "",
                    store.toString(),
                    price.toDoubleOrNull() ?: return@mapNotNull null
                )
            } else {
                prev = line
                null
            }
        }
}