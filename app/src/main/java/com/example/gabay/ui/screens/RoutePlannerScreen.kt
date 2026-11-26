package com.example.gabay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gabay.data.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RoutePlannerScreen(repo: GabayRepository, initialDest: String?) {
    var dest by remember { mutableStateOf(initialDest ?: repo.popularDestinations().first()) }
    var routes by remember { mutableStateOf<List<RouteOption>>(emptyList()) }
    var selected by remember { mutableStateOf<RouteOption?>(null) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialDest) {
        if (initialDest != null) {
            dest = initialDest
            routes = repo.findRoutes(initialDest)
            selected = null
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).background(GabayGreenDot, CircleShape))
            Spacer(Modifier.width(8.dp))
            Text("Current location: ${repo.userStop.name}",
                style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(12.dp))

        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Destination: $dest")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                repo.popularDestinations().forEach { d ->
                    DropdownMenuItem(text = { Text(d) }, onClick = {
                        dest = d; expanded = false
                        routes = repo.findRoutes(d); selected = null
                    })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { routes = repo.findRoutes(dest); selected = null },
            modifier = Modifier.fillMaxWidth()) { Text("Search Routes") }

        Spacer(Modifier.height(16.dp))
        if (routes.isEmpty()) {
            Text("Pindutin ang Search Routes para makita ang mga opsyon.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        routes.forEach { r ->
            RouteCard(r) { selected = r }
            Spacer(Modifier.height(8.dp))
        }

        selected?.let { r ->
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Text("Trip Guidance — $dest", style = MaterialTheme.typography.titleMedium)
            r.legs.forEachIndexed { i, l ->
                Row(Modifier.padding(vertical = 6.dp)) {
                    Surface(Modifier.size(28.dp), shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer) {
                        Text("${i + 1}", style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(5.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(l.mode.label, style = MaterialTheme.typography.titleSmall)
                        Text(l.instruction, style = MaterialTheme.typography.bodySmall)
                    }
                    Text("₱${l.fare}", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(8.dp))
            var showRate by remember { mutableStateOf(false) }
            var savedOk by remember { mutableStateOf(false) }
            Button(onClick = { showRate = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Complete Trip & Rate / Tapos na Biyahe")
            }
            if (savedOk) {
                Spacer(Modifier.height(8.dp))
                Text("✓ Nai-save sa Trip History", color = GabayGreenText)
            }
            if (showRate) {
                var rating by remember { mutableStateOf(5) }
                AlertDialog(
                    onDismissRequest = { showRate = false },
                    title = { Text("Rate the trip") },
                    text = {
                        Row {
                            (1..5).forEach { i ->
                                Text(if (i <= rating) "★" else "☆", fontSize = 30.sp,
                                    modifier = Modifier.clickable { rating = i }.padding(4.dp))
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            repo.addTrip(TripRecord(dest, r.fare, r.totalMinutes, rating,
                                SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())))
                            showRate = false; savedOk = true; selected = null
                        }) { Text("Save") }
                    }
                )
            }
        }
    }
}

@Composable
private fun RouteCard(r: RouteOption, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(r.modeChain, style = MaterialTheme.typography.titleSmall)
                Text("${r.transfers} transfer • ${r.walkingMinutes} min lakad",
                    style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₱${r.fare}", style = MaterialTheme.typography.titleLarge)
                Text("~${r.totalMinutes} min", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private val GabayGreenDot = Color(0xFF2E7D32)
private val GabayGreenText = Color(0xFF2E7D32)