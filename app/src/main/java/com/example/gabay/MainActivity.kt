package com.example.gabay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gabay.data.GabayRepository
import com.example.gabay.screens.*
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    // Lazy so applicationContext is valid when it's first read (inside setContent)
    private val repo: GabayRepository by lazy { GabayRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            applicationContext.getSharedPreferences("osmdroid", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue =
            "GabayApp/1.0 (com.example.gabay; student project)"

        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GabayAppEntryPoint(repo = repo)
                }
            }
        }
    }
}

@Composable
fun GabayAppEntryPoint(repo: GabayRepository) {
    var isLoggedIn by remember { mutableStateOf(repo.userName.isNotBlank() || repo.consent) }

    if (!isLoggedIn) {
        LoginScreen(repo = repo, onLoggedIn = { isLoggedIn = true })
    } else {
        GabayMainScreen(repo = repo)
    }
}

@Composable
fun GabayMainScreen(repo: GabayRepository) {
    var selectedTab by remember { mutableStateOf(0) }
    var pendingDest by remember { mutableStateOf<String?>(null) }   // <-- new

    val tabs = listOf("Home", "Route", "AI", "Reports", "Profile")
    val icons = listOf(
        Icons.Filled.Home, Icons.Filled.DirectionsBus, Icons.Filled.SmartToy,
        Icons.Filled.Assessment, Icons.Filled.Person
    )

    // Single shared action: "I want to go to X" -> open Route tab with X
    val onPlan: (String) -> Unit = { dest ->
        pendingDest = dest
        selectedTab = 1
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, title ->
                    NavigationBarItem(
                        icon = { Icon(icons[i], title) },
                        label = { Text(title) },
                        selected = selectedTab == i,
                        onClick = { selectedTab = i }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen(repo, onPlan = onPlan)
                1 -> RoutePlannerScreen(repo, initialDest = pendingDest)
                2 -> AiChatScreen(repo)
                3 -> ReportsScreen(repo)
                4 -> ProfileScreen(repo, lang = repo.lang) { repo.lang = it }
            }
        }
    }
}