package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AppDatabase
import com.example.models.MenuItem
import com.example.ui.theme.GlassOutline
import com.example.viewmodel.CartItem
import com.example.viewmodel.CartViewModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val orderDao = db.orderDao()

    val orders by orderDao.getAllOrders().collectAsState(initial = emptyList())

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Order History", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (orders.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                        Text("No past orders yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(orders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, GlassOutline)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                            val dateStr = sdf.format(Date(order.date))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(order.restaurantName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("₹${order.totalAmount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            val itemsArray = JSONArray(order.itemsJson)
                            val displayItems = mutableListOf<String>()
                            for(i in 0 until itemsArray.length()) {
                                val obj = itemsArray.getJSONObject(i)
                                displayItems.add("${obj.getInt("quantity")}x ${obj.getString("name")}")
                            }
                            
                            Text(
                                text = displayItems.joinToString(", "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    val parsedItems = mutableListOf<CartItem>()
                                    for(i in 0 until itemsArray.length()) {
                                        val obj = itemsArray.getJSONObject(i)
                                        val menuItem = MenuItem(
                                            id = obj.getString("id"),
                                            restaurantId = obj.getString("restaurantId"),
                                            name = obj.getString("name"),
                                            price = obj.getDouble("price"),
                                            description = if (obj.has("description")) obj.getString("description") else "",
                                            isVegetarian = if (obj.has("isVegetarian")) obj.getBoolean("isVegetarian") else false
                                        )
                                        parsedItems.add(CartItem(menuItem, obj.getInt("quantity")))
                                    }
                                    cartViewModel.populateCart(order.restaurantId, parsedItems)
                                    onNavigateToCart()
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                            ) {
                                Text("Reorder", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
