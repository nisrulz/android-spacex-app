package com.nisrulz.example.spacexapi.storage.roomdb.mapper

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.storage.roomdb.entity.LaunchInfoEntity

fun LaunchInfoEntity.toDomainModel(): LaunchInfo {
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

fun LaunchInfo.toEntity(): LaunchInfoEntity {
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

fun List<LaunchInfoEntity>.toDomainModelList(): List<LaunchInfo> {
    return map { it.toDomainModel() }
}

fun List<LaunchInfo>.toEntityList(): List<LaunchInfoEntity> {
    return map { it.toEntity() }
}
