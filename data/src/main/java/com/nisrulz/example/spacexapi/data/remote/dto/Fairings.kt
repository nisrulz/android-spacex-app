package com.nisrulz.example.spacexapi.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Fairings(
    val recovered: Boolean = false,
    val recovery_attempt: Boolean = false,
    val reused: Boolean = false,
    val ships: List<String>,
)