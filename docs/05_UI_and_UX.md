# 5. UI and UX Design

## Design Philosophy

Hail AI's interface is inspired by modern conversational AI apps (Gemini, ChatGPT, Perplexity) combined with a clean, white-grid aesthetic. The design prioritizes:
- **Floating components** — Top bar and input bar float over content with translucent backgrounds, never using traditional Android AppBar/BottomBar patterns.
- **Conversational flow** — All interactions happen in-chat. Provider cards, booking confirmations, and agent traces are rendered inline as chat bubbles.
- **Animated gradients** — The background responds to app state (idle → thinking → done) with smooth color transitions.
- **Content-first scrolling** — Content flows under the floating bars with fade-edge scrims.

---

## Design System (`ui/theme/`)

### Color Palette (`Color.kt`)
- **Primary:** `HailBlue (#4285F4)` — Google blue accent used for buttons, links, and interactive elements.
- **Primary Dark:** `HailBlueDark (#1A73E8)` — For pressed states.
- **Primary Light:** `HailBlueLight (#D2E3FC)` — For chips and badges.
- **Primary Soft:** `HailBlueSoft (#E8F0FE)` — Very soft blue background.
- **Gradient Idle:** Soft blue (#D4E7FE) → Lavender (#E8D5F5) → Peach (#F5E6D3) → White.
- **Gradient Thinking:** Orange (#FFCC80) → Coral (#FFAB91) → Pink (#F8BBD0) → White.
- **Surfaces:** White (#FFFFFF), Off-white (#F8F9FA), Card (#F1F3F4), Dim (#E8EAED).
- **Text:** Near-black (#1F1F1F), Gray (#5F6368), Light gray (#9AA0A6), White-on-blue.
- **Status:** Green (#34A853), Orange (#FBBC04), Red (#EA4335) with matching light backgrounds.
- **Stars:** Gold (#FBBC04) filled, Gray (#DADCE0) empty.

### Typography (`Type.kt`)
Modern sans-serif scales overriding Android defaults. Used consistently via `MaterialTheme.typography`.

### Shapes (`Shape.kt`)
- `CardShape` — Rounded corners for cards and surfaces.
- `PillShape` — Full pill shape for buttons and badges.
Used consistently across all components.

---

## Custom Components (`ui/components/`)

### `GradientBackground.kt`
A full-screen animated gradient wrapper with three states:
- **IDLE:** Soft pastel gradient (blue → lavender → peach → white) positioned at the bottom of the screen, creating a warm welcome when the chat is empty.
- **THINKING:** Warm gradient (orange → coral → pink → white) that animates upward from the bottom, creating a visual sense of "processing" while agents are running.
- **DONE:** Gradient settles and fades, leaving a clean white workspace for the conversation.

Uses `animateColorAsState` for smooth color transitions and `Brush.verticalGradient` with animated Y offsets inside `drawBehind`.

### `FloatingTopBar.kt`
A translucent pill-shaped bar floating at the top of the screen:
- Hamburger menu icon (opens SideDrawer via `ModalNavigationDrawer`).
- "Hail AI" title text.
- Action buttons: New Chat, Services grid, Bookings, Settings, About.
- Semi-transparent white background with rounded corners — content from the LazyColumn scrolls underneath for a premium layered effect.

### `FloatingInputBar.kt`
A bottom-anchored input bar:
- "+" button to open the full services bottom sheet (`HailBottomSheet`).
- `TextField` for typing requests.
- Send button (Feather `Send` icon) — disabled while the agent pipeline is processing.
- Uses `navigationBarsPadding()` to avoid system navigation bar overlap.

### `ChatBubble.kt` (21,646 bytes — the largest component)
A polymorphic chat bubble component that renders differently based on `MessageType`:
- **`USER`** — Right-aligned bubble with blue background and white text.
- **`AI_TEXT`** — Left-aligned bubble with white/light background and dark text.
- **`AI_THINKING`** — Shows the `TypingIndicator` (three bouncing dots) with a subtle background.
- **`AI_AGENT_STEP`** — Compact inline step indicator showing agent emoji, name, and a colored status badge (green for completed, orange for running).
- **`AI_PROVIDER_CARD`** — Renders a list of `ProviderCard` components inline in the conversation.
- **`AI_BOOKING_CONFIRM`** — Renders a booking confirmation card with animated checkmark and details.
- **`AI_TRACE_SUMMARY`** — Collapsed summary showing "X steps completed → View trace", tappable to navigate to the Agent Trace screen.

### `ProviderCard.kt`
A rich card displaying:
- Provider name and category badge (e.g., "AC Technician").
- Star rating (using `StarFilled` and `StarEmpty` colors).
- Price range in PKR (e.g., "PKR 800–1200").
- Verified badge (green checkmark if `verified = true`).
- Experience years and service area.
- **"Book Now" CTA button** — turns gray and shows "Booked" after the user books that provider, using `bookedProviderIds` tracking.

### `BookingFlowSheet.kt` (25,602 bytes — the second largest component)
A modal bottom sheet for the booking confirmation flow:
- Provider header with name, rating, category.
- Available time slots rendered as tappable chips (from `provider.availableSlots`).
- Location summary (area + city).
- Price estimate.
- "Confirm Booking" primary button that triggers `viewModel.bookProvider()`.

### `QuickActionChips.kt`
A horizontally scrollable row of service category chips shown in the empty state:
- 8 chips with Feather icons + labels: ⚡ Electrician, 🔧 Plumber, ❄️ AC Repair, 🧹 Cleaner, 📚 Tutor, 🪚 Carpenter, 🎨 Painter, 🐛 Pest Control.
- Tapping a chip calls `viewModel.sendQuickAction(category, label)`, which pre-fills a Roman Urdu/English request specific to that category and immediately sends it.
- "See more" chip opens the full services bottom sheet.

### `SideDrawer.kt`
A navigation drawer (via `ModalNavigationDrawer`) with:
- Recent chat conversations stored as `ConversationSnapshot` objects — tappable to restore a previous conversation.
- "New Chat" button to clear and start fresh.
- Navigation links: My Bookings, Settings.

### `TypingIndicator.kt`
Three animated bouncing dots shown while the AI is "thinking". Each dot animates vertically with a staggered delay for a fluid wave effect. Used inside `AI_THINKING` message type.

### `HailBottomSheet.kt`
A full services bottom sheet with all 8 service categories displayed in a grid. Also includes navigation shortcuts to Settings, Bookings, History, and Agent Trace.

---

## Screens

### Splash Screen (`SplashScreen.kt`)
- **Icon Animation:** `spring(dampingRatio = MediumBouncy, stiffness = Low)` — drops from Y=-1000 to Y=0.
- **Scale Animation:** 0.1 → 1.0 over 1200ms with `FastOutSlowInEasing`.
- **Audio:** 150ms sine-wave generated via `AudioTrack` — exponential sweep from 400Hz to 1200Hz with `Math.exp(-progress * 5.0)` decay envelope. Generated entirely in code as raw byte arrays — no audio files bundled.
- **Gradient:** Animated `startY`/`endY` in `drawBehind` — 4 colors (blue, lavender, pink, mint) with alpha fading to white.
- **Branding:** "Developed by Aeroloom Studio" with `alpha` fade-in after 1.5s delay.

### Home Screen (`HomeScreen.kt`)
- `LazyColumn` for messages with auto-scroll to bottom on new messages.
- Gradient state logic: `IDLE` (no messages), `THINKING` (processing), `DONE` (has response).
- Top and bottom fade-edge scrims (semi-transparent white gradients) for smooth content clipping under the floating bars.
- `ModalNavigationDrawer` for the side drawer.
- Empty state with "Kya chahiye aaj?" title and quick action chips.

### Booking Confirmation Screen (`BookingConfirmationScreen.kt`)
- Custom `AnimatedCheckmark` composable drawn with `Canvas` — circle draws first (0-50% progress), then checkmark strokes animate in two phases (50-100%).
- Detail rows with Feather icons: User (provider), Tool (service), Clock (time), MapPin (location), DollarSign (estimate), Phone (contact).
- Green confirmation message card with `StatusGreenLight` background.
- "View Agent Trace" primary button (HailBlue) and "Back to Home" outlined button.

### My Bookings Screen (`MyBookingsScreen.kt`)
- Segmented tabs: Active / Completed / Cancelled.
- Booking cards with colored status badges.
- Tap a booking → navigates to `BookingConfirmationScreen`.

### Agent Trace Screen (`AgentTraceScreen.kt`)
- Vertical timeline of all 6 agent steps.
- Each step shows: agent name + emoji, tool used, input/output summaries, duration in ms.
- Expandable raw JSON output view for each step.
- Essential for hackathon demo — shows judges exactly which agent made which decision.

### Settings, About, Privacy, Terms, Licenses
- Clean list-based screens with Feather icons and proper back navigation via `NavController.popBackStack()`.
- About screen shows app version, team name, and links to Terms/Privacy/Licenses.

### Location Screen (`LocationMapScreen.kt`)
- Map screen for location context.

---

## Screen Navigation Flow

```
splash → home → booking/{bookingId}
               → my_bookings
               → agent_trace
               → settings → about → privacy → privacy_policy
                                             → terms
                                             → licenses
                          → location
```

All transitions use custom animations: enter slides from right (340ms), exit slides left, pop reverses.
