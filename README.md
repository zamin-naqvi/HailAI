# Hail AI — AI Service Orchestrator for Pakistan's Informal Economy

**Team:** AeroLoom Studio | **Hackathon:** AISeekho 2026 — Challenge 2

*Just say it. It's done.*

---

## What is Hail AI?

Hail AI is a fully agentic, chat-based Android application that automates the entire lifecycle of informal service requests in Pakistan. A user types a natural language request — in English, Urdu, or Roman Urdu — such as *"Mujhe kal subah G-13 mein AC technician chahiye"*, and the system understands intent, finds the best nearby provider from a mock dataset, books the slot, sends a simulated notification, and schedules follow-up reminders. The entire flow is orchestrated through a 6-agent sequential pipeline built natively in Kotlin.

## The Problem

Pakistan's informal service economy — plumbers, electricians, AC technicians, tutors, maids, carpenters, painters, pest control — relies entirely on word-of-mouth and WhatsApp groups. There is:

- **No intelligent matching** between a customer's needs and a provider's skills, location, availability, or rating.
- **No scheduling or accountability** — appointments are verbal, cancellations are common, and there is no follow-up.
- **No transparency** — customers cannot compare providers, see ratings, or understand why one was recommended over another.

Existing platforms like Rozgar.pk and OLX offer static listings, but they require the user to manually search, filter, call, and negotiate. The cognitive load is immense.

## Our Solution

Hail AI shifts the paradigm from **"Search and Filter"** to **"Conversational Delegation"**. Instead of navigating menus, the user simply speaks or types their need. Six specialized AI agents handle the rest:

1. **NLU Agent** — Parses the user's natural language input, detects the language (English / Roman Urdu), extracts service type, location, time preference, and urgency.
2. **Discovery Agent** — Queries the Firebase Realtime Database (and falls back to the local mock dataset) for providers matching the extracted category and city.
3. **Ranking Agent** — Scores candidates using the formula: `score = (rating × 0.4) + (proximity × 0.35) + (availability × 0.25)` and selects the top 3.
4. **Booking Agent** — Creates a confirmed booking record, assigns a booking ID, writes it to Firebase via Retrofit REST API, and generates a confirmation message.
5. **Notification Agent** — Simulates dispatching a push notification to the user's device.
6. **Follow-Up Agent** — Schedules pre-appointment reminders and post-completion feedback requests.

The user sees all of this happen live in the chat — each agent step streams into the conversation as it completes, and a collapsible "Agent Trace" summary lets the user (or a judge) inspect exactly which agent did what, which tool it used, and what it output.

---

## Complete Technical Documentation

We have prepared comprehensive documentation explaining every aspect of the system:

1. **[Problem and Solution](docs/01_Problem_and_Solution.md)** — The core friction in Pakistan's informal economy and how our conversational interface eliminates it.
2. **[How It Works](docs/02_How_It_Works.md)** — Step-by-step breakdown of the user journey from typing a request to receiving a booking confirmation.
3. **[Architecture](docs/03_Architecture.md)** — Native Android MVVM + UDF architecture, every dependency and why it was chosen, and how the mock repository layer is decoupled.
4. **[Agents and AI Pipeline](docs/04_Agents_and_AI.md)** — Deep dive into the 6-agent orchestration pipeline, the NLU parsing logic, the ranking formula, and the Firebase write pipeline.
5. **[UI and UX Design](docs/05_UI_and_UX.md)** — Design system, custom components (FloatingTopBar, FloatingInputBar, ChatBubble, ProviderCard, BookingFlowSheet, SideDrawer, QuickActionChips, TypingIndicator, GradientBackground), and the physics-based splash screen.
6. **[Combined Documentation](docs/Combined_Documentation.md)** — Master document combining all of the above into a single reference.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (100% declarative) |
| Architecture | MVVM + Repository Pattern |
| Navigation | Compose NavController with animated transitions |
| Networking | Retrofit2 + OkHttp (REST calls to Firebase RTDB) |
| Serialization | Kotlinx.Serialization |
| State | StateFlow + ViewModel |
| Icons | Compose Icons — Feather |
| Animations | Lottie Compose |
| Image Loading | Coil Compose |
| Splash | AndroidX Core SplashScreen |
| Backend | Firebase Realtime Database (REST API via Retrofit) |

---

## Mock vs Real Infrastructure

| Component | Real or Mock | Details |
|---|---|---|
| Provider Data | **Mock** | 31 synthetic providers across 8 categories, 3 cities (Islamabad, Lahore, Karachi), generated in `MockProviders.kt` |
| NLU Agent | **Mock** | Local keyword-based parser in `AgentOrchestrator.kt` (no live LLM calls) |
| Ranking Agent | **Real logic** | Weighted scoring formula executed locally |
| Firebase RTDB | **Real** | Live REST API calls via Retrofit — providers are fetched and bookings are written to the cloud |
| Notifications | **Mock** | Simulated push notification (no FCM integration) |
| Follow-Up | **Mock** | Reminders are created as data objects but not actually scheduled via Cloud Tasks |

---

## Screens

| Screen | Route | Description |
|---|---|---|
| Splash | `splash` | Physics-based icon drop animation with synthesized audio |
| Home / Chat | `home` | Main chat interface with floating top bar, floating input bar, quick action chips, and gradient background |
| Booking Confirmation | `booking/{bookingId}` | Animated checkmark, booking details card, provider contact info |
| My Bookings | `my_bookings` | Segmented tabs: Active / Completed / Cancelled |
| Agent Trace | `agent_trace` | Timeline of all 6 agent steps with tools used, durations, and expandable JSON output |
| Settings | `settings` | App preferences and navigation to About, Privacy, Terms |
| About | `about` | App info, team, and legal links |
| Location | `location` | Map screen |

---

## Service Categories

| Category Key | Display Name | Example Provider |
|---|---|---|
| `hvac` | AC Technician | Ali AC Services (Islamabad) |
| `plumbing` | Plumber | Master Pipes Solutions (Islamabad) |
| `electrical` | Electrician | Waqas Electric (Islamabad) |
| `cleaning` | Home Cleaner / Maid | Sparkle Home Cleaners (Islamabad) |
| `tutoring` | Home Tutor | Sir Kamran Tutoring (Islamabad) |
| `carpentry` | Carpenter | WoodCraft Masters (Lahore) |
| `painting` | Painter | Perfect Finish Karachi |
| `pest_control` | Pest Control | Shield Pest Control (Islamabad) |

---

## How to Build & Run

1. Clone the repository.
2. Open in **Android Studio** (Ladybug or newer recommended).
3. Sync Gradle.
4. Build and run on a device or emulator running **Android 8.0 (API 26)** or higher.
5. No `google-services.json` is required — Firebase is accessed via raw REST API through Retrofit.

---

*Built with Google Antigravity for #AISeekho2026 by AeroLoom Studio*
*Team Lead: Syed Zaman Abbas*
