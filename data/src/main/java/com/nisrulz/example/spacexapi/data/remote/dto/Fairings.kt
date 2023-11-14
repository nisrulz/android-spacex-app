package com.nisrulz.example.spacexapi.data.remote.dto

data class Fairings(
    val recovered: Boolean,
    val recovery_attempt: Boolean,
    val reused: Boolean,
    val ships: List<String>,
)