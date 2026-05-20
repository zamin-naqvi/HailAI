# Hail AI — Complete Technical Documentation

**Team:** AeroLoom Studio | **Hackathon:** AISeekho 2026 — Challenge 2: AI Service Orchestrator for Informal Economy

---

## 1. Problem and Solution

### The Core Problem
Pakistan's informal service economy — plumbers, electricians, AC technicians, tutors, maids, carpenters, painters, pest control — relies entirely on word-of-mouth and WhatsApp groups. There is no intelligent matching, no scheduling, no accountability, and no transparency. Users must manually search, call, negotiate, and hope for the best.

### The Solution
Hail AI shifts the paradigm from **"Search and Filter"** to **"Conversational Delegation"**. The user types a natural language request in English or Roman Urdu (e.g., *"Mujhe kal subah G-13 mein AC technician chahiye"*), and a 6-agent orchestration pipeline handles intent understanding, provider discovery, ranking, booking, notification, and follow-up — all streamed live into the chat interface.

### Why It's Better
- **Zero Friction:** If you can send a text message, you can book a service. No menus, no filters, no forms.
- **Multilingual:** Handles English, Roman Urdu, and mixed-language queries natively.
- **Intelligent Matching:** Weighted scoring formula evaluates rating, proximity, and availability.
- **Full Transparency:** Agent Trace screen shows every decision, tool, and output for complete trust.
- **Automated Follow-Up:** Pre-appointment reminders and post-completion feedback requests are scheduled automatically.

---

## 2. Architecture Overview

### Pattern: MVVM + Unidirectional Data Flow
- **Model:** `Provider`, `Booking`, `AgentStep`, `ChatMessage` data classes + `MockProviders` dataset + `HailAiApiService` Retrofit interface.
- **ViewModel:** `HomeViewModel` — central state manager using `StateFlow` for reactive UI updates.
- **View:** 100% Jetpack Compose — 10 screens, 10 custom components, full design system.
- **Agent Layer:** `AgentOrchestrator` — 6-step sequential pipeline split into Discovery (1-3) and Booking (4-6) phases.

### Tech Stack
| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (100% declarative) |
| Architecture | MVVM + Repository |
| Navigation | Compose NavController (animated transitions) |
| Networking | Retrofit2 + OkHttp |
| Serialization | Kotlinx.Serialization |
| State | StateFlow + ViewModel |
| Icons | Compose Icons — Feather |
| Animations | Lottie Compose |
| Image Loading | Coil Compose |
| Splash | AndroidX Core SplashScreen |
| Backend | Firebase Realtime Database (REST API via Retrofit) |

### Why Retrofit Instead of Firebase SDK?
We intentionally avoided the official Firebase Android SDK to keep the APK lightweight. Instead, we use Retrofit to make direct REST API calls (`GET`, `PUT`) to our Firebase Realtime Database `.json` endpoints. This gives us full control over networking with OkHttp logging, keeps the dependency graph minimal, and avoids bundling Analytics, Crashlytics, and other unnecessary Firebase sub-SDKs.

---

## 3. Agent Pipeline

All 6 agents run sequentially inside `AgentOrchestrator.kt`. Each agent emits real-time status updates (`PENDING` → `RUNNING` → `COMPLETED`) via a suspend callback.

| # | Agent | Tool | Duration | What It Does |
|---|---|---|---|---|
| 1 | 🧠 NLU Agent | Local keyword parser | ~2.2s | Parses input → extracts service type, location, time, urgency, language |
| 2 | 🔍 Discovery Agent | Retrofit GET + MockProviders | ~2.5s | Queries Firebase RTDB for providers; falls back to local mock dataset |
| 3 | 📊 Ranking Agent | Weighted scoring formula | ~2.0s | Scores by `(rating×0.4)+(proximity×0.35)+(availability×0.25)`, returns top 3 |
| 4 | 📝 Booking Agent | Retrofit PUT | ~1.3s | Creates booking in Firebase RTDB, generates booking ID and confirmation |
| 5 | 🔔 Notification Agent | Simulated FCM | ~0.8s | Simulates push notification dispatch |
| 6 | ⏰ Follow-Up Agent | Simulated Cloud Tasks | ~0.7s | Schedules pre-appointment reminder and feedback request |

### NLU Agent — Keyword Mappings
| Keywords | Detected Service |
|---|---|
| `"ac"`, `"cooling"`, `"hvac"` | AC Technician |
| `"plumb"`, `"pipe"`, `"pani"`, `"water"` | Plumber |
| `"electr"`, `"bijli"`, `"light"` | Electrician |
| `"clean"`, `"safai"`, `"maid"` | Home Cleaner |
| `"tutor"`, `"teacher"`, `"padhai"` | Home Tutor |
| `"carpenter"`, `"furniture"`, `"wood"` | Carpenter |
| `"paint"`, `"color"` | Painter |
| `"pest"`, `"bug"` | Pest Control |

### Mock vs Real
- **NLU:** Mock (keyword matching, no live LLM). The architecture supports swapping in a Gemini `GenerativeModel.generateContent()` call.
- **Discovery:** Hybrid — real Firebase REST call with mock local fallback.
- **Booking:** Real — bookings are written to Firebase RTDB via Retrofit PUT.
- **Notification & Follow-Up:** Mock — simulated steps, no actual FCM or Cloud Tasks.

---

## 4. Mock Data

### Provider Dataset (`MockProviders.kt`)
- **31 providers** across 8 categories.
- **3 cities:** Islamabad (16 providers), Lahore (9 providers), Karachi (6 providers).
- **Categories:** HVAC (5), Plumbing (5), Electrical (4), Cleaning (4), Tutoring (4), Carpentry (3), Painting (3), Pest Control (3).
- Each provider has: id, name, category, subcategory, phone, area, city, lat/lng, rating (4.1–4.9), total reviews, price range (PKR), available days, available time slots, verified status, experience years, languages spoken.

### Firebase Realtime Database
- **Base URL:** Stored in `local.properties` as `FIREBASE_URL` and injected via `BuildConfig` — not committed to version control.
- **`providers.json`:** GET endpoint returning all providers (can be seeded or empty).
- **`bookings/{bookingId}.json`:** PUT endpoint for writing confirmed bookings.

### Booking State Machine
```
PENDING → CONFIRMED → REMINDER_SENT → IN_PROGRESS → COMPLETED
    ↓                                                    ↓
CANCELLED ←──────────────────────────────────────── DISPUTED
```

---

## 5. UI and UX Design

### Design Principles
- **Floating Components:** TopBar and InputBar float over content with translucent backgrounds — no traditional Android AppBar.
- **Chat-First:** Everything happens in the conversation — provider cards, bookings, agent traces are all inline.
- **Animated Gradients:** Background responds to state — idle (pastels), thinking (warm orange/coral), done (white).
- **Content-First Scrolling:** Content flows under floating bars with fade-edge scrims.

### Custom Components
| Component | File Size | Purpose |
|---|---|---|
| `BookingFlowSheet` | 25.6 KB | Bottom sheet for slot selection and booking confirmation |
| `ChatBubble` | 21.6 KB | Polymorphic bubble rendering 7 different message types |
| `ProviderCard` | 10.7 KB | Rich card with rating, price, verified badge, Book Now CTA |
| `GradientBackground` | 9.0 KB | Animated 3-state gradient wrapper (IDLE/THINKING/DONE) |
| `HailBottomSheet` | 8.0 KB | Full services grid + navigation links |
| `FloatingTopBar` | 6.8 KB | Translucent top bar with menu, title, action pills |
| `SideDrawer` | 6.2 KB | Navigation drawer with conversation history |
| `QuickActionChips` | 4.7 KB | Horizontally scrollable service category chips |
| `FloatingInputBar` | 4.2 KB | Bottom input with +, text field, send |
| `TypingIndicator` | 2.4 KB | Three animated bouncing dots |

### Color Palette
- **Primary:** HailBlue `#4285F4` — Google blue accent
- **Gradient Idle:** Soft blue → Lavender → Peach → White
- **Gradient Thinking:** Orange → Coral → Pink → White
- **Text:** Near-black `#1F1F1F`, Gray `#5F6368`, Light gray `#9AA0A6`
- **Status:** Green `#34A853`, Orange `#FBBC04`, Red `#EA4335`

### Screens
| Screen | Key Features |
|---|---|
| Splash | Spring physics icon drop + math-synthesized audio + animated gradient |
| Home/Chat | LazyColumn chat, gradient state, floating bars, fade scrims, empty state with "Kya chahiye aaj?" |
| Booking Confirmation | Animated Canvas checkmark, detail rows with Feather icons, action buttons |
| My Bookings | Segmented tabs (Active/Completed/Cancelled) |
| Agent Trace | Timeline of 6 steps with expandable JSON — critical for hackathon demo |
| Settings/About/Privacy | Clean list layouts with Feather icons |

### Splash Screen Technical Details
- **Icon Animation:** `spring(dampingRatio = MediumBouncy, stiffness = Low)` — drops from Y=-1000 to Y=0.
- **Scale Animation:** 0.1 → 1.0 over 1200ms with `FastOutSlowInEasing`.
- **Audio:** 150ms sine-wave generated via `AudioTrack` — exponential sweep from 400Hz to 1200Hz with `Math.exp(-progress * 5.0)` decay envelope. Generated entirely in code as raw byte arrays — no audio files bundled.
- **Gradient:** Animated `startY`/`endY` in `drawBehind` — 4 colors with alpha fading to white.
- **Branding:** "Developed by Aeroloom Studio" with alpha fade-in after 1.5s delay.

---

## 6. Screens & Navigation

All routes defined in `NavGraph.kt`:

```
splash → home → booking/{bookingId}
               → my_bookings
               → agent_trace
               → settings → about → privacy → privacy_policy
                                             → terms
                                             → licenses
                          → location
```

Animated transitions: enter slides from right (340ms), exit slides left, pop reverses. Duration: 340ms slide + 200ms fade.

---

## 7. How It Works — User Flow

1. **User opens app** → Custom splash with physics animation + synthesized audio.
2. **Empty state** → "Kya chahiye aaj?" greeting + quick action chips (Plumber, Electrician, etc.).
3. **User types request** → e.g., "Mujhe kal subah G-13 mein AC technician chahiye".
4. **Agent pipeline runs** → NLU parses → Discovery queries Firebase → Ranking scores top 3.
5. **Providers shown** → 3 ProviderCards inline in chat with "Book Now" buttons.
6. **User books** → BookingFlowSheet opens → selects time slot → confirms.
7. **Booking pipeline runs** → Booking written to Firebase → Notification simulated → Reminders scheduled.
8. **Confirmation shown** → Animated checkmark + booking details + "View Agent Trace".
9. **Agent Trace** → Full timeline of all 6 steps with tools, durations, and JSON output.

---

## 8. Project Structure

```
com.aeroloomstudio.hailai/
├── MainActivity.kt
├── agent/
│   └── AgentOrchestrator.kt           # 6-agent pipeline
├── data/
│   ├── mock/MockProviders.kt          # 31 hardcoded providers
│   ├── model/
│   │   ├── AgentStep.kt               # Step data + 6 templates
│   │   ├── Booking.kt                 # Booking + status enum + reminder
│   │   ├── ChatMessage.kt             # 8 message types
│   │   └── Provider.kt                # Provider data class
│   └── network/HailAiApiService.kt    # Retrofit + Firebase REST
└── ui/
    ├── components/                     # 10 custom Compose components
    ├── navigation/NavGraph.kt          # Routes + animated transitions
    ├── screens/                        # 10 screens
    └── theme/                          # Color, Type, Shape, Theme
```

---

## 9. How to Build & Run

1. Clone the repository.
2. Open in Android Studio (Ladybug or newer).
3. Sync Gradle.
4. Run on device/emulator with Android 8.0+ (API 26+).
5. No `google-services.json` required — Firebase is accessed via REST.

---

## 10. Evaluation Criteria Mapping

| Criteria | Weight | How Hail AI Addresses It |
|---|---|---|
| **Agentic Reasoning & Workflow** | 20% | 6-step sequential agent pipeline: NLU → Discovery → Ranking → Booking → Notification → Follow-Up. Each step is autonomous with traceable decisions. |
| **Matching Quality & Decision Logic** | 20% | Weighted scoring formula (rating + proximity + availability). Clear reasoning generated per recommendation. |
| **Action Simulation & Execution** | 15% | Full Firebase RTDB write (booking created), notifications simulated, reminders scheduled, state machine updated. End-to-end simulated. |
| **Technical Implementation** | 10% | Clean MVVM architecture, Kotlin + Jetpack Compose, Retrofit, Kotlinx.Serialization. Edge cases handled (no providers found, network failure, slot validation). |
| **Innovation & UX** | 10% | Multilingual (Roman Urdu/English), streaming agent status updates, animated gradients, physics-based splash with synthesized audio, Agent Trace screen for full transparency. |

---

*Built with Google Antigravity for #AISeekho2026 by AeroLoom Studio — Team Lead: Syed Zaman Abbas*
