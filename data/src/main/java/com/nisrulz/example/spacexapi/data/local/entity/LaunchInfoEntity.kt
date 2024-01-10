package com.nisrulz.example.spacexapi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LaunchInfoEntity(
    val date_local: String,
    val details: String?,
    val flight_number: Int,
    @PrimaryKey val id: String,
    val logo: String?,
    val name: String,
    val success: Boolean,
    val isBookmarked: Boolean = false,
)
