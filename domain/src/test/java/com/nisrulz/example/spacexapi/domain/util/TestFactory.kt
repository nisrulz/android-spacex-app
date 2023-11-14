package com.nisrulz.example.spacexapi.domain.util

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo

object TestFactory {
    fun buildLaunchInfo() = LaunchInfo(
        date_local = "",
        details = "",
        flight_number = 0,
        id = "",
        logo = "",
        name = "",
        success = false,
        isBookmarked = false
    )

    fun buildListOfLaunchInfo() = listOf(
        buildLaunchInfo().copy(isBookmarked = true, id = "000001", name = "Flight 1"),
        buildLaunchInfo().copy(isBookmarked = false, id = "000002", name = "Flight 2"),
        buildLaunchInfo().copy(isBookmarked = true, id = "000003", name = "Flight 3"),
    )

    fun buildListOfBookmarkedLaunchInfo() = listOf(
        buildLaunchInfo().copy(isBookmarked = true, id = "000001", name = "Flight 1"),
        buildLaunchInfo().copy(isBookmarked = true, id = "000003", name = "Flight 3"),
    )
}