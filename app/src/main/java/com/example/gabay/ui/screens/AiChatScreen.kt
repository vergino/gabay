package com.example.gabay.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gabay.ai.AiAssistant
import com.example.gabay.data.ChatMessage
import com.example.gabay.data.GabayRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiChatScreen(repo: GabayRepository) {
    val msgs = remember {
        mutableStateListOf<ChatMessage>().apply { add(ChatMessage(false, AiAssistant.greeting())) }
    }
    var input by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(msgs.size) {
        if (msgs.isNotEmpty()) listState.animateScrollToItem(msgs.size - 1)
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(16.dp)
        ) { items(msgs) { ChatBubble(it) } }

        Surface(tonalElevation = 3.dp) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input, onValueChange = { input = it },
                    placeholder = { Text("Magtanong... / Ask...") },
                    singleLine = true, modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(onClick = {
                    val q = input.trim()
                    if (q.isNotEmpty()) {
                        msgs.add(ChatMessage(true, q)); input = ""
                        scope.launch {
                            delay(600)
                            msgs.add(ChatMessage(false, AiAssistant.respond(q, repo)))
                        }
                    }
                }) { Icon(Icons.AutoMirrored.Filled.Send, null) }
            }
        }
    }
}

@Composable
private fun ChatBubble(m: ChatMessage) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = if (m.fromUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (m.fromUser) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Text(m.text, Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
        }
    }
}