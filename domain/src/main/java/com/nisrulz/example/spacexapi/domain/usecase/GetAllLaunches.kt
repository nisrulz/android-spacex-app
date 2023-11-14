package com.nisrulz.example.spacexapi.domain.usecase

import com.nisrulz.example.spacexapi.domain.repository.LaunchesRepository
import javax.inject.Inject

class GetAllLaunches @Inject constructor(
    private val repository: LaunchesRepository,
) {
    suspend operator fun invoke() = repository.getListOfLaunches()
}