package com.example.pdfrendercompose.presenter

import com.example.pdfrendercompose.data.domainmodel.Device

interface DeviceRepository {
    suspend fun fetchAndCacheDevices()
    suspend fun getCachedDevices(): List<Device>
}