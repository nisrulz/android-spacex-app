package com.nisrulz.example.spacexapi.data.mapper

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.network.dto.LaunchInfoResponse

fun LaunchInfoResponse.toDomainModel() = LaunchInfo(
    date_local = date_local,
    details = details,
    flight_number = flight_number,
    id = id,
    logo = links.patch?.small,
    name = name,
    success = success,
    isBookmarked = false
)

fun List<LaunchInfoResponse>.toDomainModelList(): List<LaunchInfo> = this.map { it.toDomainModel() }
