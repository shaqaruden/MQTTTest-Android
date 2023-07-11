@file:OptIn(ExperimentalSerializationApi::class)

package com.shaqaruden.mqttdemo

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.concurrent.CompletableFuture


class MainViewModel: ViewModel() {

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private val _mqttClient: Mqtt3Client = Mqtt3Client.builder()
        .identifier(UUID.randomUUID().toString())
        .serverHost("listowel-iiot-zima-46225.listech.on.ca")
        .serverPort(1883)
        .automaticReconnectWithDefaultConfig()
        .buildAsync()

    init {
        val connAckFuture: CompletableFuture<Mqtt3ConnAck> = _mqttClient.toAsync().connect()

        viewModelScope.launch {
            _mqttClient.toAsync()
                .subscribeWith()
                .topicFilter("Moriroku/Listowel/Injection/IJ-H1")
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback { publish ->
                    val message = Json.decodeFromString<Message>(publish.payloadAsBytes.toString(Charsets.UTF_8))
                    Log.d("MQTTMessage", "Received message: ${message.message}")
                    addMessage(message)
                }
                .send()
        }
    }

    private fun addMessage(message: Message) {
        Log.d("MQTTMessage", "Adding message: ${message.message}")
        scope.launch {
            _messages.add(message)
        }
    }

    fun setMessage(message: String) {
        _message.value = message
    }

    fun publishMessage() {

        val msg = Message("Android", _message.value)
        Log.d("MQTTMessage", "Sending message: ${msg.message}")

        viewModelScope.launch {
            _mqttClient.toAsync().publishWith()
                .topic("Moriroku/Listowel/Injection/IJ-H1")
                .payload(Json.encodeToString(msg).toByteArray())
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()

            _message.value = ""
        }

    }
}