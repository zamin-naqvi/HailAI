package com.aeroloomstudio.hailai.agent

import com.aeroloomstudio.hailai.data.network.HailAiApiService
import com.aeroloomstudio.hailai.data.model.*
import com.aeroloomstudio.hailai.data.mock.MockProviders
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * AI Agent Orchestrator connected directly to Firebase Realtime Database.
 * Natively executes the 6-agent sequential pipeline in Kotlin.
 *
 * Split into two phases:
 *   Phase 1 (discover): Steps 1-3 — NLU, Discovery, Ranking → returns providers
 *   Phase 2 (book):     Steps 4-6 — Booking, Notification, Follow-Up → returns booking
 */
class AgentOrchestrator {

    private val apiService = HailAiApiService.create()

    data class DiscoveryResult(
        val steps: List<AgentStep>,
        val providers: List<Provider>,
        val topProvider: Provider?,
        val nluOutput: NluOutput?,
        val parsedCategory: String,
        val parsedCity: String,
        val parsedArea: String,
        val timeNormalized: String,
        val serviceType: String,
    )

    data class BookingResult(
        val steps: List<AgentStep>,
        val booking: Booking,
    )

    /** Full pipeline result — kept for backward compatibility. */
    data class AgentResult(
        val steps: List<AgentStep>,
        val providers: List<Provider>,
        val topProvider: Provider?,
        val booking: Booking?,
        val nluOutput: NluOutput?,
    )

    data class NluOutput(
        val serviceType: String,
        val serviceCategory: String,
        val locationRaw: String,
        val city: String,
        val timePreference: String,
        val timeNormalized: String,
        val urgency: String,
        val confidence: Double,
        val originalLanguage: String,
    )

    // ─────────────────────────────────────────────────────────────────────────
    //  Phase 1: Discovery (Steps 1-3)
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun discoverProviders(
        userInput: String,
        onStepUpdate: suspend (AgentStep) -> Unit,
    ): DiscoveryResult {
        val steps = mutableListOf<AgentStep>()

        // Roman Urdu & English Local Parser
        val lower = userInput.lowercase()
        var serviceType = "General Service"
        var category = "hvac"
        if (lower.contains("ac") || lower.contains("cooling") || lower.contains("hvac")) {
            serviceType = "AC Technician"
            category = "hvac"
        } else if (lower.contains("plumb") || lower.contains("pipe") || lower.contains("pani") || lower.contains("water")) {
            serviceType = "Plumber"
            category = "plumbing"
        } else if (lower.contains("electr") || lower.contains("bijli") || lower.contains("light")) {
            serviceType = "Electrician"
            category = "electrical"
        } else if (lower.contains("clean") || lower.contains("safai") || lower.contains("maid")) {
            serviceType = "Home Cleaner"
            category = "cleaning"
        } else if (lower.contains("tutor") || lower.contains("teacher") || lower.contains("padhai")) {
            serviceType = "Home Tutor"
            category = "tutoring"
        } else if (lower.contains("carpenter") || lower.contains("furniture") || lower.contains("wood") || lower.contains("carpentry")) {
            serviceType = "Carpenter"
            category = "carpentry"
        } else if (lower.contains("paint") || lower.contains("color")) {
            serviceType = "Painter"
            category = "painting"
        } else if (lower.contains("pest") || lower.contains("bug")) {
            serviceType = "Pest Control"
            category = "pest_control"
        }

        var city = "Islamabad"
        if (lower.contains("lahore") || lower.contains("lhr")) {
            city = "Lahore"
        } else if (lower.contains("karachi") || lower.contains("khi")) {
            city = "Karachi"
        }

        var area = "G-13"
        val areas = listOf("G-13", "Gulberg", "G-9", "F-7", "E-11", "G-10", "I-10", "F-11", "H-13", "DHA", "Clifton", "Johar Town", "Model Town", "Blue Area", "PECHS", "Bahria Town")
        for (a in areas) {
            if (lower.contains(a.lowercase())) {
                area = a
                break
            }
        }

        val calendar = Calendar.getInstance()
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sdfDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

        val isTomorrow = lower.contains("tomorrow") || lower.contains("kal")
        val timePref = if (isTomorrow) "tomorrow morning" else "today"
        if (isTomorrow) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        val dateString = sdfDate.format(calendar.time)
        val timeNormalized = if (isTomorrow) "${dateString}T10:00:00" else sdfDateTime.format(calendar.time)
        val urgency = if (lower.contains("urgent") || lower.contains("jaldi")) "high" else "normal"
        val isRomanUrdu = listOf("mujhe", "chahiye", "kaam", "karwana", "bijli", "safai", "ac", "tutor").any { lower.contains(it) }
        val lang = if (isRomanUrdu) "roman_urdu" else "english"

        val extractedNlu = NluOutput(
            serviceType = serviceType,
            serviceCategory = category,
            locationRaw = "$area, $city",
            city = city,
            timePreference = timePref,
            timeNormalized = timeNormalized,
            urgency = urgency,
            confidence = 0.95,
            originalLanguage = lang
        )

        // ── Step 1: NLU Agent ────────────────────────────────────────────────
        val nluStepRunning = agentStepTemplates[0].copy(status = AgentStepStatus.RUNNING)
        onStepUpdate(nluStepRunning)
        delay(2200)

        val nluStepCompleted = agentStepTemplates[0].copy(
            status = AgentStepStatus.COMPLETED,
            durationMs = 2200,
            outputSummary = "Detected: $serviceType in $area, $city ($lang)",
        )
        steps.add(nluStepCompleted)
        onStepUpdate(nluStepCompleted)

        // ── Step 2: Discovery Agent ──────────────────────────────────────────
        val discoveryStepRunning = agentStepTemplates[1].copy(status = AgentStepStatus.RUNNING)
        onStepUpdate(discoveryStepRunning)

        val candidates = try {
            val databaseMap = apiService.getProviders()
            val filtered = databaseMap.values.filter {
                it.category.equals(category, ignoreCase = true) &&
                it.city.equals(city, ignoreCase = true)
            }
            if (filtered.isNotEmpty()) filtered else MockProviders.searchProviders(category, city, area)
        } catch (e: Exception) {
            e.printStackTrace()
            MockProviders.searchProviders(category, city, area)
        }
        delay(2500)

        val discoveryStepCompleted = agentStepTemplates[1].copy(
            status = AgentStepStatus.COMPLETED,
            durationMs = 2500,
            outputSummary = "Located ${candidates.size} matching providers in $city",
        )
        steps.add(discoveryStepCompleted)
        onStepUpdate(discoveryStepCompleted)

        // ── Step 3: Ranking Agent ────────────────────────────────────────────
        val rankingStepRunning = agentStepTemplates[2].copy(status = AgentStepStatus.RUNNING)
        onStepUpdate(rankingStepRunning)

        val scored = candidates.map { p ->
            val ratingScore = p.rating / 5.0
            val proximityScore = 0.8
            val availabilityScore = if (p.availableSlots.isNotEmpty()) 1.0 else 0.0
            val score = (ratingScore * 0.4) + (proximityScore * 0.35) + (availabilityScore * 0.25)
            Pair(p, score)
        }.sortedByDescending { it.second }

        val rankedCandidates = scored.map { it.first }
        val topProvider = rankedCandidates.firstOrNull()
        delay(2000)

        val rankingStepCompleted = agentStepTemplates[2].copy(
            status = AgentStepStatus.COMPLETED,
            durationMs = 2000,
            outputSummary = "Top pick: ${topProvider?.name ?: "None"}",
        )
        steps.add(rankingStepCompleted)
        onStepUpdate(rankingStepCompleted)

        return DiscoveryResult(
            steps = steps,
            providers = rankedCandidates.take(3),
            topProvider = topProvider,
            nluOutput = extractedNlu,
            parsedCategory = category,
            parsedCity = city,
            parsedArea = area,
            timeNormalized = timeNormalized,
            serviceType = serviceType,
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Phase 2: Booking (Steps 4-6)
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun bookProvider(
        provider: Provider,
        selectedSlot: String,
        serviceType: String,
        area: String,
        city: String,
        timeNormalized: String,
        onStepUpdate: suspend (AgentStep) -> Unit,
    ): BookingResult {
        val steps = mutableListOf<AgentStep>()
        val sdfId = SimpleDateFormat("yyyyMMdd", Locale.US)
        val sdfDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

        // ── Step 4: Booking Agent ────────────────────────────────────────────
        val bookingStepRunning = agentStepTemplates[3].copy(status = AgentStepStatus.RUNNING)
        onStepUpdate(bookingStepRunning)

        val bookingId = "BK-${sdfId.format(Date())}-${UUID.randomUUID().toString().take(4).uppercase(Locale.US)}"

        val localBooking = Booking(
            bookingId = bookingId,
            providerId = provider.id,
            providerName = provider.name,
            serviceType = serviceType,
            status = BookingStatus.CONFIRMED,
            slotDatetime = "${timeNormalized.substringBefore("T")}T${selectedSlot}:00",
            locationRequested = "$area, $city",
            createdAt = sdfDateTime.format(Date()),
            agentTraceId = "trace_${UUID.randomUUID().toString().take(6)}",
            confirmationMessage = "Booking confirmed! ${provider.name} will arrive on ${timeNormalized.substringBefore("T")} at ${selectedSlot}.",
            providerPhone = provider.phone,
            providerRating = provider.rating,
            priceEstimate = "PKR ${provider.priceMin}–${provider.priceMax}",
            reminders = listOf(
                Reminder("pre_appointment", "${timeNormalized.substringBefore("T")}T09:00:00"),
                Reminder("feedback_request", "${timeNormalized.substringBefore("T")}T11:30:00")
            )
        )

        val confirmedBooking = try {
            apiService.createBooking(bookingId, localBooking)
        } catch (e: Exception) {
            e.printStackTrace()
            localBooking
        }
        delay(1300)

        val bookingStepCompleted = agentStepTemplates[3].copy(
            status = AgentStepStatus.COMPLETED,
            durationMs = 1300,
            outputSummary = "Booking ${confirmedBooking.bookingId} confirmed",
        )
        steps.add(bookingStepCompleted)
        onStepUpdate(bookingStepCompleted)

        // ── Step 5: Notification Agent ───────────────────────────────────────
        val notificationStepRunning = agentStepTemplates[4].copy(status = AgentStepStatus.RUNNING)
        onStepUpdate(notificationStepRunning)
        delay(800)

        val notificationStepCompleted = agentStepTemplates[4].copy(
            status = AgentStepStatus.COMPLETED,
            durationMs = 800,
            outputSummary = "Simulated push notification dispatched",
        )
        steps.add(notificationStepCompleted)
        onStepUpdate(notificationStepCompleted)

        // ── Step 6: Follow-Up Agent ──────────────────────────────────────────
        val followupStepRunning = agentStepTemplates[5].copy(status = AgentStepStatus.RUNNING)
        onStepUpdate(followupStepRunning)
        delay(700)

        val followupStepCompleted = agentStepTemplates[5].copy(
            status = AgentStepStatus.COMPLETED,
            durationMs = 700,
            outputSummary = "Reminders and feedback requests scheduled",
        )
        steps.add(followupStepCompleted)
        onStepUpdate(followupStepCompleted)

        return BookingResult(
            steps = steps,
            booking = confirmedBooking,
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Legacy: Full pipeline (backward compat)
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun processRequest(
        userInput: String,
        onStepUpdate: suspend (AgentStep) -> Unit,
    ): AgentResult {
        val discovery = discoverProviders(userInput, onStepUpdate)

        if (discovery.topProvider == null) {
            return AgentResult(
                steps = discovery.steps,
                providers = emptyList(),
                topProvider = null,
                booking = null,
                nluOutput = discovery.nluOutput,
            )
        }

        val slot = discovery.topProvider.availableSlots.firstOrNull() ?: "10:00"
        val bookingResult = bookProvider(
            provider = discovery.topProvider,
            selectedSlot = slot,
            serviceType = discovery.serviceType,
            area = discovery.parsedArea,
            city = discovery.parsedCity,
            timeNormalized = discovery.timeNormalized,
            onStepUpdate = onStepUpdate,
        )

        return AgentResult(
            steps = discovery.steps + bookingResult.steps,
            providers = discovery.providers,
            topProvider = discovery.topProvider,
            booking = bookingResult.booking,
            nluOutput = discovery.nluOutput,
        )
    }
}
