package com.example.pdfrendercompose.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdfrendercompose.data.domainmodel.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeviceViewModel(private val repo: DeviceRepository) : ViewModel() {

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices

    init {
        loadDevices()
    }

    private fun loadDevices() {
        viewModelScope.launch {
            repo.fetchAndCacheDevices()
            _devices.value = repo.getCachedDevices()
        }
    }

    fun insertUser(userName:String, email:String){
        viewModelScope.launch {
            repo.insertUser(userName,email)
        }
    }

    fun removeDevice(device: Device) {
        viewModelScope.launch {
            repo.deleteDevice(device.id.toInt(), device.name)
            _devices.value = repo.getCachedDevices()
        }
    }
}