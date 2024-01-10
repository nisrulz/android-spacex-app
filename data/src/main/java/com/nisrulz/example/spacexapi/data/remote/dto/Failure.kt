package com.nisrulz.example.spacexapi.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Failure(
    val altitude: Int = 0,
    val reason: String = "",
    val time: Int = 0,
)
