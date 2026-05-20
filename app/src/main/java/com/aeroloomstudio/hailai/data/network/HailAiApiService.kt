package com.aeroloomstudio.hailai.data.network

import com.aeroloomstudio.hailai.BuildConfig
import com.aeroloomstudio.hailai.data.model.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface HailAiApiService {
    @GET("providers.json")
    suspend fun getProviders(): Map<String, Provider>

    @PUT("bookings/{bookingId}.json")
    suspend fun createBooking(
        @Path("bookingId") bookingId: String,
        @Body booking: Booking
    ): Booking

    companion object {
        private val BASE_URL: String = BuildConfig.FIREBASE_URL

        fun create(): HailAiApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val jsonMediaType = "application/json".toMediaType()
            val json = kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                encodeDefaults = true
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(json.asConverterFactory(jsonMediaType))
                .build()

            return retrofit.create(HailAiApiService::class.java)
        }
    }
}
