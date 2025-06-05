package com.servicein.ui.screen.splashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.servicein.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreenView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(1000)
        viewModel.onAppStart(
            openAndPopUp = { route, popUp ->
                navController.navigate(route) {
                    popUpTo(popUp)
                }
            }
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 82.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "App Logo",
        )
    }
}