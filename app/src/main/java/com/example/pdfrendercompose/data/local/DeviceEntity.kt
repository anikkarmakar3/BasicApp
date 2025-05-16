package com.example.pdfrendercompose.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String = "",
    val name: String ="",
    val year: Int = 0,
    val price: Double=0.0,
    val cpuModel: String= "",
    val hardDiskSize: String= ""
)
