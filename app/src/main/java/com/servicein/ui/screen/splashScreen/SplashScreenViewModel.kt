package com.servicein.ui.screen.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicein.domain.preference.AppPreferencesManager
import com.servicein.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferencesManager
) : ViewModel() {
    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        viewModelScope.launch {
            if (appPreferenceManager.customerName.first() != "" && appPreferenceManager.customerId.first() != "") {
                openAndPopUp(Screen.Home.route, Screen.SplashScreen.route)
            } else {
                openAndPopUp(Screen.Login.route, Screen.SplashScreen.route)
            }
        }
    }
}