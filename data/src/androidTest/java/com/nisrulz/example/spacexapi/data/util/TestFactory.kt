package com.nisrulz.example.spacexapi.data.util

import com.nisrulz.example.spacexapi.data.local.entity.LaunchInfoEntity

object TestFactory {
    private fun buildLaunchInfoEntity() = LaunchInfoEntity(
        date_local = "",
        details = "",
        flight_number = 0,
        id = "",
        logo = "",
        name = "",
        success = false,
        isBookmarked = false
    )

    fun buildListOfLaunchInfoEntity() = listOf(
        buildLaunchInfoEntity().copy(isBookmarked = true, id = "000001", name = "Flight 1"),
        buildLaunchInfoEntity().copy(isBookmarked = false, id = "000002", name = "Flight 2"),
        buildLaunchInfoEntity().copy(isBookmarked = true, id = "000003", name = "Flight 3"),
    )

    fun buildListOfBookmarkedLaunchInoEntity() = listOf(
        buildLaunchInfoEntity().copy(isBookmarked = true, id = "000001", name = "Flight 1"),
        buildLaunchInfoEntity().copy(isBookmarked = true, id = "000003", name = "Flight 3"),
    )
}