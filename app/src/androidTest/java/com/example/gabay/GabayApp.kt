package com.example.gabay

import android.app.Application
import org.osmdroid.config.Configuration
import java.io.File

class GabayApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Configure osmdroid BEFORE any map is created
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        // Set a proper User-Agent (OSM requires this)
        Configuration.getInstance().userAgentValue = "com.example.gabay/1.0"

        // Enable tile caching to reduce requests
        Configuration.getInstance().osmdroidTileCache = File(
            applicationContext.cacheDir, "osmdroid"
        )
        Configuration.getInstance().tileFileSystemCacheMaxBytes = 500 * 1024 * 1024 // 500MB
        Configuration.getInstance().tileFileSystemThreads = 4
    }
}