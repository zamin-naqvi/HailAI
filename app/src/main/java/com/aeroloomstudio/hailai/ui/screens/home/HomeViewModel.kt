package com.aeroloomstudio.hailai.ui.screens.home

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeroloomstudio.hailai.agent.AgentOrchestrator
import com.aeroloomstudio.hailai.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * A snapshot of a single conversation that can be restored later.
 */
data class ConversationSnapshot(
    val title: String,
    val messages: List<ChatMessage>,
    val agentSteps: List<AgentStep>,
    val currentBooking: Booking?,
    val hasFirstResponse: Boolean,
)

class HomeViewModel : ViewModel() {

    private val orchestrator = AgentOrchestrator()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _inputText = MutableStateFlow(TextFieldValue(""))
    val inputText: StateFlow<TextFieldValue> = _inputText.asStateFlow()

    private val _currentBooking = MutableStateFlow<Booking?>(null)
    val currentBooking: StateFlow<Booking?> = _currentBooking.asStateFlow()

    private val _agentSteps = MutableStateFlow<List<AgentStep>>(emptyList())
    val agentSteps: StateFlow<List<AgentStep>> = _agentSteps.asStateFlow()

    private val _allBookings = MutableStateFlow<List<Booking>>(emptyList())
    val allBookings: StateFlow<List<Booking>> = _allBookings.asStateFlow()

    // Conversation snapshots for drawer
    private val _conversationSnapshots = MutableStateFlow<List<ConversationSnapshot>>(emptyList())

    private val _conversations = MutableStateFlow<List<String>>(emptyList())
    val conversations: StateFlow<List<String>> = _conversations.asStateFlow()

    private var currentConversationIndex = -1

    private val _hasFirstResponse = MutableStateFlow(false)
    val hasFirstResponse: StateFlow<Boolean> = _hasFirstResponse.asStateFlow()

    // ── Discovery result storage for booking phase ───────────────────────
    private var lastDiscoveryResult: AgentOrchestrator.DiscoveryResult? = null

    fun updateInput(value: TextFieldValue) {
        _inputText.value = value
    }

    /**
     * Phase 1: Send a message — runs only discovery steps (1-3).
     * Providers are shown; user picks one and clicks "Book Now".
     */
    fun sendMessage() {
        val text = _inputText.value.text.trim()
        if (text.isBlank() || _isProcessing.value) return

        _inputText.value = TextFieldValue("")

        // Add user message
        val userMessage = ChatMessage(
            content = text,
            type = MessageType.USER,
        )
        _messages.value = _messages.value + userMessage
        _isProcessing.value = true
        _agentSteps.value = emptyList()

        val roundId = System.currentTimeMillis().toString()
        val thinkingId = "thinking_$roundId"

        val thinkingMessage = ChatMessage(
            id = thinkingId,
            content = "",
            type = MessageType.AI_THINKING,
        )
        _messages.value = _messages.value + thinkingMessage

        // Save to conversation history
        val convTitle = text.take(40) + if (text.length > 40) "..." else ""
        if (currentConversationIndex == -1) {
            val snapshot = ConversationSnapshot(
                title = convTitle,
                messages = _messages.value,
                agentSteps = _agentSteps.value,
                currentBooking = _currentBooking.value,
                hasFirstResponse = _hasFirstResponse.value,
            )
            _conversationSnapshots.value = listOf(snapshot) + _conversationSnapshots.value
            _conversations.value = _conversationSnapshots.value.map { it.title }
            currentConversationIndex = 0
        }

        viewModelScope.launch {
            try {
                // Phase 1: Discovery only (Steps 1-3)
                val discoveryResult = orchestrator.discoverProviders(text) { step ->
                    _agentSteps.value = _agentSteps.value.toMutableList().apply {
                        val idx = indexOfFirst { it.stepNumber == step.stepNumber }
                        if (idx >= 0) set(idx, step) else add(step)
                    }

                    val stepMessage = ChatMessage(
                        id = "${roundId}_step_${step.stepNumber}_${step.status}",
                        content = "",
                        type = MessageType.AI_AGENT_STEP,
                        agentStep = step,
                    )

                    _messages.value = _messages.value.toMutableList().apply {
                        removeAll { it.id == thinkingId }
                        val stepIdx = indexOfFirst { msg ->
                            msg.agentStep?.stepNumber == step.stepNumber &&
                            msg.id.startsWith("${roundId}_step_")
                        }
                        if (stepIdx >= 0) set(stepIdx, stepMessage) else add(stepMessage)
                    }
                }

                // Remove thinking message
                _messages.value = _messages.value.filter { it.id != thinkingId }

                // Store discovery result for booking phase
                lastDiscoveryResult = discoveryResult

                // Collapse step messages into a single trace summary
                val stepIds = _messages.value
                    .filter { it.id.startsWith("${roundId}_step_") }
                    .map { it.id }
                    .toSet()
                _messages.value = _messages.value.filter { it.id !in stepIds }
                _messages.value = _messages.value + ChatMessage(
                    id = "${roundId}_trace",
                    content = "",
                    type = MessageType.AI_TRACE_SUMMARY,
                    completedSteps = discoveryResult.steps,
                )

                // Add provider cards
                if (discoveryResult.providers.isNotEmpty()) {
                    val providerMessage = ChatMessage(
                        content = "I found ${discoveryResult.providers.size} providers for you. Tap \"Book Now\" to proceed:",
                        type = MessageType.AI_PROVIDER_CARD,
                        providers = discoveryResult.providers,
                    )
                    _messages.value = _messages.value + providerMessage
                } else {
                    _messages.value = _messages.value + ChatMessage(
                        content = "I couldn't find matching providers. Try specifying a different area or service.",
                        type = MessageType.AI_TEXT,
                    )
                }

            } catch (e: Exception) {
                _messages.value = _messages.value.filter { it.id != thinkingId }
                _messages.value = _messages.value + ChatMessage(
                    content = "Something went wrong: ${e.message}. Please try again.",
                    type = MessageType.AI_TEXT,
                )
            } finally {
                _isProcessing.value = false
                _hasFirstResponse.value = true
                saveCurrentConversation()
            }
        }
    }

    /**
     * Phase 2: Book a specific provider — runs booking steps (4-6).
     * Called after user confirms in BookingFlowSheet.
     */
    fun bookProvider(provider: Provider, selectedSlot: String) {
        val discovery = lastDiscoveryResult ?: return
        _isProcessing.value = true

        val roundId = "book_${System.currentTimeMillis()}"

        viewModelScope.launch {
            try {
                val bookingResult = orchestrator.bookProvider(
                    provider = provider,
                    selectedSlot = selectedSlot,
                    serviceType = discovery.serviceType,
                    area = discovery.parsedArea,
                    city = discovery.parsedCity,
                    timeNormalized = discovery.timeNormalized,
                ) { step ->
                    _agentSteps.value = _agentSteps.value.toMutableList().apply {
                        val idx = indexOfFirst { it.stepNumber == step.stepNumber }
                        if (idx >= 0) set(idx, step) else add(step)
                    }

                    val stepMessage = ChatMessage(
                        id = "${roundId}_step_${step.stepNumber}_${step.status}",
                        content = "",
                        type = MessageType.AI_AGENT_STEP,
                        agentStep = step,
                    )

                    _messages.value = _messages.value.toMutableList().apply {
                        val stepIdx = indexOfFirst { msg ->
                            msg.agentStep?.stepNumber == step.stepNumber &&
                            msg.id.startsWith("${roundId}_step_")
                        }
                        if (stepIdx >= 0) set(stepIdx, stepMessage) else add(stepMessage)
                    }
                }

                // Collapse booking step messages into a trace summary
                val bookStepIds = _messages.value
                    .filter { it.id.startsWith("${roundId}_step_") }
                    .map { it.id }
                    .toSet()
                _messages.value = _messages.value.filter { it.id !in bookStepIds }
                _messages.value = _messages.value + ChatMessage(
                    id = "${roundId}_trace",
                    content = "",
                    type = MessageType.AI_TRACE_SUMMARY,
                    completedSteps = bookingResult.steps,
                )

                // Add booking confirmation to chat
                val booking = bookingResult.booking
                val bookingMessage = ChatMessage(
                    content = "",
                    type = MessageType.AI_BOOKING_CONFIRM,
                    booking = booking,
                )
                _messages.value = _messages.value + bookingMessage
                _currentBooking.value = booking
                _allBookings.value = _allBookings.value + booking

                // Add summary
                _messages.value = _messages.value + ChatMessage(
                    content = "I've booked ${booking.providerName} for you at ${selectedSlot} in ${booking.locationRequested}. Reminders have been scheduled!",
                    type = MessageType.AI_TEXT,
                )

            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    content = "Booking failed: ${e.message}. Please try again.",
                    type = MessageType.AI_TEXT,
                )
            } finally {
                _isProcessing.value = false
                saveCurrentConversation()
            }
        }
    }

    fun sendQuickAction(category: String, label: String) {
        val text = when (category) {
            "hvac" -> "Mujhe AC technician chahiye G-13 Islamabad mein kal subah"
            "plumbing" -> "I need a plumber in G-9 Islamabad today"
            "electrical" -> "Bijli ka kaam karwana hai F-7 Islamabad"
            "cleaning" -> "Home cleaning service chahiye E-11 mein kal"
            "tutoring" -> "Tutor chahiye G-10 Islamabad mein evening"
            "carpentry" -> "Carpenter chahiye furniture repair ke liye I-10"
            "painting" -> "Need a painter in F-11 Islamabad tomorrow morning"
            "pest_control" -> "Pest control service chahiye H-13 mein"
            else -> "I need a $label"
        }
        _inputText.value = TextFieldValue(text)
        sendMessage()
    }

    private fun saveCurrentConversation() {
        if (currentConversationIndex < 0) return
        val snapshots = _conversationSnapshots.value.toMutableList()
        if (currentConversationIndex < snapshots.size) {
            snapshots[currentConversationIndex] = ConversationSnapshot(
                title = snapshots[currentConversationIndex].title,
                messages = _messages.value,
                agentSteps = _agentSteps.value,
                currentBooking = _currentBooking.value,
                hasFirstResponse = _hasFirstResponse.value,
            )
            _conversationSnapshots.value = snapshots
        }
    }

    fun loadConversation(index: Int) {
        saveCurrentConversation()

        val snapshots = _conversationSnapshots.value
        if (index < 0 || index >= snapshots.size) return

        val snapshot = snapshots[index]
        _messages.value = snapshot.messages
        _agentSteps.value = snapshot.agentSteps
        _currentBooking.value = snapshot.currentBooking
        _hasFirstResponse.value = snapshot.hasFirstResponse
        _isProcessing.value = false
        currentConversationIndex = index
    }

    fun clearChat() {
        saveCurrentConversation()

        _messages.value = emptyList()
        _currentBooking.value = null
        _agentSteps.value = emptyList()
        _isProcessing.value = false
        _hasFirstResponse.value = false
        lastDiscoveryResult = null
        currentConversationIndex = -1
    }
}
