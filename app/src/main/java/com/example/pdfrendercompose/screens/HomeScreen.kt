package com.example.pdfrendercompose.screens

import MyPdfViewerScreen
import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import com.example.pdfrendercompose.MainActivity
import com.example.pdfrendercompose.PreferenceUtils
import com.example.pdfrendercompose.R
import com.example.pdfrendercompose.route.Routes

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as? Activity
    val channelId = "dummy_channel"
    if (activity != null) {
        createNotificationChannel(activity,channelId)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var showPdf by remember { mutableStateOf(false) }
            var sendNotification by remember { mutableStateOf(false) }
            Button(onClick = { navController.navigate(Routes.Image) }) {
                Text("Image Feature")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate(Routes.Data) }) {
                Text("Data Feature")
            }
            Button(onClick = { showPdf = true }) {
                Text("Open PDF")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { sendNotification = true }) {
                Text("Send Notification")
            }

            if (showPdf) {
                MyPdfViewerScreen()
            }

            RowWithTextAndSwitch()

            if (sendNotification) {
                sendNotification = false
                if (PreferenceUtils.get("switch_state", false)) {
                    if (activity != null) {
                        sendNotification(activity,channelId)
                    }
                } else {
                    Toast.makeText(context, "Not enable", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}

private fun sendNotification(activity: Activity,channelId:String) {
    val intent = Intent(activity, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        activity, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(activity, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Hello!")
        .setContentText("This is a local notification.")
        .setLocalOnly(true)
        .setContentIntent(pendingIntent).setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_MAX)

    with(NotificationManagerCompat.from(activity)) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(1, builder.build())
    }
}

private fun createNotificationChannel(activity: Activity,channelId: String) {
    val channel = NotificationChannel(
        channelId, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Channel for test notifications"
    }
    activity.let {
        val notificationManager: NotificationManager =
            it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}

@Composable
fun RowWithTextAndSwitch() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Enable Notification : ",
            style = MaterialTheme.typography.bodyLarge
        )

        SwitchWithIconExample()
    }
}


@Composable
fun SwitchWithIconExample() {
    var checked by remember { mutableStateOf(false) }

    Switch(
        checked = checked, onCheckedChange = {
            checked = it
            PreferenceUtils.save("switch_state", it)
        },
        thumbContent = if (checked) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        })
}
