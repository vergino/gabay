# Project Plan

Gabay: An Artificial Intelligence Integrated Location Assistance Application for Commuters

## Project Brief

# Project Brief: Gabay

**App Name:** Gabay  
**Concept:** Artificial Intelligence Integrated Location Assistance Application for Commuters  
**Target:** Minimum Viable Product (MVP)

---

## Features

1. **AI Commute & Navigation Assistant**  
   Allows commuters to input destination queries in natural language (text or voice) to receive AI-powered route suggestions, step-by-step travel instructions, and transit tips customized for local commuting options.

2. **Real-Time Location & Route Tracking**  
   Displays an interactive map view tracking the user's live position via GPS, displaying transit progress, and signaling upcoming stops or landmarks along the selected route.

3. **Context-Aware Proactive Alerts**  
   Delivers timely AI notifications for traffic delays, alternative route suggestions, and arrival/transfer warnings based on real-time location.

4. **Nearby Transit Hubs & Destination Presets**  
   Enables users to quickly discover nearby transit stops (bus, train, jeepney/puj) and save frequent destinations (e.g., Home, Work) for one-tap route generation.

---

## High-Level Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Navigation:** Jetpack Navigation 3 (state-driven navigation)
* **Adaptive Strategy:** Compose Material Adaptive library (for multi-pane and adaptive layout management across screen sizes)
* **Asynchronous & State Management:** Kotlin Coroutines, `Flow`, `StateFlow`, and Jetpack `ViewModel` (MVVM architecture with Unidirectional Data Flow)
* **Location & Maps:** Google Maps SDK for Android & Google Play Services Fused Location Provider API
* **AI Integration:** Google GenAI SDK (Gemini API) / ML Kit GenAI Prompt API for real-time commute assistance and natural language query parsing

## Implementation Steps

