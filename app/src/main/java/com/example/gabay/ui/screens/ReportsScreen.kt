package com.example.gabay.screens

import androidx.compose.foundation.horizontalScroll
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
import com.example.gabay.data.*

@Composable
fun ReportsScreen(repo: GabayRepository) {
    var reports by remember { mutableStateOf(repo.reports()) }
    var showForm by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(
            "Ang mga report ay sine-validate ng Admin bago ipakita sa ibang komuter.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = { showForm = true }, modifier = Modifier.fillMaxWidth()) {
            Text("+ Mag-report / Add Report")
        }
        Spacer(Modifier.height(16.dp))
        reports.forEach { r ->
            ReportCard(r); Spacer(Modifier.height(8.dp))
        }
    }

    if (showForm) {
        ReportForm(
            onDismiss = { showForm = false },
            onSubmit = { type, place, details ->
                repo.addReport(type, place, details)
                reports = repo.reports(); showForm = false
            }
        )
    }
}

@Composable
private fun ReportCard(r: CommunityReport) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(r.type.label.uppercase(), style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (r.verified) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        if (r.verified) "VERIFIED" else "PENDING",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(r.timeAgo, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(4.dp))
            Text(r.place, style = MaterialTheme.typography.titleSmall)
            Text(r.details, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportForm(onDismiss: () -> Unit, onSubmit: (ReportType, String, String) -> Unit) {
    var type by remember { mutableStateOf(ReportType.DELAY) }
    var place by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Community Report") },
        text = {
            Column {
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    ReportType.values().forEach { t ->
                        FilterChip(type == t, { type = t },
                            label = { Text(t.label) }, modifier = Modifier.padding(end = 6.dp))
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(place, { place = it },
                    label = { Text("Location / Lugar") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(details, { details = it },
                    label = { Text("Details") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmit(type, place.trim(), details.trim()) },
                enabled = place.isNotBlank() && details.isNotBlank()
            ) { Text("Submit") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}