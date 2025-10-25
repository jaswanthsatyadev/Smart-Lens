package com.evolvarc.smartlens.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evolvarc.smartlens.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val userId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data class PhoneVerification(val verificationId: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private var verificationId: String? = null
    
    fun initGoogleSignIn(context: Context, onSignInIntent: (Intent) -> Unit) {
        val signInIntent = googleSignInClient.signInIntent
        onSignInIntent(signInIntent)
    }
    
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            authRepository.signInWithGoogle(account).fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user.uid)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(
                        error.message ?: "Failed to sign in with Google"
                    )
                }
            )
        }
    }
    
    fun startPhoneAuth(phoneNumber: String, activity: Activity) {
        _uiState.value = AuthUiState.Loading
        
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneCredential(credential)
            }
            
            override fun onVerificationFailed(e: FirebaseException) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Phone verification failed"
                )
            }
            
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@AuthViewModel.verificationId = verificationId
                _uiState.value = AuthUiState.PhoneVerification(verificationId)
            }
        }
        
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    
    fun verifyOtp(otp: String) {
        val verificationId = this.verificationId ?: run {
            _uiState.value = AuthUiState.Error("Verification ID is null")
            return
        }
        
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneCredential(credential)
    }
    
    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            authRepository.signInWithPhoneCredential(credential).fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user.uid)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(
                        error.message ?: "Failed to sign in with phone"
                    )
                }
            )
        }
    }
    
    fun showError(message: String) {
        _uiState.value = AuthUiState.Error(message)
    }
    
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
    
    fun currentUser() = authRepository.currentUser
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            googleSignInClient.signOut()
            _uiState.value = AuthUiState.Idle
        }
    }
}
