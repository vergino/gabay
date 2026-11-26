package com.example.gabay.ai

import com.example.gabay.data.GabayRepository

/**
 * AI Module — lightweight NLU for English, Filipino, and Taglish.
 * Responses are grounded in repository route/fare data (no invented routes).
 */
object AiAssistant {

    enum class Intent { GREET, ROUTE, FARE, NEARBY, HELP, UNKNOWN }

    private val greetWords = listOf("hello", "hi ", "hey", "kumusta", "kamusta", "mabuhay", "good morning", "good afternoon", "good evening")
    private val nearbyWords = listOf("malapit", "nearby", "pinakamalapit", "multisan", "stop", "saan may", "saan ang jeep")
    private val fareWords = listOf("magkano", "how much", "fare", "pamasahe", "bayad", "presyo", "cost", "bayran")
    private val routeWords = listOf("route", "paano", "how do i get", "how to get", "how can i go", "pupunta", "pumunta", "get to", "directions", "daan", "biyahe", "byahe", "sakay", "pa ")
    private val helpWords = listOf("help", "tulong", "ano magagawa", "what can you do", "what can i ask")

    fun classify(raw: String): Intent {
        val q = " " + raw.lowercase() + " "
        return when {
            greetWords.any { q.contains(it) } -> Intent.GREET
            nearbyWords.any { q.contains(it) } -> Intent.NEARBY
            fareWords.any { q.contains(it) } -> Intent.FARE
            routeWords.any { q.contains(it) } -> Intent.ROUTE
            helpWords.any { q.contains(it) } -> Intent.HELP
            else -> Intent.UNKNOWN
        }
    }

    fun greeting() =
        "Kumusta! Ako si Gabay AI. 🚌 Itanong mo lang: \"Paano pumunta sa SM Fairview?\" o \"Magkano pamasahe to Cubao?\""

    fun respond(raw: String, repo: GabayRepository): String {
        val dest = repo.findDest(raw)
        return when (classify(raw)) {
            Intent.GREET -> greeting()

            Intent.ROUTE -> if (dest == null) {
                "Saan po ang pupuntahan? Halimbawa: \"Paano pumunta sa McArthur Station?\""
            } else {
                val routes = repo.findRoutes(dest)
                if (routes.isEmpty())
                    "Wala pang naka-verify na route papuntang $dest sa current coverage. Subukan: ${repo.popularDestinations().joinToString(", ")}."
                else {
                    val best = routes.minByOrNull { it.totalMinutes }!!
                    "Pinakamabilis papuntang $dest: ${best.modeChain} — ₱${best.fare}, " +
                            "humigit-kumulang ${best.totalMinutes} min, ${best.transfers} transfer, " +
                            "${best.walkingMinutes} min lakad. May ${routes.size} options sa Route Planner."
                }
            }

            Intent.FARE -> if (dest == null) {
                "Para saan ang fare? Halimbawa: \"Magkano pamasahe to Novaliches?\""
            } else {
                val routes = repo.findRoutes(dest)
                if (routes.isEmpty()) "Wala pang fare data para sa $dest."
                else "Ang pamasahe papuntang $dest ay ₱${routes.minOf { it.fare }}–₱${routes.maxOf { it.fare }}, depende sa route (jeep/bus/LRT)."
            }

            Intent.NEARBY -> {
                val n = repo.nearestStops().take(2)
                "Pinakamalapit na stops: " + n.joinToString(" at ") {
                    "${it.first.name} (~${it.second} m lakad)"
                } + "."
            }

            Intent.HELP ->
                "Kaya kong: 1) mag-recommend ng route, 2) mag-estimate ng fare, " +
                        "3) hanapin ang malapit na stops, 4) ipakita ang community reports. " +
                        "English, Filipino, o Taglish — okay lang!"

            Intent.UNKNOWN ->
                "Hmm, hindi ko sigurado. Pwede bang mas detalyado? Hal: \"Paano pumunta sa SM Fairview\" o \"Magkano fare to Cubao\"."
        }
    }

    /** AI Test Set — used by the built-in accuracy demo (Profile screen). */
    val testSet: List<Pair<String, Intent>> = listOf(
        "how do i get to mcarthur station" to Intent.ROUTE,
        "magkano pamasahe to cubao" to Intent.FARE,
        "paano pumunta sa sm fairview" to Intent.ROUTE,
        "pinakamalapit na jeep stop" to Intent.NEARBY,
        "saan may multisan" to Intent.NEARBY,
        "what can you do" to Intent.HELP,
        "kumusta" to Intent.GREET,
        "how much is the fare to novaliches" to Intent.FARE,
        "route to batasan" to Intent.ROUTE,
        "tulong" to Intent.HELP
    )
}