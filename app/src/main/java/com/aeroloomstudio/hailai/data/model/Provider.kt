package com.aeroloomstudio.hailai.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Provider(
    val id: String,
    val name: String,
    val category: String,
    val subcategory: String,
    val phone: String,
    val area: String,
    val city: String,
    val lat: Double,
    val lng: Double,
    val rating: Double,
    @SerialName("total_reviews") val totalReviews: Int,
    val priceMin: Int,
    val priceMax: Int,
    val currency: String = "PKR",
    @SerialName("available_days") val availableDays: List<String>,
    @SerialName("available_slots") val availableSlots: List<String>,
    val verified: Boolean,
    @SerialName("experience_years") val experienceYears: Int,
    val languages: List<String>,
    @SerialName("photo_url") val photoUrl: String = "",
)

fun Provider.displayCategory(): String = when (category) {
    "hvac" -> "AC Technician"
    "plumbing" -> "Plumber"
    "electrical" -> "Electrician"
    "cleaning" -> "Home Cleaner"
    "tutoring" -> "Home Tutor"
    "carpentry" -> "Carpenter"
    "painting" -> "Painter"
    "pest_control" -> "Pest Control"
    else -> category.replaceFirstChar { it.uppercase() }
}

fun Provider.priceRange(): String = "$currency $priceMin–$priceMax"
