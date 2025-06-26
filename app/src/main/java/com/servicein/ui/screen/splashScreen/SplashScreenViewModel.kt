package com.servicein.ui.screen.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.servicein.domain.usecase.ManagePreferencesUseCase
import com.servicein.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val preferencesUseCase: ManagePreferencesUseCase,
) : ViewModel() {
    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        viewModelScope.launch {
            if (preferencesUseCase.customerName.first() != "" && preferencesUseCase.customerId.first() != "") {
                openAndPopUp(Screen.Home.route, Screen.SplashScreen.route)
            } else {
                openAndPopUp(Screen.Login.route, Screen.SplashScreen.route)
            }
        }
    }
}