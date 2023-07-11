package com.shaqaruden.mqttdemo

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val client: String,
    val message: String
)
