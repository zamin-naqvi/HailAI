# 3. Architecture & Dependencies

## Architectural Pattern: MVVM + Unidirectional Data Flow

Hail AI follows the **Model-View-ViewModel (MVVM)** pattern with strict Unidirectional Data Flow (UDF).

### Model Layer
The data layer is organized into three packages:
- **`data/model/`** — Contains all Kotlin data classes:
  - `Provider.kt` — Represents a service provider (id, name, category, subcategory, phone, area, city, lat/lng, rating, reviews, price range, available days/slots, verified status, experience, languages, photo URL). Uses `@Serializable` and `@SerialName` for Kotlinx.Serialization.
  - `Booking.kt` — Represents a confirmed booking (bookingId, providerId, providerName, serviceType, status enum, slotDatetime, location, createdAt, agentTraceId, reminders, confirmationMessage, etc.). Includes `BookingStatus` enum (`PENDING → CONFIRMED → REMINDER_SENT → IN_PROGRESS → COMPLETED / CANCELLED / DISPUTED`) and `Reminder` data class.
  - `AgentStep.kt` — Represents a single step in the agent pipeline (stepNumber, agentName, agentEmoji, toolUsed, status enum, inputSummary, outputSummary, outputJson, durationMs). Includes 6 predefined `agentStepTemplates`.
  - `ChatMessage.kt` — Represents a chat message (id, content, type enum, timestamp, optional agentStep/providers/booking/completedSteps). `MessageType` enum: `USER`, `AI_TEXT`, `AI_THINKING`, `AI_AGENT_STEP`, `AI_PROVIDER_CARD`, `AI_BOOKING_CONFIRM`, `AI_TRACE_SUMMARY`, `SYSTEM`.
- **`data/mock/`** — Contains `MockProviders.kt`: a singleton object holding 31 hardcoded `Provider` instances across 8 service categories and 3 Pakistani cities, with search/filter methods.
- **`data/network/`** — Contains `HailAiApiService.kt`: a Retrofit interface defining two endpoints:
  - `GET providers.json` — Fetches all providers from Firebase RTDB.
  - `PUT bookings/{bookingId}.json` — Writes a booking to Firebase RTDB.
  - Companion object creates the Retrofit instance with OkHttp logging interceptor, Kotlinx.Serialization converter, and base URL loaded from `BuildConfig.FIREBASE_URL` (stored in `local.properties`, not committed to git).

### ViewModel Layer
- **`HomeViewModel.kt`** — The central state manager. Holds `StateFlow`s for: messages list, processing flag, input text, current booking, agent steps list, all bookings list, conversation snapshots (for drawer history), and hasFirstResponse flag. Orchestrates Phase 1 (discovery) and Phase 2 (booking) by calling `AgentOrchestrator` methods and updating the message list in real-time as agent steps stream in.

### View Layer
The UI is 100% Jetpack Compose with no XML layouts.
- **10 screens** in `ui/screens/`: splash, home, booking, mybookings, trace, providers, settings, about, privacy (with sub-screens), location.
- **10 custom components** in `ui/components/`: ChatBubble, ProviderCard, FloatingInputBar, FloatingTopBar, GradientBackground, HailBottomSheet, BookingFlowSheet, QuickActionChips, SideDrawer, TypingIndicator.
- **Design system** in `ui/theme/`: Color.kt (full semantic palette), Type.kt, Shape.kt, Theme.kt.

### Agent Layer
- **`agent/AgentOrchestrator.kt`** — The core brain. Split into two phases:
  - `discoverProviders()` — Runs NLU → Discovery → Ranking (Steps 1-3), returns providers.
  - `bookProvider()` — Runs Booking → Notification → Follow-Up (Steps 4-6), returns confirmed booking.
  - Each step emits status updates via a `onStepUpdate` callback, which the ViewModel uses to stream step progress into the chat.

---

## Dependencies

| Dependency | Purpose | Why This Choice |
|---|---|---|
| **Jetpack Compose** | Entire UI | Declarative, reactive, eliminates XML boilerplate |
| **Compose Animation** | Spring physics, fade, slide transitions | Smooth 60fps UI transitions |
| **Retrofit2 + OkHttp** | Network layer (Firebase REST API) | Lightweight — avoids the heavy official Firebase SDK (which bundles Analytics, Crashlytics, etc.) |
| **Kotlinx.Serialization** | JSON parsing | Kotlin-native, faster than Gson, compile-time safe |
| **Navigation Compose** | Screen routing with animated transitions | Single-activity architecture, type-safe routes |
| **Compose Icons — Feather** | All icons throughout the app | Clean, consistent icon set (ArrowLeft, User, Tool, Clock, MapPin, Phone, etc.) |
| **Coil Compose** | Async image loading | Kotlin-first, lightweight, Compose-native |
| **Lottie Compose** | Animated illustrations | Rich JSON-based animations |
| **AndroidX Core SplashScreen** | Native splash screen API (Android 12+) | Backward-compatible cold-start handling |
| **Lifecycle ViewModel Compose** | ViewModel + StateFlow integration | `collectAsStateWithLifecycle()` for lifecycle-aware state observation |

---

## Navigation Architecture

All screens are defined in `NavGraph.kt` with string routes:
- `splash` → `home` → `booking/{bookingId}`, `my_bookings`, `agent_trace`, `settings`, `about`, `privacy`, `privacy_policy`, `terms`, `licenses`, `location`.
- Custom animated transitions: enter slides from right, exit slides left, pop-enter slides from left, pop-exit slides right. Duration: 340ms slide + 200ms fade.

---

## Project Structure

```
com.aeroloomstudio.hailai/
├── MainActivity.kt                    # Single-activity entry point
├── agent/
│   └── AgentOrchestrator.kt           # 6-agent pipeline (352 lines)
├── data/
│   ├── mock/
│   │   └── MockProviders.kt           # 31 hardcoded providers (323 lines)
│   ├── model/
│   │   ├── AgentStep.kt               # Step data class + 6 templates
│   │   ├── Booking.kt                 # Booking + status enum + reminder
│   │   ├── ChatMessage.kt             # Message types for chat UI
│   │   └── Provider.kt                # Provider data class
│   └── network/
│       └── HailAiApiService.kt        # Retrofit interface for Firebase REST
└── ui/
    ├── components/
    │   ├── BookingFlowSheet.kt         # Bottom sheet for booking confirmation
    │   ├── ChatBubble.kt              # Polymorphic chat bubble (7 types)
    │   ├── FloatingInputBar.kt        # Bottom input with +, text, send
    │   ├── FloatingTopBar.kt          # Translucent top bar with action pills
    │   ├── GradientBackground.kt      # 3-state animated gradient
    │   ├── HailBottomSheet.kt         # Services grid bottom sheet
    │   ├── ProviderCard.kt            # Rich provider card with Book Now
    │   ├── QuickActionChips.kt        # Service category chips
    │   ├── SideDrawer.kt              # Navigation drawer
    │   └── TypingIndicator.kt         # Animated bouncing dots
    ├── navigation/
    │   └── NavGraph.kt                # All routes + animated transitions
    ├── screens/
    │   ├── about/                     # About screen
    │   ├── booking/                   # Booking confirmation + animated checkmark
    │   ├── home/                      # HomeScreen + HomeViewModel
    │   ├── location/                  # Map screen
    │   ├── mybookings/                # Active/Completed/Cancelled tabs
    │   ├── privacy/                   # Privacy, Terms, Licenses sub-screens
    │   ├── providers/                 # Provider list screen
    │   ├── settings/                  # Settings screen
    │   ├── splash/                    # Physics splash + audio synthesis
    │   └── trace/                     # Agent trace timeline
    └── theme/
        ├── Color.kt                   # Full semantic color palette
        ├── Shape.kt                   # CardShape, PillShape
        ├── Theme.kt                   # Material 3 theme
        └── Type.kt                    # Typography scales
```
