package com.example.pdfrendercompose.screens

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pdfrendercompose.MainActivity
import com.example.pdfrendercompose.PreferenceUtils
import com.example.pdfrendercompose.R
import com.example.pdfrendercompose.data.domainmodel.Device
import com.example.pdfrendercompose.presenter.DeviceViewModel

@Composable
fun DeviceListScreen(viewModel: DeviceViewModel) {
    val devices = viewModel.devices.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val channelId = "dummy_channel"

    if (activity != null) {
        createNotificationChannel(activity, channelId)
        requestNotificationPermission(activity)
    }

    var sendNotification by remember { mutableStateOf(false) }
    var deviceDetails by remember { mutableStateOf(Device()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deviceToDelete by remember { mutableStateOf<Device?>(null) }

    if (sendNotification) {
        sendNotification = false
        if (activity != null) {
            sendNotification(activity, channelId, deviceDetails)
        }
    }

    RowWithTextAndSwitch()

    LazyColumn(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 100.dp)
    ) {
        items(devices.value) { device ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = {
                            if (PreferenceUtils.get(PreferenceUtils.PREFS_KEY, false)) {
                                deviceToDelete = device
                                showDeleteDialog = true
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Notification Settings Not enabled",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        })
                    }
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

    if (showDeleteDialog && deviceToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Device") },
            text = { Text("Are you sure you want to delete this device?") },
            confirmButton = {
                TextButton(onClick = {
                    sendNotification = true
                    deviceDetails = deviceToDelete!!
                    viewModel.removeDevice(deviceToDelete!!)
                    showDeleteDialog = false
                    deviceToDelete = null
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    deviceToDelete = null
                }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun RowWithTextAndSwitch() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 50.dp)
    ) {
        Text(text = "Enable Notification : ", style = MaterialTheme.typography.bodyLarge)
        SwitchWithIconExample()
    }
}

@Composable
fun SwitchWithIconExample() {
    var checked by remember {
        mutableStateOf(PreferenceUtils.get(PreferenceUtils.PREFS_KEY, false))
    }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
            PreferenceUtils.save(PreferenceUtils.PREFS_KEY, it)
        },
        thumbContent = if (checked) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else null
    )
}

private fun requestNotificationPermission(activity: Activity) {
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1001
        )
    }
}

private fun sendNotification(activity: Activity, channelId: String, device: Device) {
    if (ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Toast.makeText(activity, "Notification permission not granted", Toast.LENGTH_SHORT).show()
        return
    }

    val intent = Intent(activity, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent = PendingIntent.getActivity(
        activity,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(activity, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(device.name)
        .setContentText("${device.name} is deleted whose price is ${device.price}")
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_MAX)

    NotificationManagerCompat.from(activity).notify(1, builder.build())
}

private fun createNotificationChannel(activity: Activity, channelId: String) {
    val channel = NotificationChannel(
        channelId,
        "My Notifications",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Channel for test notifications"
    }

    val notificationManager =
        activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

