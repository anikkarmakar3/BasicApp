package com.example.pdfrendercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pdfrendercompose.module.AppModule
import com.example.pdfrendercompose.presenter.DeviceViewModel
import com.example.pdfrendercompose.route.Routes
import com.example.pdfrendercompose.screens.DeviceListScreen
import com.example.pdfrendercompose.screens.HomeScreen
import com.example.pdfrendercompose.screens.ImageScreen
import com.example.pdfrendercompose.screens.LoginScreen
import com.example.pdfrendercompose.ui.theme.PdfRenderComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PreferenceUtils.init(this)
        val repo = AppModule.provideRepository(
            AppModule.provideApi(),
            AppModule.provideDatabase(applicationContext)
        )
        val viewModel = DeviceViewModel(repo)
        setContent {
            PdfRenderComposeTheme {
                MyApp(viewModel)
            }
        }
    }

    @Composable
    fun MyApp(viewModel: DeviceViewModel) {
        val navController = rememberNavController()
        NavHost(navController, startDestination = Routes.Login) {
            composable(Routes.Login) { LoginScreen(navController, viewModel) }
            composable(Routes.Home) { HomeScreen(navController) }
            composable(Routes.Image) { ImageScreen(navController) }
            composable(Routes.Data) { DeviceListScreen(viewModel) }
        }
    }


}