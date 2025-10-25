package com.evolvarc.smartlens.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.evolvarc.smartlens.ui.alternatives.AlternativesScreen
import com.evolvarc.smartlens.ui.addproduct.AddProductScreen
import com.evolvarc.smartlens.ui.auth.LoginScreen
import com.evolvarc.smartlens.ui.main.MainScreen
import com.evolvarc.smartlens.ui.product.ProductDetailsScreen
import com.evolvarc.smartlens.ui.scanner.ScannerScreen
import com.evolvarc.smartlens.ui.search.SearchScreen

@Composable
fun SmartLensNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.popBackStack()
                },
                onContinueAsGuest = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("main") {
            MainScreen(
                onNavigateToScanner = {
                    navController.navigate(Screen.Scanner.route)
                },
                onNavigateToProduct = { barcode ->
                    navController.navigate(Screen.ProductDetails.createRoute(barcode))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onProductScanned = { barcode ->
                    navController.navigate(Screen.ProductDetails.createRoute(barcode))
                },
                onAddProduct = { barcode ->
                    navController.navigate(Screen.AddProduct.createRoute(barcode))
                },
                onHistoryClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { barcode ->
                    navController.navigate(Screen.ProductDetails.createRoute(barcode))
                }
            )
        }
        
        composable(
            route = Screen.ProductDetails.route,
            arguments = listOf(navArgument("barcode") { type = NavType.StringType })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: return@composable
            ProductDetailsScreen(
                barcode = barcode,
                onNavigateBack = { navController.popBackStack() },
                onShowAlternatives = {
                    navController.navigate(Screen.Alternatives.createRoute(barcode))
                }
            )
        }
        
        composable(
            route = Screen.AddProduct.route,
            arguments = listOf(navArgument("barcode") { type = NavType.StringType })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: return@composable
            AddProductScreen(
                barcode = barcode,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.Alternatives.route,
            arguments = listOf(navArgument("barcode") { type = NavType.StringType })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: return@composable
            AlternativesScreen(
                barcode = barcode,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
