package com.nisrulz.example.spacexapi.data.mapper

import com.nisrulz.example.spacexapi.data.local.entity.LaunchInfoEntity
import com.nisrulz.example.spacexapi.network.retrofit.dto.LaunchInfoResponse

fun LaunchInfoResponse.toEntity() = LaunchInfoEntity(
    date_local = date_local,
    details = details,
    flight_number = flight_number,
    id = id,
    logo = links.patch?.small,
    name = name,
    success = success
)

fun List<LaunchInfoResponse>.toEntityList(): List<LaunchInfoEntity> = this.map { it.toEntity() }
