package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material3.HorizontalDivider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                val messages = remember { mutableStateListOf<Message>() }
                var input by remember { mutableStateOf("") }
                var loading by remember { mutableStateOf(false) }

                val scope = rememberCoroutineScope()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {

                    Text(
                        text = "UDP AI Chat",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )

                    HorizontalDivider()

                    // Chat history
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(messages) { msg ->
                            ChatBubble(msg)
                        }

                        if (loading) {
                            item {
                                ChatBubble(
                                    Message(
                                        isUser = false,
                                        text = "Thinking…"
                                    )
                                )
                            }
                        }
                    }

                    HorizontalDivider()

                    // Input row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ask something…") },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            enabled = !loading,
                            onClick = {
                                if (input.isBlank()) return@Button

                                val question = input
                                input = ""

                                messages.add(Message(true, question))
                                loading = true

                                scope.launch {
                                    try {
                                        val reply = withContext(Dispatchers.IO) {
                                            UdpClient.askAI(question)
                                        }
                                        messages.add(Message(false, reply))
                                    } catch (e: Exception) {
                                        messages.add(
                                            Message(false, "Error: ${e.message}")
                                        )
                                    } finally {
                                        loading = false
                                    }
                                }
                            }
                        ) {
                            Text("Send")
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ChatBubble(message: Message) {

    val bgColor = if (message.isUser)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant

    val textColor = if (message.isUser)
        Color.White
    else
        Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser)
            Arrangement.End
        else
            Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(bgColor, shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(text = message.text, color = textColor)
        }
    }
}

data class Message(val isUser: Boolean, val text: String)
