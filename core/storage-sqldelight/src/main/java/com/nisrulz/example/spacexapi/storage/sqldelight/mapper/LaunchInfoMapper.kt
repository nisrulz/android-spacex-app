package com.nisrulz.example.spacexapi.storage.sqldelight.mapper

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.storage.sqldelight.LaunchInfoEntity

fun LaunchInfoEntity.toDomainModel(): LaunchInfo {
    return LaunchInfo(
        date_local = date_local,
        details = details,
        flight_number = flight_number.toInt(),
        id = id,
        logo = logo,
        name = name,
        success = success == 1L,
        isBookmarked = isBookmarked == 1L
    )
}

fun LaunchInfo.toEntity(): LaunchInfoEntity {
    return LaunchInfoEntity(
        id = id,
        date_local = date_local,
        details = details,
        flight_number = flight_number.toLong(),
        logo = logo,
        name = name,
        success = if (success) 1L else 0L,
        isBookmarked = if (isBookmarked) 1L else 0L
    )
}

fun List<LaunchInfoEntity>.toDomainModelList(): List<LaunchInfo> {
    return map { it.toDomainModel() }
}

fun List<LaunchInfo>.toEntityList(): List<LaunchInfoEntity> {
    return map { it.toEntity() }
}
