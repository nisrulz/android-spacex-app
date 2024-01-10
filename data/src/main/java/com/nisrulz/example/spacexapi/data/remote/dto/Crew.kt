package com.nisrulz.example.spacexapi.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Crew(
    val crew: String = "",
    val role: String = "",
)
