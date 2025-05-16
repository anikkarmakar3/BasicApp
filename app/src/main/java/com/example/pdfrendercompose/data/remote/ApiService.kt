package com.example.pdfrendercompose.data.remote

import com.example.pdfrendercompose.data.apiresponse.ApiResponseItem
import retrofit2.http.GET

interface ApiService {
    @GET("objects")
    suspend fun getDevices(): List<ApiResponseItem>
}