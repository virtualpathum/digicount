package com.digiworld.digicount

import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.digiworld.digicount.system.AppNavGraph
import com.digiworld.digicount.system.SignInViewModel
import com.digiworld.digicount.ui.theme.DigiCountTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val viewModel: SignInViewModel by viewModels()

    // One Tap client
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    // Activity result launcher for Google Sign-In
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken

            if (idToken != null) {
                Log.d(TAG, "Got ID token: $idToken")
                viewModel.signInWithGoogle(idToken)
            } else {
                Log.d(TAG, "No ID token!")
                viewModel.postError("No ID token received")
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Sign-in failed: ${e.localizedMessage}", e)
            viewModel.postError(e.localizedMessage ?: "Sign-in failed")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception: ${e.localizedMessage}", e)
            viewModel.postError("Sign-in failed: ${e.localizedMessage}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Google Sign-In
        setupGoogleSignIn()

        setContent {
            DigiCountTheme {
                AppNavGraph(
                    viewModel = viewModel,
                    onGoogleSignIn = { launchGoogleSignIn() }
                )
            }
        }
    }

    private fun setupGoogleSignIn() {
        try {
            // Initialize One Tap client
            oneTapClient = Identity.getSignInClient(this)

            // Configure sign-in request
            signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(BuildConfig.GOOGLE_OAUTH_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(true)
                .build()

            Log.d(TAG, "Google Sign-In setup complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up Google Sign-In: ${e.localizedMessage}", e)
            Toast.makeText(this, "Error setting up authentication", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchGoogleSignIn() {
        try {
            Log.d(TAG, "Beginning Google Sign-In flow")
            viewModel.setLoading() // Set loading state

            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        Log.d(TAG, "Sign-in request successful, launching intent")
                        val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        signInLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}", e)
                        viewModel.postError("Couldn't start sign-in process: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // Handle standard Google Sign In if One Tap fails
                    Log.e(TAG, "One Tap sign-in failed: ${e.localizedMessage}", e)
                    fallbackToGoogleSignIn()
                }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception in sign-in flow: ${e.localizedMessage}", e)
            viewModel.postError("Sign-in failed: ${e.localizedMessage}")
        }
    }

    private fun fallbackToGoogleSignIn() {
        Log.d(TAG, "Attempting fallback to standard Google Sign-In")

        // Create a GoogleSignInOptions object
        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
            com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(BuildConfig.GOOGLE_OAUTH_CLIENT_ID)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options
        val googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso)

        // Launch the sign-in intent
        val signInIntent = googleSignInClient.signInIntent
        standardSignInLauncher.launch(signInIntent)
    }

    // Activity result launcher for standard Google Sign-In
    private val standardSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            if (idToken != null) {
                Log.d(TAG, "Standard sign-in successful, got ID token")
                viewModel.signInWithGoogle(idToken)
            } else {
                Log.d(TAG, "Standard sign-in: No ID token!")
                viewModel.postError("No ID token received")
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Standard sign-in failed: ${e.localizedMessage}", e)
            viewModel.postError("Sign-in failed: ${e.localizedMessage ?: e.statusCode.toString()}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception: ${e.localizedMessage}", e)
            viewModel.postError("Sign-in failed: ${e.localizedMessage}")
        }
    }
}
