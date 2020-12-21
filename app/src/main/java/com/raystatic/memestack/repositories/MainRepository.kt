package com.raystatic.memestack.repositories

import com.raystatic.memestack.remote.ApiService
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiService
){

    suspend fun getMemes() = apiService.getMemes()

}