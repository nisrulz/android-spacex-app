package com.nisrulz.example.spacexapi.domain.usecase

import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import javax.inject.Inject

class GetLaunchDetail @Inject constructor(
    private val repository: LaunchesRepository,
) {
    suspend operator fun invoke(id: String) = repository.getLaunchDetail(id)
}