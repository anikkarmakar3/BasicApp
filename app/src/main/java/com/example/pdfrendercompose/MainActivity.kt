package com.example.pdfrendercompose

import MyPdfViewerScreen
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.pdfrendercompose.module.AppModule
import com.example.pdfrendercompose.presenter.DeviceViewModel
import com.example.pdfrendercompose.route.Routes
import com.example.pdfrendercompose.screens.DeviceListScreen
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    private val channelId: String = "dummy_channel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PreferenceUtils.init(this)
        createNotificationChannel()
        val repo = AppModule.provideRepository(
            AppModule.provideApi(),
            AppModule.provideDatabase(applicationContext)
        )
        val viewModel = DeviceViewModel(repo)
        setContent {
            ProvideWindowInsets  {
                MyApp(viewModel)
            }
        }
    }

    @Composable
    fun MyApp(viewModel: DeviceViewModel) {
        val navController = rememberNavController()
        NavHost(navController, startDestination = Routes.Login) {
            composable(Routes.Login) { LoginScreen(navController) }
            composable(Routes.Home) { HomeScreen(navController) }
            composable(Routes.Image) { ImageScreen(navController) }
            composable(Routes.Data) { DeviceListScreen(viewModel) }
            /*composable(Routes.Detail + "/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                id?.let { DetailScreen(navController, it) }
            }*/
        }
    }

    @Composable
    fun LoginScreen(navController: NavHostController) {
        val context = LocalContext.current
        val launcher = rememberFirebaseAuthLauncher(
            onAuthComplete = {
                navController.navigate(Routes.Home) {
                    popUpTo(Routes.Login) { inclusive = true }
                }
            },
            onAuthError = { Log.e("Login", "Auth failed") }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1021082948306-gimgst140rhfprpr2lutcse2k2tl1heu.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            val signInIntent = googleSignInClient.signInIntent

            Button(onClick = {
                launcher.launch(signInIntent)
            }) {
                Text("Sign in with Google")
            }
        }
    }

    @Composable
    fun rememberFirebaseAuthLauncher(
        onAuthComplete: (FirebaseUser?) -> Unit,
        onAuthError: (Exception) -> Unit
    ): ManagedActivityResultLauncher<Intent, ActivityResult> {
        val context = LocalContext.current

        return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { authResult ->
                        if (authResult.isSuccessful) {
                            onAuthComplete(FirebaseAuth.getInstance().currentUser)
                        } else {
                            onAuthError(authResult.exception ?: Exception("Auth failed"))
                        }
                    }
            } catch (e: ApiException) {
                onAuthError(e)
            }
        }
    }

    @Composable
    fun ImageScreen(navController: NavHostController) {
        val context = LocalContext.current
        val imageUri = remember { mutableStateOf<Uri?>(null) }

        val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            if (it != null) {
                val bitmap = it
                val uri = saveBitmapToCache(context, bitmap)
                imageUri.value = uri
            }
        }

        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri.value = it }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { cameraLauncher.launch() }) {
                Text("Capture Image")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Select from Gallery")
            }
            Spacer(modifier = Modifier.height(16.dp))
            imageUri.value?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }

    fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
        val filename = "temp_image.png"
        val cachePath = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(cachePath, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        return contentUri
    }

    @Composable
    fun HomeScreen(navController: NavHostController) {
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
                        sendNotification()
                    } else {
                        Toast.makeText(this@MainActivity, "Not enable", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    private fun sendNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hello!")
            .setContentText("This is a local notification.")
            .setLocalOnly(true)
            .setContentIntent(pendingIntent).setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for test notifications"
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
