# 4. Agents and AI Pipeline

## Overview

The core intelligence of Hail AI is a 6-agent sequential orchestration pipeline implemented natively in Kotlin inside `AgentOrchestrator.kt`. The pipeline is split into two phases:

- **Phase 1 — Discovery (Steps 1-3):** NLU → Discovery → Ranking → Returns top 3 providers.
- **Phase 2 — Booking (Steps 4-6):** Booking → Notification → Follow-Up → Returns confirmed booking.

Each agent step emits real-time status updates (`PENDING` → `RUNNING` → `COMPLETED`) via a suspend callback, which the `HomeViewModel` uses to stream live progress into the chat UI.

---

## Agent 1: NLU Agent

**Tool Used:** Local keyword-based parser (simulating Gemini 3.1 Pro)
**Duration:** ~2.2 seconds (simulated with delay)

**What it does:**
- **Service Type Detection:** Scans the input for keywords mapped to 8 categories:
  - `"ac"` / `"cooling"` / `"hvac"` → AC Technician
  - `"plumb"` / `"pipe"` / `"pani"` / `"water"` → Plumber
  - `"electr"` / `"bijli"` / `"light"` → Electrician
  - `"clean"` / `"safai"` / `"maid"` → Home Cleaner
  - `"tutor"` / `"teacher"` / `"padhai"` → Home Tutor
  - `"carpenter"` / `"furniture"` / `"wood"` → Carpenter
  - `"paint"` / `"color"` → Painter
  - `"pest"` / `"bug"` → Pest Control
- **City Detection:** `"lahore"` → Lahore, `"karachi"` → Karachi, default → Islamabad.
- **Area Detection:** Scans for 16 known areas: G-13, Gulberg, G-9, F-7, E-11, G-10, I-10, F-11, H-13, DHA, Clifton, Johar Town, Model Town, Blue Area, PECHS, Bahria Town.
- **Time Detection:** `"tomorrow"` / `"kal"` → next day 10:00 AM. Otherwise → current time.
- **Urgency Detection:** `"urgent"` / `"jaldi"` → high. Otherwise → normal.
- **Language Detection:** Presence of Roman Urdu keywords (`"mujhe"`, `"chahiye"`, `"kaam"`, `"karwana"`, etc.) → `roman_urdu`. Otherwise → `english`.

**Output:** `NluOutput` data class with: serviceType, serviceCategory, locationRaw, city, timePreference, timeNormalized (ISO 8601), urgency, confidence (0.95), originalLanguage.

---

## Agent 2: Discovery Agent

**Tools Used:** `realtime_db_query_tool` (Retrofit GET), `MockProviders.searchProviders()`
**Duration:** ~2.5 seconds

**What it does:**
1. Calls `apiService.getProviders()` — a Retrofit `GET providers.json` to the Firebase Realtime Database.
2. Filters the response by `category` (from NLU) and `city` (from NLU).
3. If the filtered list is non-empty, uses it.
4. If empty or if the network call throws an exception, falls back to `MockProviders.searchProviders(category, city, area)` — which filters the 31 hardcoded local providers.

**Output:** List of candidate `Provider` objects.

---

## Agent 3: Ranking Agent

**Tools Used:** `scoring_tool` (local weighted formula)
**Duration:** ~2.0 seconds

**What it does:**
- For each candidate provider, computes:
  - `ratingScore = provider.rating / 5.0`
  - `proximityScore = 0.8` (fixed; real GPS distance calculation is not implemented)
  - `availabilityScore = if (provider.availableSlots.isNotEmpty()) 1.0 else 0.0`
  - `score = (ratingScore × 0.4) + (proximityScore × 0.35) + (availabilityScore × 0.25)`
- Sorts candidates by descending score.
- Returns top 3.

**Output:** `DiscoveryResult` containing the ranked providers, top provider, NLU output, and parsed metadata.

---

## Agent 4: Booking Agent

**Tools Used:** `realtime_db_write_tool` (Retrofit PUT)
**Duration:** ~1.3 seconds

**What it does:**
1. Generates a booking ID: `BK-YYYYMMDD-XXXX` (date stamp + 4 random uppercase characters from UUID).
2. Constructs a `Booking` object with:
   - Provider details (name, phone, rating, price estimate)
   - Selected time slot
   - Location (area + city)
   - Confirmation message text
   - Two `Reminder` objects: `pre_appointment` at 09:00 and `feedback_request` at 11:30
   - `BookingStatus.CONFIRMED`
3. Calls `apiService.createBooking(bookingId, booking)` — a Retrofit `PUT bookings/{bookingId}.json` to Firebase RTDB.
4. If the network call fails, keeps the local booking object.

**Output:** `BookingResult` with the confirmed `Booking`.

---

## Agent 5: Notification Agent

**Tools Used:** `fcm_notify_tool` (simulated)
**Duration:** ~0.8 seconds

**What it does:**
- Simulates sending a push notification. No actual FCM SDK is integrated.
- The step completes with output: *"Simulated push notification dispatched"*.

---

## Agent 6: Follow-Up Agent

**Tools Used:** `cloud_tasks_tool`, `state_machine_tool` (simulated)
**Duration:** ~0.7 seconds

**What it does:**
- Simulates scheduling two Cloud Tasks:
  - A pre-appointment reminder (1 hour before the slot).
  - A post-completion feedback request (90 minutes after).
- The reminders are stored as `Reminder` data objects inside the `Booking` but are not actually scheduled via Google Cloud Tasks.

---

## Agent Step Templates

Each agent has a predefined template in `agentStepTemplates` (in `AgentStep.kt`) containing:
- Step number (1-6)
- Agent name and emoji (🧠 NLU, 🔍 Discovery, 📊 Ranking, 📝 Booking, 🔔 Notification, ⏰ Follow-Up)
- Tool used string
- Default input/output summaries
- Sample JSON output for the trace view

---

## Pipeline Summary Table

| # | Agent | Tool | Duration | Real or Mock | Input | Output |
|---|---|---|---|---|---|---|
| 1 | 🧠 NLU | Keyword parser | ~2.2s | Mock | Raw user text | Structured `NluOutput` JSON |
| 2 | 🔍 Discovery | Retrofit GET + MockProviders | ~2.5s | Hybrid (real Firebase + mock fallback) | NLU output | List of candidate providers |
| 3 | 📊 Ranking | Weighted scoring formula | ~2.0s | Real logic | Candidate list | Top 3 ranked providers |
| 4 | 📝 Booking | Retrofit PUT | ~1.3s | Real (Firebase write) | Selected provider + slot | Confirmed `Booking` object |
| 5 | 🔔 Notification | Simulated FCM | ~0.8s | Mock | Booking data | Delivery status |
| 6 | ⏰ Follow-Up | Simulated Cloud Tasks | ~0.7s | Mock | Booking ID + time | Scheduled reminders |

---

## Why Mock Instead of Live LLM?

For this hackathon proof-of-concept:
- Vertex AI / Gemini API integration was not completed due to time constraints.
- The NLU parsing is implemented as a local keyword matcher to demonstrate the agent architecture without network latency or API key management overhead.
- The agent step structure, data flow, and trace logging are production-ready and can be trivially swapped to call a live Gemini endpoint by replacing the keyword parsing in Step 1 with a `GenerativeModel.generateContent()` call.

---

## Reasoning Flow Example

```
User: "Mujhe kal subah G-13 mein AC technician chahiye"

Step 1 [NLU Agent]:
  → Detected: Roman Urdu
  → service_type: "AC Technician", category: "hvac"
  → location: "G-13, Islamabad"
  → time: "tomorrow morning" → 2026-05-21T10:00:00

Step 2 [Discovery Agent]:
  → Tool call: apiService.getProviders() (Firebase RTDB GET)
  → Filtered by category="hvac", city="Islamabad"
  → Fallback: MockProviders.searchProviders("hvac", "Islamabad", "G-13")
  → Found: 3 providers in Islamabad

Step 3 [Ranking Agent]:
  → Scored 3 providers
  → Ali AC Services: (4.7/5 × 0.4) + (0.8 × 0.35) + (1.0 × 0.25) = 0.906
  → Cool Breeze AC: (4.5/5 × 0.4) + (0.8 × 0.35) + (1.0 × 0.25) = 0.890
  → Royal AC Services: (4.2/5 × 0.4) + (0.8 × 0.35) + (1.0 × 0.25) = 0.866
  → Top pick: Ali AC Services (score: 0.906)

Step 4 [Booking Agent]:
  → Booking ID: BK-20260521-A7F2
  → apiService.createBooking("BK-20260521-A7F2", booking) (Firebase RTDB PUT)
  → Confirmation: "Booking confirmed! Ali AC Services will arrive on 2026-05-21 at 09:00."

Step 5 [Notification Agent]:
  → Simulated push notification dispatched

Step 6 [Follow-Up Agent]:
  → Reminders scheduled: pre_appointment at 09:00, feedback at 11:30
  → Booking state: CONFIRMED
```
