package com.nisrulz.example.spacexapi.network.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Crew(
    val crew: String = "",
    val role: String = ""
)
