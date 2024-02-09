package com.nisrulz.example.spacexapi.domain.model

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

data class LaunchInfo(
    val date_local: String,
    val details: String?,
    val flight_number: Int,
    val id: String,
    val logo: String?,
    val name: String,
    val success: Boolean,
    val isBookmarked: Boolean = false,
) {
    fun getFormattedDate(): String {
        val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val targetFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        return try {
            val date = sourceFormat.parse(date_local) ?: ""
            targetFormat.format(date)
        } catch (e: ParseException) {
            ""
        }
    }

    fun wasSuccessfulString(): String = if (success) "Yes" else "No"

    fun getDetailsString() = details ?: "-"
}
