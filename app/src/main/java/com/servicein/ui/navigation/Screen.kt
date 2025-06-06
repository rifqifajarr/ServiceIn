package com.servicein.ui.navigation

sealed class Screen(val route: String) {
    data object SplashScreen : Screen("splash_screen")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object ShopDetail : Screen("shop_detail")
    data object History : Screen("history")
    data object HistoryDetail : Screen("history_detail")
    data object OrderType : Screen("order_type/{shopId}") {
        fun createRoute(shopId: String?) = "order_type/$shopId"
    }
    data object OrderLocation : Screen("order_location/{shopId}") {
        fun createRoute(shopId: String?) = "order_location/$shopId"
    }
    data object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
}