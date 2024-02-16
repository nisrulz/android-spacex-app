package com.nisrulz.example.spacexapi.network.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class Links(
    val article: String = "",
    val flickr: Flickr? = null,
    val patch: Patch? = null,
    val presskit: String = "",
    val reddit: Reddit? = null,
    val webcast: String = "",
    val wikipedia: String = "",
    val youtube_id: String = ""
)
