package com.example.pdfrendercompose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pdfrendercompose.presenter.DeviceViewModel

@Composable
fun DeviceListScreen(viewModel: DeviceViewModel) {
    val devices = viewModel.devices.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(devices.value) { device ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = device.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "CPU: ${device.cpuModel}")
                    Text(text = "Price: \$${device.price}")
                    Text(text = "Storage: ${device.hardDiskSize}")
                    Text(text = "Year: ${device.year}")
                }
            }
        }
    }
}