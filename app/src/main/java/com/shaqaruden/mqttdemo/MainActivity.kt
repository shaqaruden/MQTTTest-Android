@file:OptIn(ExperimentalMaterial3Api::class)

package com.shaqaruden.mqttdemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
                val response by viewModel.response.collectAsState()

                LaunchedEffect(response) {
                    if (response != "") {
                        Toast.makeText(this@MainActivity, response, Toast.LENGTH_LONG).show()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MQTTForm(
                        modifier = Modifier.padding(20.dp),
                        message = topic.value,
                        onMessageChange = { viewModel.setMessage(it) },
                        onSend = { viewModel.publishMessage() }
                    )
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
            label = { Text(text = "Message") }
        )
        Button(onClick = onSend, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Send")
        }
    }
}