package com.servicein.ui.screen.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.servicein.R
import com.servicein.data.repository.CustomerRepository
import com.servicein.domain.preference.AppPreferencesManager
import com.servicein.domain.usecase.AccountService
import com.servicein.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
    private val appPreferenceManager: AppPreferencesManager,
    private val customerRepository: CustomerRepository
): ViewModel() {
    data class LoginUiState(
        val isLoading: Boolean = false,
        val isSignedIn: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(context: Context, openAndPopUp: (String, String) -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                handleSignIn(result, openAndPopUp)
            } catch (e: GetCredentialException) {
                Log.e("LoginViewModel", "Google Sign-In cancelled or failed: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                )
            }
        }
    }

    private fun handleSignIn(
        result: GetCredentialResponse,
        openAndPopUp: (String, String) -> Unit
    ) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    viewModelScope.launch {
                        try {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            accountService.signInWithGoogle(googleIdTokenCredential.idToken).fold(
                                onSuccess = { result ->
                                    if (result.uid != null && result.displayName != null) {
                                        Log.i("LoginViewModel", "signInWithGoogle: $result")
                                        customerRepository.createCustomer(result.uid, result.displayName).fold(
                                            onSuccess = {
                                                appPreferenceManager.setCustomerId(result.uid)
                                                appPreferenceManager.setCustomerName(result.displayName)
                                                _uiState.value = _uiState.value.copy(
                                                    isLoading = false,
                                                    isSignedIn = true,
                                                )
                                                Log.i("LoginViewModel", "Customer saved: $result")
                                                openAndPopUp(Screen.Home.route, Screen.SplashScreen.route)
                                            },
                                            onFailure = { e ->
                                                Log.e("LoginViewModel", "Customer save failed: ${e.message}")
                                            }
                                        )
                                    } else {
                                        Log.e("LoginViewModel", "Invalid user data: $result")
                                    }
                                },
                                onFailure = { e ->
                                    Log.e("LoginViewModel", "signInWithGoogle: ${e.message}")
                                }
                            )
                        } catch (e: GoogleIdTokenParsingException) {
                            Log.e("LoginViewModel", "Google ID token parsing failed: ${e.message}")
                        } finally {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                            )
                        }
                    }
                } else {
                    Log.e("LoginViewModel", "Invalid credential type: ${credential.type}")
                }
            }

            else -> {
                Log.e("LoginViewModel", "Invalid credential type: ${credential.type}")
            }
        }
    }
}