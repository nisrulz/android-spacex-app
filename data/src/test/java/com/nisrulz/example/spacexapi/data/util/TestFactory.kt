package com.nisrulz.example.spacexapi.data.util

import com.nisrulz.example.spacexapi.data.local.entity.LaunchInfoEntity
import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.network.retrofit.dto.Fairings
import com.nisrulz.example.spacexapi.network.retrofit.dto.Flickr
import com.nisrulz.example.spacexapi.network.retrofit.dto.LaunchInfoResponse
import com.nisrulz.example.spacexapi.network.retrofit.dto.Links
import com.nisrulz.example.spacexapi.network.retrofit.dto.Patch
import com.nisrulz.example.spacexapi.network.retrofit.dto.Reddit

object TestFactory {
    //region LaunchInfoResponse
    private fun buildLaunchInfoResponse() = LaunchInfoResponse(
        auto_update = false,
        capsules = emptyList(),
        cores = emptyList(),
        crew = emptyList(),
        date_local = "",
        date_precision = "",
        date_unix = 0,
        date_utc = "",
        details = "",
        failures = emptyList(),
        fairings = Fairings(
            recovered = false,
            recovery_attempt = false,
            reused = false,
            ships = emptyList()
        ),
        flight_number = 0,
        id = "",
        launch_library_id = "",
        launchpad = "",
        links = Links(
            article = "",
            flickr = Flickr(
                original = emptyList(),
                small = emptyList()
            ),
            patch = Patch(
                large = "",
                small = ""
            ),
            presskit = "",
            reddit = Reddit(
                campaign = "",
                launch = "",
                media = "",
                recovery = ""
            ),
            webcast = "",
            wikipedia = "",
            youtube_id = ""
        ),
        name = "",
        net = false,
        payloads = emptyList(),
        rocket = "",
        ships = emptyList(),
        static_fire_date_unix = 0,
        static_fire_date_utc = "",
        success = false,
        tbd = false,
        upcoming = false,
        window = 0
    )

    fun buildListOfLaunchInfoResponse() = listOf(
        buildLaunchInfoResponse().copy(id = "000001", name = "Flight 1"),
        buildLaunchInfoResponse().copy(id = "000002", name = "Flight 2"),
        buildLaunchInfoResponse().copy(id = "000003", name = "Flight 3")
    )
    //endregion

    //region LaunchInfoEntity
    fun buildLaunchInfoEntity() = LaunchInfoEntity(
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
        buildLaunchInfoEntity().copy(isBookmarked = true, id = "000003", name = "Flight 3")
    )

    fun buildListOfBookmarkedLaunchInfoEntity() = listOf(
        buildLaunchInfoEntity().copy(isBookmarked = true, id = "000001", name = "Flight 1"),
        buildLaunchInfoEntity().copy(isBookmarked = true, id = "000003", name = "Flight 3")
    )
    //endregion

    //region LaunchInfo
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
        buildLaunchInfo().copy(isBookmarked = true, id = "000003", name = "Flight 3")
    )

    fun buildListOfBookmarkedLaunchInfo() = listOf(
        buildLaunchInfo().copy(isBookmarked = true, id = "000001", name = "Flight 1"),
        buildLaunchInfo().copy(isBookmarked = true, id = "000003", name = "Flight 3")
    )
    //endregion
}
