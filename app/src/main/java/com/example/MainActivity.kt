package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.AestheticAppBackground
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CartViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
            var isDarkTheme by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(isSystemDark) }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                FoodDeliveryApp(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

@Composable
fun FoodDeliveryApp(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf("delivery", "dining", "search", "profile")

    AestheticAppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (currentRoute in bottomBarRoutes) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Delivery") },
                            label = { Text("Delivery") },
                            selected = currentRoute == "delivery",
                            onClick = {
                                navController.navigate("delivery") {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Star, contentDescription = "Dining") },
                            label = { Text("Dining") },
                            selected = currentRoute == "dining",
                            onClick = {
                                navController.navigate("dining") {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text("Search") },
                            selected = currentRoute == "search",
                            onClick = {
                                navController.navigate("search") {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") },
                            selected = currentRoute == "profile",
                            onClick = {
                                navController.navigate("profile") {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController, 
                startDestination = "delivery",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("delivery") {
                    HomeScreen(
                        onRestaurantClick = { restaurantId ->
                            navController.navigate("menu/$restaurantId")
                        }
                    )
                }
                composable("dining") {
                    com.example.ui.screens.DiningScreen(
                        onRestaurantClick = { spotId ->
                            navController.navigate("dining_details/$spotId")
                        }
                    )
                }
                composable(
                    route = "dining_details/{spotId}",
                    arguments = listOf(navArgument("spotId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val spotId = backStackEntry.arguments?.getString("spotId") ?: return@composable
                    com.example.ui.screens.DiningReservationScreen(
                        spotId = spotId,
                        onBackClick = { navController.navigateUp() }
                    )
                }
                composable("search") {
                    com.example.ui.screens.SearchScreen(
                        onRestaurantClick = { restaurantId ->
                            navController.navigate("menu/$restaurantId")
                        }
                    )
                }
                composable("profile") {
                    com.example.ui.screens.ProfileScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = onThemeToggle,
                        cartViewModel = cartViewModel,
                        onNavigateToHistory = { navController.navigate("history") }
                    )
                }
                composable("history") {
                    com.example.ui.screens.HistoryScreen(
                        cartViewModel = cartViewModel,
                        onBack = { navController.navigateUp() },
                        onNavigateToCart = { navController.navigate("cart") }
                    )
                }
                composable(
                    route = "menu/{restaurantId}",
                    arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
                    MenuScreen(
                        restaurantId = restaurantId,
                        viewModel = cartViewModel,
                        onBack = { navController.navigateUp() },
                        onNavigateToCart = { navController.navigate("cart") }
                    )
                }
                composable("cart") {
                    CartScreen(
                        viewModel = cartViewModel,
                        onBack = { navController.navigateUp() },
                        onCheckout = { 
                            navController.navigate("checkout")
                        }
                    )
                }
                composable("checkout") {
                    CheckoutScreen(
                        viewModel = cartViewModel,
                        onBack = { navController.navigateUp() },
                        onPlaceOrder = {
                            navController.navigate("tracking") {
                                popUpTo("delivery") { inclusive = false }
                            }
                        }
                    )
                }
                composable("tracking") {
                    TrackingScreen(
                        onReturnHome = {
                            cartViewModel.clearCart()
                            navController.popBackStack(
                                navController.graph.findStartDestination().id,
                                inclusive = false
                            )
                        }
                    )
                }
            }
        }
    }
}
