package com.example.gabay.data

import android.content.Context
import com.example.gabay.ai.AiAssistant
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GabayRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("gabay_prefs", Context.MODE_PRIVATE)

    // ---------- Session / settings ----------
    var userName: String
        get() = prefs.getString("user_name", "") ?: ""
        set(v) { prefs.edit().putString("user_name", v).apply() }

    var consent: Boolean
        get() = prefs.getBoolean("consent", false)
        set(v) { prefs.edit().putBoolean("consent", v).apply() }

    var lang: String
        get() = prefs.getString("lang", "en") ?: "en"
        set(v) { prefs.edit().putString("lang", v).apply() }

    // ---------- Network data (prototype: built-in sample, prod: GTFS/OSM + MySQL) ----------
    val userStop = Stop("olfu", "OLFU Valenzuela Campus", "OLFU", 14.7016, 120.9830)

    val stops = listOf(
        userStop,
        Stop("game", "GameZone Valenzuela", "GameZone", 14.7050, 120.9850),
        Stop("dapdap", "Dapdap Terminal", "Dapdap", 14.7100, 120.9880),
        Stop("mcarthur", "McArthur LRT-2 Station", "McArthur", 14.7250, 120.9950, true),
        Stop("nova", "Novaliches Church", "Novaliches", 14.7400, 121.0050),
        Stop("batasan", "Batasan Grand Central", "Batasan", 14.7500, 121.0150),
        Stop("sm", "SM City Fairview", "SM Fairview", 14.7350, 121.0250),
        Stop("terminus", "Fairview Jeep Terminus", "Terminus", 14.7300, 121.0300),
        Stop("cubao", "Araneta Center Cubao", "Cubao", 14.6190, 121.0510)
    )

    // Update roadPairs to use the new lat/lon structure (logic remains the same)
    val roadPairs: List<Pair<Stop, Stop>> by lazy {
        val ids = listOf(
            "olfu" to "game", "game" to "dapdap", "dapdap" to "mcarthur",
            "mcarthur" to "nova", "nova" to "batasan",
            "mcarthur" to "sm", "sm" to "terminus", "mcarthur" to "cubao"
        )
        ids.map { (a, b) -> stops.first { it.id == a } to stops.first { it.id == b } }
    }

    private fun leg(m: Mode, a: String, b: String, min: Int, fare: Int, instr: String) =
        RouteLeg(m, stops.first { it.id == a }, stops.first { it.id == b }, min, fare, instr)

    private val routes: Map<String, List<RouteOption>> = mapOf(
        "McArthur Station" to listOf(
            RouteOption("m1", listOf(
                leg(Mode.JEEPNEY, "olfu", "mcarthur", 18, 15, "Mag-jeep mula OLFU Gate patungong McArthur (Kongoy–McArthur route)."),
                leg(Mode.WALK, "mcarthur", "mcarthur", 4, 0, "Lakad 4 min papuntang station entrance."))),
            RouteOption("m2", listOf(
                leg(Mode.UV, "olfu", "mcarthur", 12, 20, "UV Express mula OLFU, direct sa McArthur."))),
            RouteOption("m3", listOf(
                leg(Mode.JEEPNEY, "olfu", "game", 5, 13, "Mag-jeep papuntang GameZone."),
                leg(Mode.JEEPNEY, "game", "mcarthur", 15, 13, "Lumipat ng jeep patungong McArthur.")))
        ),
        "SM City Fairview" to listOf(
            RouteOption("s1", listOf(
                leg(Mode.JEEPNEY, "olfu", "mcarthur", 18, 15, "Jeep OLFU → McArthur."),
                leg(Mode.JEEPNEY, "mcarthur", "sm", 15, 15, "Transfer jeep patungong SM Fairview."),
                leg(Mode.WALK, "sm", "sm", 5, 0, "Lakad 5 min papasok sa mall."))),
            RouteOption("s2", listOf(
                leg(Mode.BUS, "olfu", "sm", 35, 20, "Bus via Commonwealth, walis sa SM Fairview.")))
        ),
        "Araneta Center Cubao" to listOf(
            RouteOption("c1", listOf(
                leg(Mode.JEEPNEY, "olfu", "mcarthur", 18, 15, "Jeep OLFU → McArthur."),
                leg(Mode.TRAIN, "mcarthur", "cubao", 15, 15, "LRT-2 mula McArthur Station, alight sa Cubao."),
                leg(Mode.WALK, "cubao", "cubao", 3, 0, "Lakad 3 min papuntang Araneta Center."))),
            RouteOption("c2", listOf(
                leg(Mode.UV, "olfu", "cubao", 45, 60, "UV Express direct sa Cubao (mas mahal, walang transfer).")))
        ),
        "Novaliches" to listOf(
            RouteOption("n1", listOf(
                leg(Mode.JEEPNEY, "olfu", "mcarthur", 18, 15, "Jeep OLFU → McArthur."),
                leg(Mode.JEEPNEY, "mcarthur", "nova", 8, 13, "Jeep McArthur → Novaliches."))),
            RouteOption("n2", listOf(
                leg(Mode.JEEPNEY, "olfu", "dapdap", 10, 13, "Jeep OLFU → Dapdap."),
                leg(Mode.JEEPNEY, "dapdap", "nova", 15, 13, "Jeep Dapdap → Novaliches.")))
        ),
        "Batasan Grand Central" to listOf(
            RouteOption("b1", listOf(
                leg(Mode.JEEPNEY, "olfu", "mcarthur", 18, 15, "Jeep OLFU → McArthur."),
                leg(Mode.JEEPNEY, "mcarthur", "nova", 8, 13, "Jeep McArthur → Novaliches."),
                leg(Mode.JEEPNEY, "nova", "batasan", 6, 13, "Jeep Novaliches → Batasan."))),
            RouteOption("b2", listOf(
                leg(Mode.JEEPNEY, "olfu", "nova", 25, 20, "Jeep OLFU → Novaliches."),
                leg(Mode.JEEPNEY, "nova", "batasan", 6, 13, "Jeep Novaliches → Batasan.")))
        ),
        "Dapdap Terminal" to listOf(
            RouteOption("d1", listOf(
                leg(Mode.JEEPNEY, "olfu", "dapdap", 10, 13, "Jeep OLFU → Dapdap Terminal."))),
            RouteOption("d2", listOf(
                leg(Mode.JEEPNEY, "olfu", "game", 5, 13, "Jeep OLFU → GameZone."),
                leg(Mode.JEEPNEY, "game", "dapdap", 6, 13, "Jeep GameZone → Dapdap.")))
        ),
        "Fairview Terminus" to listOf(
            RouteOption("f1", listOf(
                leg(Mode.JEEPNEY, "olfu", "mcarthur", 18, 15, "Jeep OLFU → McArthur."),
                leg(Mode.JEEPNEY, "mcarthur", "sm", 15, 15, "Jeep McArthur → SM Fairview."),
                leg(Mode.WALK, "sm", "terminus", 6, 0, "Lakad 6 min papuntang terminus.")))
        )
    )

    fun popularDestinations() = routes.keys.toList()

    fun findRoutes(dest: String): List<RouteOption> =
        routes[dest]?.sortedBy { it.totalMinutes } ?: emptyList()

    private val aliases: Map<String, List<String>> = mapOf(
        "McArthur Station" to listOf("mcarthur", "m c arthur", "lrt2", "lrt 2", "mckarthur"),
        "SM City Fairview" to listOf("sm fairview", "sm", "fairview mall"),
        "Araneta Center Cubao" to listOf("cubao", "araneta"),
        "Novaliches" to listOf("novaliches", "nova"),
        "Batasan Grand Central" to listOf("batasan"),
        "Dapdap Terminal" to listOf("dapdap"),
        "Fairview Terminus" to listOf("fairview terminus", "jeep terminus", "terminus"),
        "OLFU Valenzuela Campus" to listOf("olfu", "our lady of fatima", "campus", "fatima")
    )

    /** Grounded place extraction for the AI Module. */
    fun findDest(qRaw: String): String? {
        val q = qRaw.lowercase(Locale.getDefault())
        aliases.forEach { (canonical, list) ->
            if (list.any { q.contains(it) } || q.contains(canonical.lowercase())) return canonical
        }
        return null
    }

    fun nearestStops(): List<Pair<Stop, Int>> =
        stops.filter { it.id != "olfu" }
            .map { s ->
                // Simple distance calculation using lat/lon
                val dLat = s.lat - userStop.lat
                val dLon = s.lon - userStop.lon
                val distKm = kotlin.math.sqrt(dLat * dLat + dLon * dLon) * 111.0 // rough km to meters
                s to (distKm * 1000).toInt()
            }
            .sortedBy { it.second }
            .take(4)

    // ---------- Community Reports ----------
    private val seedReports = listOf(
        CommunityReport(1, ReportType.DELAY, "Quirino Highway",
            "Heavy traffic mula Fairview patungong McArthur.", "2:15 PM", true),
        CommunityReport(2, ReportType.ROUTE_CHANGE, "McArthur Station",
            "Temporary reroute ng kongoy jeep sa side road.", "1:40 PM", true),
        CommunityReport(3, ReportType.HAZARD, "Dapdap Terminal",
            "Malamig na ulan, may bahâ sa tapat ng terminal.", "12:05 PM", false)
    )

    fun reports(): List<CommunityReport> {
        val user = loadJson("reports")
        val list = seedReports.toMutableList()
        for (i in 0 until user.length()) {
            val o = user.getJSONObject(i)
            list.add(0, CommunityReport(
                o.getLong("id"), ReportType.valueOf(o.getString("type")),
                o.getString("place"), o.getString("details"),
                o.getString("time"), false
            ))
        }
        return list
    }

    fun addReport(type: ReportType, place: String, details: String) {
        val arr = loadJson("reports")
        arr.put(JSONObject().apply {
            put("id", System.currentTimeMillis())
            put("type", type.name); put("place", place); put("details", details)
            put("time", SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()))
        })
        prefs.edit().putString("reports", arr.toString()).apply()
    }

    // ---------- Trip History ----------
    fun trips(): List<TripRecord> {
        val arr = loadJson("trips")
        val list = mutableListOf(
            TripRecord("SM City Fairview", 30, 38, 5, "Yesterday 6:20 PM"),
            TripRecord("Novaliches", 28, 26, 4, "Mon 8:05 AM")
        )
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(0, TripRecord(o.getString("dest"), o.getInt("fare"),
                o.getInt("min"), o.getInt("rating"), o.getString("time")))
        }
        return list
    }

    fun addTrip(t: TripRecord) {
        val arr = loadJson("trips")
        arr.put(JSONObject().apply {
            put("dest", t.dest); put("fare", t.fare); put("min", t.minutes)
            put("rating", t.rating); put("time", t.time)
        })
        prefs.edit().putString("trips", arr.toString()).apply()
    }

    // ---------- Saved Places ----------
    fun savedPlaces(): List<SavedPlace> {
        val arr = loadJson("saved")
        val list = mutableListOf(SavedPlace("Batasan Grand Central", "Favorite"))
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(0, SavedPlace(o.getString("name"), o.getString("cat")))
        }
        return list
    }

    fun addSavedPlace(name: String, cat: String) {
        val arr = loadJson("saved")
        arr.put(JSONObject().put("name", name).put("cat", cat))
        prefs.edit().putString("saved", arr.toString()).apply()
    }

    private fun loadJson(key: String): JSONArray =
        try { JSONArray(prefs.getString(key, "[]")) } catch (e: Exception) { JSONArray() }

    // ---------- AI Accuracy Testing (Chapter 3, Testing Procedure) ----------
    fun runAiTest(): List<Pair<String, Boolean>> =
        AiAssistant.testSet.map { (q, expected) -> q to (AiAssistant.classify(q) == expected) }
}