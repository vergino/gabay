package com.example.gabay.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gabay.data.GabayRepository
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun HomeScreen(
    repo: GabayRepository,
    onPlan: (String) -> Unit,          // <-- new: tells MainActivity to open Route tab
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {

        // 1. Search Bar -> on submit, plan a route to whatever was typed
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            placeholder = { Text("Saan pupunta? / Where to?") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            trailingIcon = {
                IconButton(onClick = { if (query.isNotBlank()) onPlan(query.trim()) }) {
                    Icon(Icons.Filled.Search, "Search")
                }
            },
            singleLine = true
        )

        // 2. Popular Destinations -> tap a chip to plan
        Text(
            "Sikat na Destinasyon / Popular Destinations",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repo.popularDestinations().forEach { dest ->
                AssistChip(onClick = { onPlan(dest) }, label = { Text(dest) })
            }
        }

        // 3. Working OSMDroid map
        GabayMapView(modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 8.dp))

        // 4. Nearby Stops -> "Route" button plans from that stop
        Text(
            "Pinakamalapit na Stops / Nearby Stops",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth().height(140.dp)
                .verticalScroll(rememberScrollState()).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "GameZone Valenzuela" to "~437 m lakad / walk",
                "Bandan Terminal" to "~600 m lakad / walk",
                "OLFU Main Gate" to "~120 m lakad / walk"
            ).forEach { (name, dist) ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(name, style = MaterialTheme.typography.bodyLarge)
                            Text(dist, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { onPlan(name) }) { Text("Route") }
                    }
                }
            }
        }
    }
}

@Composable
private fun GabayMapView(modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(object : OnlineTileSourceBase(
                    "OpenStreetMapDE", 0, 18, 256, ".png",
                    arrayOf("https://tile.openstreetmap.de/"),
                    "© OpenStreetMap contributors"
                ) {
                    override fun getTileURLString(pTileIndex: Long): String {
                        val z = org.osmdroid.util.MapTileIndex.getZoom(pTileIndex)
                        val x = org.osmdroid.util.MapTileIndex.getX(pTileIndex)
                        val y = org.osmdroid.util.MapTileIndex.getY(pTileIndex)
                        return "https://tile.openstreetmap.de/$z/$x/$y.png"
                    }
                })
                setMultiTouchControls(true)
                val olfu = GeoPoint(14.6937, 121.0493)
                controller.setZoom(16.0)
                controller.setCenter(olfu)
                val marker = Marker(this)
                marker.position = olfu
                marker.title = "OLFU Valenzuela"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                overlays.add(marker)
            }
        },
        modifier = modifier
    )
}