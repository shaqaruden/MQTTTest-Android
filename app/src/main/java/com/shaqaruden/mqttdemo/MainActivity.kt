@file:OptIn(ExperimentalMaterial3Api::class)

package com.shaqaruden.mqttdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shaqaruden.mqttdemo.ui.theme.MQTTDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MQTTDemoTheme {

                val viewModel: MainViewModel = viewModel()
                val topic = viewModel.message.collectAsState()
                val messages = viewModel.messages

                Surface(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.fillMaxSize()) {
                        Column(
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        verticalArrangement = Arrangement.Bottom) {
                            messages.forEach {
                                Log.d("MQTTMessage", "Message Client: ${it.client}")
                                when(it.client) {
                                    "Android" -> {
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                modifier = Modifier
                                                    .fillMaxWidth(0.9f)
                                                    .padding(vertical = 8.dp),
                                                color = Color(0xFFC5E1A5)
                                            ) {
                                                Text(text = "You: ${it.message}", modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                                            }
                                        }
                                    }
                                    else -> {
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                modifier = Modifier
                                                    .fillMaxWidth(0.9f)
                                                    .padding(vertical = 8.dp),
                                                color = Color(0xFFECEFF1)
                                            ) {
                                                Text(
                                                    text = "${it.client}: ${it.message}",
                                                    modifier = Modifier.padding(
                                                        vertical = 8.dp,
                                                        horizontal = 16.dp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }   
                        }
                        MQTTForm(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            message = topic.value,
                            onMessageChange = { viewModel.setMessage(it) },
                            onSend = { viewModel.publishMessage() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MQTTForm(modifier: Modifier = Modifier, message: String, onMessageChange: (String) -> Unit, onSend: () -> Unit) {
    // Build a form with a text field and a button
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Message") },
            shape = RoundedCornerShape(100),
            trailingIcon = {
                IconButton(onClick = { onSend() }) {
                    Icon(imageVector = Icons.Rounded.Send, contentDescription = "")
                }
            }
        )
    }
}