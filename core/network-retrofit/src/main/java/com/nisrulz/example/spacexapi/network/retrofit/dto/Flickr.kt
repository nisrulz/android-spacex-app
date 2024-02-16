package com.nisrulz.example.spacexapi.network.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Flickr(
    val original: List<String>,
    val small: List<String>
)
