package com.nisrulz.example.spacexapi.data.mapper

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.storage.roomdb.entity.LaunchInfoEntity

fun LaunchInfo.mapFromDomainModel(): LaunchInfoEntity {
    return LaunchInfoEntity(
        date_local = date_local,
        details = details,
        flight_number = flight_number,
        id = id,
        logo = logo,
        name = name,
        success = success,
        isBookmarked = isBookmarked
    )
}

fun LaunchInfoEntity.mapToDomainModel(): LaunchInfo {
    return LaunchInfo(
        date_local = date_local,
        details = details,
        flight_number = flight_number,
        id = id,
        logo = logo,
        name = name,
        success = success,
        isBookmarked = isBookmarked
    )
}

fun List<LaunchInfoEntity>.mapToDomainModelList(): List<LaunchInfo> =
    this.map { it.mapToDomainModel() }
