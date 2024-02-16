package com.nisrulz.example.spacexapi.network.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Patch(
    val large: String = "",
    val small: String = ""
)
