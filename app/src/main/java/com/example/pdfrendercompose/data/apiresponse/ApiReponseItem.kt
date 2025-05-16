package com.example.pdfrendercompose.data.apiresponse

import com.google.gson.annotations.SerializedName

data class ApiResponseItem(
    val id: String?,
    val name: String?,
    val data: DataDetails?=null
)

data class DataDetails(
    val year: Int?,
    val price: Double?,
    @SerializedName("CPU model")
    val cpuModel: String?,
    @SerializedName("Hard disk size")
    val hardDiskSize: String?
)

