package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
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
import com.example.ui.theme.GlassOutline
import com.example.viewmodel.CartViewModel
import com.example.data.AppDatabase
import com.example.data.OrderEntity
import com.example.models.MockData
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onPlaceOrder: () -> Unit
) {
    val cartState by viewModel.cartState.collectAsState()
    val paymentOptions = listOf("UPI", "Credit/Debit Card", "Cash on Delivery")
    var selectedPaymentOption by remember { mutableStateOf(paymentOptions[0]) }

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val orderDao = db.orderDao()
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Payment", fontWeight = FontWeight.ExtraBold) },
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
        },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch {
                        val jsonArray = JSONArray()
                        cartState.items.values.forEach { item ->
                            val obj = JSONObject()
                            obj.put("id", item.menuItem.id)
                            obj.put("name", item.menuItem.name)
                            obj.put("price", item.menuItem.price)
                            obj.put("quantity", item.quantity)
                            obj.put("restaurantId", item.menuItem.restaurantId)
                            obj.put("isVegetarian", item.menuItem.isVegetarian)
                            obj.put("description", item.menuItem.description)
                            jsonArray.put(obj)
                        }
                        
                        val restaurantId = cartState.currentRestaurantId ?: ""
                        val restaurantName = MockData.restaurants.find { it.id == restaurantId }?.name ?: "Unknown"
                        
                        orderDao.insertOrder(
                            OrderEntity(
                                restaurantId = restaurantId,
                                restaurantName = restaurantName,
                                date = System.currentTimeMillis(),
                                itemsJson = jsonArray.toString(),
                                totalAmount = cartState.total
                            )
                        )
                        viewModel.clearCart()
                        onPlaceOrder()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Place Order", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Select Payment Method", 
                style = MaterialTheme.typography.titleLarge, 
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            paymentOptions.forEach { option ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .selectable(
                            selected = (selectedPaymentOption == option),
                            onClick = { selectedPaymentOption = option }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPaymentOption == option) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, if (selectedPaymentOption == option) MaterialTheme.colorScheme.primary else GlassOutline),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedPaymentOption == option),
                            onClick = { selectedPaymentOption = option }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
