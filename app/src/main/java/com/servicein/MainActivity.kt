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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.libraries.places.api.Places
import com.servicein.core.theme.ServiceInTheme
import com.servicein.ui.navigation.Screen
import com.servicein.ui.screen.chat.ChatView
import com.servicein.ui.screen.orderDetail.OrderDetailView
import com.servicein.ui.screen.history.HistoryDetailView
import com.servicein.ui.screen.history.HistoryView
import com.servicein.ui.screen.history.HistoryViewModel
import com.servicein.ui.screen.home.HomeView
import com.servicein.ui.screen.home.HomeViewModel
import com.servicein.ui.screen.home.ShopDetailView
import com.servicein.ui.screen.login.LoginView
import com.servicein.ui.screen.order.OrderLocationView
import com.servicein.ui.screen.order.OrderTypeView
import com.servicein.ui.screen.order.OrderViewModel
import com.servicein.ui.screen.splashScreen.SplashScreenView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.DIRECTIONS_API_KEY)
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
fun MyApp(navHostController: NavHostController) {
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
            val viewModel: HomeViewModel = hiltViewModel()
            HomeView(navController = navHostController, viewModel = viewModel)
        }
        composable(Screen.ShopDetail.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(Screen.Home.route)
            }
            val viewModel: HomeViewModel = hiltViewModel(parentEntry)
            ShopDetailView(navController = navHostController, viewModel = viewModel)
        }
        composable(Screen.History.route) {
            val viewModel: HistoryViewModel = hiltViewModel()
            HistoryView(navController = navHostController, viewModel = viewModel)
        }
        composable(Screen.HistoryDetail.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(Screen.History.route)
            }
            val viewModel: HistoryViewModel = hiltViewModel(parentEntry)
            HistoryDetailView(viewModel = viewModel)
        }
        composable(
            Screen.OrderType.route,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: OrderViewModel = hiltViewModel()
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            OrderTypeView(
                navController = navHostController,
                viewModel = viewModel,
                shopId = shopId
            )
        }
        composable(
            Screen.OrderLocation.route,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navHostController.getBackStackEntry(Screen.OrderType.route)
            }
            val viewModel: OrderViewModel = hiltViewModel(parentEntry)
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            OrderLocationView(
                viewModel = viewModel,
                navController = navHostController,
                shopId = shopId
            )
        }
        composable(
            Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) {
            val orderId = it.arguments?.getString("orderId") ?: ""
            OrderDetailView(orderId = orderId, navController = navHostController)
        }
        composable(
            Screen.Chat.route,
            arguments = listOf(
                navArgument("shopId") { type = NavType.StringType },
                navArgument("shopName") { type = NavType.StringType },
            ),
        ) {
            val shopId = it.arguments?.getString("shopId") ?: ""
            val shopName = it.arguments?.getString("shopName") ?: ""
            ChatView(
                shopId = shopId,
                shopName = shopName,
                navController = navHostController
            )
        }
    }
}

//TODO: sorted shops not show initially,