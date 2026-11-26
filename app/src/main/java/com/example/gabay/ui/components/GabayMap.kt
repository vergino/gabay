package com.example.gabay.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun GabayMap(modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            // Initialize OSMDroid safely
            Configuration.getInstance().load(
                context,
                context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
            )
            Configuration.getInstance().userAgentValue =
                "GabayApp/1.0 (com.example.gabay; student project)"

            MapView(context).apply {
                // Use OpenStreetMap.de tiles (free, no API key, no blocking)
                setTileSource(object : OnlineTileSourceBase(
                    "OpenStreetMapDE",
                    0, 18, 256, ".png",
                    arrayOf("https://tile.openstreetmap.de/"),
                    "© OpenStreetMap contributors"
                ) {
                    override fun getTileURLString(pTileIndex: Long): String {
                        val zoom = org.osmdroid.util.MapTileIndex.getZoom(pTileIndex)
                        val x = org.osmdroid.util.MapTileIndex.getX(pTileIndex)
                        val y = org.osmdroid.util.MapTileIndex.getY(pTileIndex)
                        return "https://tile.openstreetmap.de/$zoom/$x/$y.png"
                    }
                })
                setMultiTouchControls(true)

                // Center on OLFU Valenzuela
                val olfu = GeoPoint(14.6937, 121.0493)
                controller.setZoom(16.0)
                controller.setCenter(olfu)

                // Add a marker
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