package com.aeroloomstudio.hailai.data.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val type: MessageType,
    val timestamp: Long = System.currentTimeMillis(),
    val agentStep: AgentStep? = null,
    val providers: List<Provider>? = null,
    val booking: Booking? = null,
    val isStreaming: Boolean = false,
    val completedSteps: List<AgentStep>? = null,
)

enum class MessageType {
    USER,
    AI_TEXT,
    AI_THINKING,         // Shows "Thinking..." with typing indicator
    AI_AGENT_STEP,       // Shows agent step progress (compact single line)
    AI_PROVIDER_CARD,    // Embedded provider cards
    AI_BOOKING_CONFIRM,  // Booking confirmation
    AI_TRACE_SUMMARY,    // Collapsed trace: "X steps completed → View trace"
    SYSTEM,              // System messages
}
