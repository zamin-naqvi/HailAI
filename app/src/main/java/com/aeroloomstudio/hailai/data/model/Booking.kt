package com.aeroloomstudio.hailai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val bookingId: String,
    val userId: String = "usr_demo_001",
    val providerId: String,
    val providerName: String,
    val serviceType: String,
    val status: BookingStatus,
    val slotDatetime: String,
    val locationRequested: String,
    val createdAt: String,
    val agentTraceId: String,
    val reminders: List<Reminder> = emptyList(),
    val confirmationMessage: String,
    val providerRating: Double = 0.0,
    val providerPhone: String = "",
    val priceEstimate: String = "",
)

@Serializable
enum class BookingStatus {
    PENDING,
    CONFIRMED,
    REMINDER_SENT,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    DISPUTED
}

@Serializable
data class Reminder(
    val type: String,     // "pre_appointment" or "feedback_request"
    val scheduledAt: String,
    val sent: Boolean = false,
)

fun BookingStatus.displayName(): String = when (this) {
    BookingStatus.PENDING -> "Pending"
    BookingStatus.CONFIRMED -> "Confirmed"
    BookingStatus.REMINDER_SENT -> "Reminder Sent"
    BookingStatus.IN_PROGRESS -> "In Progress"
    BookingStatus.COMPLETED -> "Completed"
    BookingStatus.CANCELLED -> "Cancelled"
    BookingStatus.DISPUTED -> "Disputed"
}
