package com.aeroloomstudio.hailai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AgentStep(
    val stepNumber: Int,
    val agentName: String,
    val agentEmoji: String,
    val toolUsed: String,
    val status: AgentStepStatus,
    val inputSummary: String,
    val outputSummary: String,
    val outputJson: String = "{}",
    val durationMs: Long = 0,
    val timestamp: String = "",
)

@Serializable
enum class AgentStepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
}

val agentStepTemplates = listOf(
    AgentStep(
        stepNumber = 1,
        agentName = "NLU Agent",
        agentEmoji = "🧠",
        toolUsed = "Gemini 3.1 Pro",
        status = AgentStepStatus.PENDING,
        inputSummary = "Raw user text",
        outputSummary = "Structured intent JSON",
        outputJson = "{\n  \"intent\": \"book_service\",\n  \"service_category\": \"plumbing\",\n  \"language_preference\": \"urdu\",\n  \"urgency\": \"normal\"\n}"
    ),
    AgentStep(
        stepNumber = 2,
        agentName = "Discovery Agent",
        agentEmoji = "🔍",
        toolUsed = "realtime_db_query_tool, maps_geocode_tool",
        status = AgentStepStatus.PENDING,
        inputSummary = "Structured intent",
        outputSummary = "Candidate providers list",
        outputJson = "{\n  \"candidates_found\": 14,\n  \"filtered_by_language\": 5,\n  \"top_candidates\": [\n    \"provider_101\",\n    \"provider_242\"\n  ]\n}"
    ),
    AgentStep(
        stepNumber = 3,
        agentName = "Ranking Agent",
        agentEmoji = "📊",
        toolUsed = "scoring_tool + Gemini 3.1 Pro reasoning",
        status = AgentStepStatus.PENDING,
        inputSummary = "Candidate providers",
        outputSummary = "Top 3 ranked providers",
        outputJson = "{\n  \"selected_provider\": \"id_101\",\n  \"confidence_score\": 0.94,\n  \"reasoning\": \"Highest rating + exact language match.\"\n}"
    ),
    AgentStep(
        stepNumber = 4,
        agentName = "Booking Agent",
        agentEmoji = "📝",
        toolUsed = "realtime_db_write_tool, receipt_generator_tool",
        status = AgentStepStatus.PENDING,
        inputSummary = "Selected provider + time",
        outputSummary = "Booking record created",
        outputJson = "{\n  \"booking_id\": \"BK-8842-12\",\n  \"status\": \"confirmed\",\n  \"database_write\": \"success\",\n  \"timestamp\": \"2026-05-20T12:05:00Z\"\n}"
    ),
    AgentStep(
        stepNumber = 5,
        agentName = "Notification Agent",
        agentEmoji = "🔔",
        toolUsed = "fcm_notify_tool",
        status = AgentStepStatus.PENDING,
        inputSummary = "Booking confirmation data",
        outputSummary = "Notification delivered",
        outputJson = "{\n  \"fcm_status\": \"success\",\n  \"payload_size_bytes\": 240,\n  \"delivered_to\": [\"device_token_abc123\"]\n}"
    ),
    AgentStep(
        stepNumber = 6,
        agentName = "Follow-Up Agent",
        agentEmoji = "⏰",
        toolUsed = "cloud_tasks_tool, state_machine_tool",
        status = AgentStepStatus.PENDING,
        inputSummary = "Booking ID + appointment time",
        outputSummary = "Reminders scheduled",
        outputJson = "{\n  \"tasks_created\": 2,\n  \"trigger_times\": [\n    \"2026-05-21T09:00:00Z\",\n    \"2026-05-21T14:30:00Z\"\n  ]\n}"
    ),
)
