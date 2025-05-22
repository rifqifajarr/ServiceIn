package com.servicein

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.google.android.libraries.places.api.Places
import com.servicein.core.theme.ServiceInTheme
import com.servicein.ui.navigation.Screen
import com.servicein.ui.screen.home.HomeView
import com.servicein.ui.screen.history.HistoryDetailView
import com.servicein.ui.screen.history.HistoryView
import com.servicein.ui.screen.history.HistoryViewModel
import com.servicein.ui.screen.home.HomeViewModel
import com.servicein.ui.screen.home.ShopDetailView
import com.servicein.ui.screen.login.LoginView
import com.servicein.ui.screen.splashScreen.SplashScreenView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        setContent {
            ServiceInTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = lightColorScheme().surface
                ) {
                    MyApp(
                        navHostController = rememberNavController(),
                    )
                }
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier, navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(Screen.SplashScreen.route) {
            SplashScreenView(navController = navHostController)
        }
        composable(Screen.Login.route) {
            LoginView(navController = navHostController)
        }
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel()
            HomeView(navController = navHostController, viewModel = viewModel)
        }
        composable(Screen.ShopDetail.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(Screen.Home.route)
            }
            val viewModel: HomeViewModel = viewModel(parentEntry)
            ShopDetailView(viewModel = viewModel)
        }
        composable(Screen.History.route) {
            val viewModel: HistoryViewModel = viewModel()
            HistoryView(navController = navHostController, viewModel = viewModel)
        }
        composable(Screen.HistoryDetail.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(Screen.History.route)
            }
            val viewModel: HistoryViewModel = viewModel(parentEntry)
            HistoryDetailView(viewModel = viewModel)
        }
    }
}