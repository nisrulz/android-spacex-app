package com.nisrulz.example.spacexapi.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class Patch(
    val large: String = "",
    val small: String = ""
)
