package com.example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.ui.screens.MainHudScreen
import com.example.ui.theme.JarvisTheme
import com.example.ui.viewmodel.JarvisViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

private const val TAG = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG, "No debug token found in Intent extra.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG, "DebugAppCheckProviderFactory installed.")
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}

class MainActivity : ComponentActivity() {

    private val viewModel: JarvisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            JarvisTheme {
                // Request voice & camera permissions at startup
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { results ->
                    val audioGranted = results[Manifest.permission.RECORD_AUDIO] ?: false
                    val cameraGranted = results[Manifest.permission.CAMERA] ?: false
                    if (!audioGranted) {
                        Log.w("MainActivity", "Record audio permission not granted")
                    }
                }

                LaunchedEffect(Unit) {
                    val permissionsNeeded = mutableListOf<String>()
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
                    }
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionsNeeded.add(Manifest.permission.CAMERA)
                    }
                    if (permissionsNeeded.isNotEmpty()) {
                        permissionLauncher.launch(permissionsNeeded.toTypedArray())
                    }
                }

                MainHudScreen(
                    viewModel = viewModel,
                    onGoogleSignInClick = {
                        initiateGoogleSignIn()
                    }
                )
            }
        }
    }

    private fun initiateGoogleSignIn() {
        val credentialManager = CredentialManager.create(this)
        lifecycleScope.launch {
            try {
                // Google Sign In request
                val googleIdOption = GetSignInWithGoogleOption.Builder("YOUR_WEB_CLIENT_ID.apps.googleusercontent.com")
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(this@MainActivity, request)
                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val auth = FirebaseAuth.getInstance()
                    val authCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                    auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            viewModel.setUserDisplayName(user?.displayName ?: "Commander")
                            Toast.makeText(this@MainActivity, "Google Sign-In Successful: ${user?.displayName}", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.setUserDisplayName("Local Commander")
                            Toast.makeText(this@MainActivity, "Google Auth: Running in Local Mode", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: GetCredentialException) {
                Log.w("MainActivity", "CredentialManager: Running in offline local guest mode", e)
                viewModel.setUserDisplayName("Commander (Offline)")
                Toast.makeText(this@MainActivity, "Offline Mode: Full Local Memory Vault Active", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("MainActivity", "Sign in error", e)
                viewModel.setUserDisplayName("Commander")
                Toast.makeText(this@MainActivity, "Offline Mode Active", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
