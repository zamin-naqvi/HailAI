# 2. How It Works — Step-by-Step User Journey

## Phase 1: App Launch & Splash Screen

When the user opens Hail AI, they are greeted by a custom-built splash screen:
- The native Android 12+ splash screen is configured with a transparent icon to avoid a "double logo" flash.
- A Jetpack Compose `SplashScreen` composable takes over, animating the Hail AI logo dropping from the top of the screen using `spring()` physics with `DampingRatioMediumBouncy`.
- Simultaneously, a mathematically synthesized audio track (a 150ms exponential frequency sweep from 400Hz→1200Hz, generated as a raw byte array via Android's `AudioTrack` API) plays a crisp "water drop" sound.
- A 4-color vertical gradient animates downward behind the logo using `drawBehind` with animated `startY`/`endY` brush coordinates.
- After 3 seconds, the app navigates to the Home screen.

## Phase 2: The Home / Chat Screen (Empty State)

The Home screen is a full-screen chat interface:
- **Floating Top Bar:** A translucent pill-shaped bar at the top with the Hail AI title, a hamburger menu (opens the side drawer), a "New Chat" button, and navigation to Bookings/Settings/About.
- **Empty State:** When no messages exist, the screen shows the Urdu-inspired greeting *"Kya chahiye aaj?"* ("What do you need today?") with a subtitle *"Ask me to find any service provider near you"*.
- **Quick Action Chips:** A horizontally scrollable row of service chips — Plumber, Electrician, AC Repair, Cleaner, Tutor, Carpenter, Painter, Pest Control — each tapping pre-fills a Roman Urdu/English request.
- **Floating Input Bar:** A bottom input bar with a text field, a "+" button (opens the full services bottom sheet), and a send button.
- **Animated Gradient Background:** The background shifts between three states — `IDLE` (soft pastels at the bottom), `THINKING` (warm orange/coral moving upward), and `DONE` (settled white).

## Phase 3: User Sends a Request

The user types something like: *"Mujhe kal subah G-13 mein AC technician chahiye"* and taps Send.

1. The user's message appears as a right-aligned chat bubble.
2. A "Thinking..." indicator with animated bouncing dots appears.
3. The gradient shifts from IDLE to THINKING (warm colors rise from bottom to top).

## Phase 4: Agent Pipeline Executes (Discovery — Steps 1-3)

The `AgentOrchestrator` runs the first 3 agents sequentially:

### Step 1: NLU Agent (~2.2 seconds)
- Parses the input using keyword matching for service categories ("ac" → HVAC, "plumb" / "pani" → Plumbing, "bijli" → Electrical, etc.).
- Detects city from keywords ("lahore", "karachi", or defaults to "Islamabad").
- Detects area from a list of 16 known areas (G-13, Gulberg, DHA, F-7, E-11, etc.).
- Detects time preference ("kal" / "tomorrow" → tomorrow 10:00 AM).
- Detects urgency ("urgent" / "jaldi" → high).
- Detects language (Roman Urdu keywords like "mujhe", "chahiye" → roman_urdu).
- Outputs a structured `NluOutput` JSON.
- A compact agent step bubble appears in the chat: *"🧠 NLU Agent — Completed"*.

### Step 2: Discovery Agent (~2.5 seconds)
- First attempts a live query to Firebase RTDB via Retrofit: `GET {FIREBASE_URL}/providers.json`.
- Filters the response by `category` and `city`.
- If Firebase returns no results or the network fails, falls back to the local `MockProviders.kt` dataset (31 hardcoded providers across 8 categories and 3 cities).
- Outputs a list of candidate providers.

### Step 3: Ranking Agent (~2.0 seconds)
- Scores each candidate: `score = (rating/5.0 × 0.4) + (0.8 × 0.35) + (availabilityScore × 0.25)`.
- Sorts by descending score.
- Selects the top 3.

### Chat Output After Discovery
- The individual agent step bubbles collapse into a single **"Agent Trace Summary"** bubble (e.g., *"3 steps completed → View trace"*), tappable to navigate to the Agent Trace screen.
- Below that, an AI text message appears: *"I found 3 providers for you. Tap 'Book Now' to proceed:"*
- Three `ProviderCard` components render inline in the chat, each showing: provider name, category badge, star rating, price range (PKR), verified badge, experience years, area, and a "Book Now" button.

## Phase 5: User Books a Provider (Booking — Steps 4-6)

The user taps "Book Now" on a provider card.

1. A `BookingFlowSheet` (bottom sheet) slides up, showing the provider's full details, available time slots as tappable chips, a location summary, and a "Confirm Booking" button.
2. The user selects a time slot and taps Confirm.

The `AgentOrchestrator` runs the remaining 3 agents:

### Step 4: Booking Agent (~1.3 seconds)
- Generates a booking ID: `BK-YYYYMMDD-XXXX` (date + random 4 chars).
- Creates a `Booking` object with all details (provider info, slot, location, reminders, confirmation message, price estimate).
- Writes the booking to Firebase RTDB via Retrofit: `PUT {FIREBASE_URL}/bookings/{bookingId}.json`.
- If Firebase write fails, the booking is kept locally.

### Step 5: Notification Agent (~0.8 seconds)
- Simulates dispatching a push notification. No actual FCM integration — the step is logged as completed.

### Step 6: Follow-Up Agent (~0.7 seconds)
- Creates two `Reminder` objects: a `pre_appointment` reminder (1 hour before) and a `feedback_request` reminder (90 minutes after). These are stored as data in the booking object.

### Chat Output After Booking
- Booking step bubbles collapse into another Agent Trace Summary.
- A `BookingConfirmation` card renders in the chat with an animated checkmark, the booking ID, and all details.
- A summary text bubble: *"I've booked [Provider] for you at [time] in [location]. Reminders have been scheduled!"*
- The booking is stored in the ViewModel's `allBookings` list and persists across the session.

## Phase 6: Post-Booking Screens

- **Booking Confirmation Screen:** Tapping the booking card navigates to a full-page confirmation with animated checkmark, detail rows (Provider, Service, Time, Location, Estimate, Contact), the confirmation message in a green card, and buttons for "View Agent Trace" and "Back to Home".
- **My Bookings Screen:** Accessible from the side drawer or settings; shows all bookings with segmented tabs for Active/Completed/Cancelled.
- **Agent Trace Screen:** A vertical timeline of all 6 agent steps showing agent name, emoji, tool used, input summary, output summary, duration, and expandable raw JSON output.
