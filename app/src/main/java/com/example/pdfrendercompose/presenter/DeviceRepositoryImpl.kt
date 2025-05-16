package com.example.pdfrendercompose.presenter

import com.example.pdfrendercompose.data.domainmodel.Device
import com.example.pdfrendercompose.data.local.DeviceDao
import com.example.pdfrendercompose.data.local.DeviceEntity
import com.example.pdfrendercompose.data.local.UserEntity
import com.example.pdfrendercompose.data.local.UserEntityDao
import com.example.pdfrendercompose.data.remote.ApiService

class DeviceRepositoryImpl(
    private val api: ApiService,
    private val dao: DeviceDao,
    private val userDao: UserEntityDao,
) : DeviceRepository {
    override suspend fun fetchAndCacheDevices() {
        val response = api.getDevices()
        val entities = response.map {
            DeviceEntity(
                id = it.id?:"",
                name = it.name?:"",
                year = it.data?.year?:0,
                price = it.data?.price?:0.0,
                cpuModel = it.data?.cpuModel?:"",
                hardDiskSize = it.data?.hardDiskSize?:""
            )
        }
        dao.insertDevices(entities)
    }

    override suspend fun insertUser(userName: String, email: String) {
        userDao.insertDevices(UserEntity(id = 0,userName,email))
    }

    override suspend fun getCachedDevices(): List<Device> {
        return dao.getDevices().map {
            Device(
                id = it.id,
                name = it.name,
                year = it.year,
                price = it.price,
                cpuModel = it.cpuModel,
                hardDiskSize = it.hardDiskSize
            )
        }
    }
}