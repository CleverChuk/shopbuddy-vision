package io.shoppbuddy.vision

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val description: String,
    val address: String,
    val store: String,
    val price: Double
)