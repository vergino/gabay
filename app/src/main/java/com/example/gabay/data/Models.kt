package com.example.gabay.data

enum class Mode(val label: String) {
    JEEPNEY("Jeep"), BUS("Bus"), UV("UV Express"), TRAIN("LRT"), WALK("Walk")
}

enum class ReportType(val label: String) {
    DELAY("Delay"), ROUTE_CHANGE("Route Change"), HAZARD("Hazard")
}

data class Stop(
    val id: String,
    val name: String,
    val shortName: String,
    val lat: Double,  // Real Latitude
    val lon: Double,  // Real Longitude
    val isHub: Boolean = false
)

data class RouteLeg(
    val mode: Mode, val from: Stop, val to: Stop,
    val minutes: Int, val fare: Int, val instruction: String
)

data class RouteOption(val id: String, val legs: List<RouteLeg>) {
    val totalMinutes: Int get() = legs.sumOf { it.minutes }
    val fare: Int get() = legs.sumOf { it.fare }
    val transfers: Int get() = (legs.count { it.mode != Mode.WALK } - 1).coerceAtLeast(0)
    val walkingMinutes: Int get() = legs.filter { it.mode == Mode.WALK }.sumOf { it.minutes }
    val modeChain: String get() =
        legs.filter { it.mode != Mode.WALK }.joinToString(" → ") { it.mode.label }
}

data class CommunityReport(
    val id: Long, val type: ReportType, val place: String,
    val details: String, val timeAgo: String, val verified: Boolean
)

data class ChatMessage(val fromUser: Boolean, val text: String)
data class SavedPlace(val name: String, val category: String)
data class TripRecord(
    val dest: String, val fare: Int, val minutes: Int,
    val rating: Int, val time: String
)