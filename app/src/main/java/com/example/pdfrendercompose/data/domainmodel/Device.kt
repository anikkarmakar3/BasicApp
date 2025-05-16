package com.example.pdfrendercompose.data.domainmodel

data class Device(
    val id: String,
    val name: String,
    val year: Int,
    val price: Double,
    val cpuModel: String,
    val hardDiskSize: String
)
