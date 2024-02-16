package com.nisrulz.example.spacexapi.network.retrofit.dto

import kotlinx.serialization.Serializable

@Serializable
data class LaunchInfoResponse(
    val auto_update: Boolean = false,
    val capsules: List<String>,
    val cores: List<Core>,
    val crew: List<Crew>,
    val date_local: String = "",
    val date_precision: String = "",
    val date_unix: Int = 0,
    val date_utc: String = "",
    val details: String = "",
    val failures: List<Failure>,
    val fairings: Fairings? = null,
    val flight_number: Int = 0,
    val id: String = "",
    val launch_library_id: String = "",
    val launchpad: String = "",
    val links: Links,
    val name: String = "",
    val net: Boolean = false,
    val payloads: List<String>,
    val rocket: String = "",
    val ships: List<String>,
    val static_fire_date_unix: Int = 0,
    val static_fire_date_utc: String = "",
    val success: Boolean = false,
    val tbd: Boolean = false,
    val upcoming: Boolean = false,
    val window: Int = 0
)
