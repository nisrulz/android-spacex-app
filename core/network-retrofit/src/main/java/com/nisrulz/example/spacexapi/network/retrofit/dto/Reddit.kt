package com.nisrulz.example.spacexapi.network.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Reddit(
    val campaign: String = "",
    val launch: String = "",
    val media: String = "",
    val recovery: String = ""
)
