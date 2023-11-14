package com.nisrulz.example.spacexapi.data.remote.dto

data class Failure(
    val altitude: Int,
    val reason: String,
    val time: Int,
)