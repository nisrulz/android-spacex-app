package com.nisrulz.example.spacexapi.network.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Core(
    val core: String = "",
    val flight: Int = 0,
    val gridfins: Boolean = false,
    val landing_attempt: Boolean = false,
    val landing_success: Boolean = false,
    val landing_type: String = "",
    val landpad: String = "",
    val legs: Boolean = false,
    val reused: Boolean = false
)
