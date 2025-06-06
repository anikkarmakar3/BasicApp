package com.example.pdfrendercompose.data.local

import androidx.room.*

@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)

    @Query("SELECT * FROM devices")
    suspend fun getDevices(): List<DeviceEntity>

    @Query("DELETE FROM devices WHERE id = :deviceId and name= :deviceName")
    suspend fun deleteDevice(deviceId : Int, deviceName: String)
}