package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.MenuItem
import com.example.models.MockData
import com.example.ui.components.QuantityToggleButton
import com.example.ui.theme.GlassOutline
import com.example.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    restaurantId: String,
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val restaurant = MockData.restaurants.find { it.id == restaurantId }
    val categories = MockData.menuByRestaurant[restaurantId] ?: emptyList()
    val cartState by viewModel.cartState.collectAsState()
    val crossRestaurantAttempt by viewModel.crossRestaurantAttempt.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clearConflict() }
    }

    if (crossRestaurantAttempt != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearConflict() },
            title = { Text("Clear Cart?") },
            text = { Text("Clear cart to add items from another restaurant?") },
            containerColor = MaterialTheme.colorScheme.surface,
            confirmButton = {
                TextButton(onClick = { viewModel.clearCartAndAdd() }) {
                    Text("Clear & Add", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearConflict() }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(restaurant?.name ?: "Menu", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            if (cartState.items.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onNavigateToCart,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp).fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${cartState.items.values.sumOf { it.quantity }} items", fontWeight = FontWeight.Bold)
                        Text("View Cart >", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            categories.forEach { category ->
                item {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(category.items, key = { "menu_${it.id}" }) { item ->
                    MenuItemRow(
                        menuItem = item,
                        quantity = cartState.items[item.id]?.quantity ?: 0,
                        onAdd = { viewModel.addToCart(item, restaurantId) },
                        onRemove = { viewModel.removeFromCart(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemRow(
    menuItem: MenuItem,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, GlassOutline),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(if (menuItem.isVegetarian) Color(0xFF4CAF50) else Color(0xFFF44336), RoundedCornerShape(2.dp)))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = menuItem.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "₹${menuItem.price}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = menuItem.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            QuantityToggleButton(
                quantity = quantity,
                onAdd = onAdd,
                onRemove = onRemove
            )
        }
    }
}
