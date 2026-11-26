package com.example.gabay.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gabay.data.GabayRepository
import com.example.gabay.data.SavedPlace

@Composable
fun ProfileScreen(repo: GabayRepository, lang: String, onLangChange: (String) -> Unit) {
    var saved by remember { mutableStateOf(repo.savedPlaces()) }
    var trips by remember { mutableStateOf(repo.trips()) }
    var consent by remember { mutableStateOf(repo.consent) }
    var showAdd by remember { mutableStateOf(false) }
    var testResults by remember { mutableStateOf<List<Pair<String, Boolean>>?>(null) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(repo.userName.ifBlank { "Commuter" },
                    style = MaterialTheme.typography.titleLarge)
                Text("Role: Commuter • ${if (consent) "Location ON" else "Location OFF"}",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Location consent", Modifier.weight(1f))
            Switch(checked = consent, onCheckedChange = { consent = it; repo.consent = it })
        }

        Spacer(Modifier.height(16.dp))
        Text("Wika / Language", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.padding(top = 6.dp)) {
            FilterChip(lang == "en", { onLangChange("en"); repo.lang = "en" },
                label = { Text("English") }, modifier = Modifier.padding(end = 8.dp))
            FilterChip(lang == "tl", { onLangChange("tl"); repo.lang = "tl" },
                label = { Text("Filipino") })
        }

        Spacer(Modifier.height(20.dp))
        Text("Mga Naka-save na Lugar / Saved Places", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.padding(top = 6.dp)) {
            saved.forEach { p ->
                AssistChip(onClick = { }, label = { Text(p.name) },
                    modifier = Modifier.padding(end = 8.dp))
            }
            TextButton(onClick = { showAdd = true }) { Text("+ Add") }
        }

        Spacer(Modifier.height(20.dp))
        Text("Trip History", style = MaterialTheme.typography.titleMedium)
        trips.forEach { t ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.padding(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(t.dest, style = MaterialTheme.typography.titleSmall)
                        Text("₱${t.fare} • ~${t.minutes} min • ${t.time}",
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Text("★".repeat(t.rating) + "☆".repeat(5 - t.rating))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        Text("AI Module — Accuracy Test (Chapter 3)", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { testResults = repo.runAiTest() },
            modifier = Modifier.fillMaxWidth()) { Text("Run AI Accuracy Test") }
        testResults?.let { res ->
            val ok = res.count { it.second }
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Intent accuracy: $ok/${res.size} (${ok * 100 / res.size}%)",
                        style = MaterialTheme.typography.titleSmall)
                    res.forEach { (q, pass) ->
                        Text((if (pass) "✓ " else "✗ ") + q,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (showAdd) {
        var name by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Save Place") },
            text = { OutlinedTextField(name, { name = it }, label = { Text("Pangalan ng lugar") }, singleLine = true) },
            confirmButton = {
                TextButton(
                    onClick = {
                        repo.addSavedPlace(name.trim(), "Saved")
                        saved = repo.savedPlaces(); showAdd = false
                    },
                    enabled = name.isNotBlank()
                ) { Text("Save") }
            }
        )
    }
}