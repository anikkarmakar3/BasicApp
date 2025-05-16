package com.example.pdfrendercompose.presenter

import com.example.pdfrendercompose.data.domainmodel.Device

interface DeviceRepository {
    suspend fun fetchAndCacheDevices()
    suspend fun insertUser(userName: String, email: String)
    suspend fun getCachedDevices(): List<Device>
    suspend fun deleteDevice(deviceId : Int, deviceName : String)
}