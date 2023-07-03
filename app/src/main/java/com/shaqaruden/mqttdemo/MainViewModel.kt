@file:OptIn(ExperimentalSerializationApi::class)

package com.shaqaruden.mqttdemo

import androidx.lifecycle.ViewModel
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.concurrent.CompletableFuture


class MainViewModel: ViewModel() {

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _response = MutableStateFlow("")
    val response: StateFlow<String> = _response

    private val _mqttClient: Mqtt5Client = Mqtt5Client.builder()
        .identifier(UUID.randomUUID().toString())
        .serverHost("10.5.0.58")
        .serverPort(1883)
        .automaticReconnectWithDefaultConfig()
        .buildAsync()

    init {
        val connAckFuture: CompletableFuture<Mqtt5ConnAck> = _mqttClient.toAsync().connect()

        _mqttClient.toAsync()
            .subscribeWith()
            .topicFilter("test/response")
            .qos(MqttQos.AT_LEAST_ONCE)
            .callback { publish ->
                val message = Json.decodeFromString<Message>(publish.payloadAsBytes.toString(Charsets.UTF_8))

                _response.value = message.message
            }
            .send()
    }

    fun setMessage(message: String) {
        _message.value = message
    }

    fun publishMessage() {

        val msg = Message(_message.value)

        _mqttClient.toAsync().publishWith()
            .topic("test/topic")
            .payload(Json.encodeToString(msg).toByteArray())
            .qos(MqttQos.AT_LEAST_ONCE)
            .send()
    }
}