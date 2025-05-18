package com.digiworld.digicount.system

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class SignInState {
    object Idle : SignInState()
    object Loading : SignInState()
    data class Success(val userName: String?) : SignInState()
    data class Error(val error: String) : SignInState()
}

class SignInViewModel : ViewModel() {
    private val TAG = "SignInViewModel"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState: StateFlow<SignInState> = _signInState

    fun setLoading() {
        _signInState.value = SignInState.Loading
    }

    fun signInWithGoogle(idToken: String?) {
        if (idToken == null) {
            Log.e(TAG, "No token received")
            return postError("No token received")
        }

        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            try {
                Log.d(TAG, "Creating credential")
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                Log.d(TAG, "Signing in with credential")
                val result = firebaseAuth.signInWithCredential(credential).await()

                Log.d(TAG, "Sign-in successful: ${result.user?.displayName}")
                _signInState.value = SignInState.Success(result.user?.displayName)
            } catch (e: Exception) {
                Log.e(TAG, "Authentication error: ${e.localizedMessage}", e)
                postError(e.localizedMessage ?: "Authentication error")
            }
        }
    }

    fun postError(message: String) {
        Log.e(TAG, "Error: $message")
        _signInState.value = SignInState.Error(message)
    }

    // Check if user is already signed in
    fun checkCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "User already signed in: ${currentUser.displayName}")
            _signInState.value = SignInState.Success(currentUser.displayName)
        }
    }

    // Sign out
    fun signOut() {
        firebaseAuth.signOut()
        _signInState.value = SignInState.Idle
    }
}

