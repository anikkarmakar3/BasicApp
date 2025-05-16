package com.example.pdfrendercompose.screens

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.pdfrendercompose.presenter.DeviceViewModel
import com.example.pdfrendercompose.route.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavHostController, viewModel: DeviceViewModel) {
    val context = LocalContext.current
    val launcher = rememberFirebaseAuthLauncher(
        viewModel,
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
    viewModel: DeviceViewModel,
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
                        viewModel.insertUser(authResult.result.user?.displayName?:"",authResult.result.user?.email?:"")
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


