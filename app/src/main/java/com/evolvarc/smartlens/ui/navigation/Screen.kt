package com.evolvarc.smartlens.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object OtpVerification : Screen("otp_verification/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "otp_verification/$phoneNumber"
    }
    object Home : Screen("home")
    object Scanner : Screen("scanner")
    object Search : Screen("search")
    object ProductDetails : Screen("product/{barcode}") {
        fun createRoute(barcode: String) = "product/$barcode"
    }
    object Alternatives : Screen("alternatives/{barcode}") {
        fun createRoute(barcode: String) = "alternatives/$barcode"
    }
    object AddProduct : Screen("add_product/{barcode}") {
        fun createRoute(barcode: String) = "add_product/$barcode"
    }
    object History : Screen("history")
    object Profile : Screen("profile")
}
