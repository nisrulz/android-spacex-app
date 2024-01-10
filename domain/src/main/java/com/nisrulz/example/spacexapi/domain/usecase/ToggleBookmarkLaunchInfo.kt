package com.nisrulz.example.spacexapi.domain.usecase

import com.nisrulz.example.spacexapi.domain.model.LaunchInfo
import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import javax.inject.Inject

class ToggleBookmarkLaunchInfo
    @Inject
    constructor(
        private val repository: LaunchesRepository,
    ) {
        suspend operator fun invoke(launchInfo: LaunchInfo) = repository.setBookmark(launchInfo.id, !launchInfo.isBookmarked)
    }
