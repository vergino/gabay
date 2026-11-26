package com.example.gabay.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gabay.data.GabayRepository
import com.example.gabay.ui.theme.GabayBlue

@Composable
fun LoginScreen(repo: GabayRepository, onLoggedIn: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var consent by remember { mutableStateOf(repo.consent) }

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("GABAY", style = MaterialTheme.typography.headlineLarge,
                color = GabayBlue, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("AI Location Assistance para sa mga Komuter",
                style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Pangalan / Name") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = consent, onCheckedChange = { consent = it })
                Text("Payag sa location data / Consent to location use",
                    style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    repo.userName = name.trim(); repo.consent = consent; onLoggedIn()
                },
                enabled = name.isNotBlank() && consent,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Simulan ang Biyahe / Start") }
        }
    }
}