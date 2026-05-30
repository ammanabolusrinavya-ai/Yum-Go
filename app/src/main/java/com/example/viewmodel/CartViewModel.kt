package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CartEntity
import com.example.models.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int
)

data class CartState(
    val currentRestaurantId: String? = null,
    val items: Map<String, CartItem> = emptyMap(), // Map of menuItemId to CartItem
    val appliedCoupon: String? = null
) {
    val subtotal: Double
        get() = items.values.sumOf { it.menuItem.price * it.quantity }

    val discount: Double
        get() = when (appliedCoupon) {
            "WELCOME50" -> subtotal * 0.5
            "CRAZY20" -> subtotal * 0.2
            else -> 0.0
        }

    val deliveryFee: Double
        get() = if (appliedCoupon == "FREESHIP" || appliedCoupon == "TRYNEW") 0.0 else 2.99

    val total: Double
        get() = subtotal - discount + deliveryFee
}

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val cartDao = AppDatabase.getDatabase(application).cartDao()

    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    private val _crossRestaurantAttempt = MutableStateFlow<CartItem?>(null) // Contains conflicting item and restaurant ID
    val crossRestaurantAttempt = _crossRestaurantAttempt.asStateFlow()

    private val validCoupons = listOf("WELCOME50", "FREESHIP", "TRYNEW", "CRAZY20")

    init {
        viewModelScope.launch {
            val savedItems = cartDao.getCartItems()
            if (savedItems.isNotEmpty()) {
                val itemsMap = savedItems.associate { entity ->
                    val obj = JSONObject(entity.itemJson)
                    val menuItem = MenuItem(
                        id = obj.getString("id"),
                        restaurantId = entity.restaurantId,
                        name = obj.getString("name"),
                        price = obj.getDouble("price"),
                        description = if (obj.has("description")) obj.getString("description") else "",
                        isVegetarian = if (obj.has("isVegetarian")) obj.getBoolean("isVegetarian") else false,
                        imageUrl = if (obj.has("imageUrl")) obj.getString("imageUrl") else ""
                    )
                    entity.menuItemId to CartItem(menuItem, entity.quantity)
                }
                
                val currentRestaurantId = savedItems.first().restaurantId
                
                _cartState.value = CartState(
                    currentRestaurantId = currentRestaurantId,
                    items = itemsMap
                )
            }
        }
    }

    private fun saveItemToDb(cartItem: CartItem) {
        viewModelScope.launch {
            val obj = JSONObject()
            val m = cartItem.menuItem
            obj.put("id", m.id)
            obj.put("name", m.name)
            obj.put("price", m.price)
            obj.put("description", m.description)
            obj.put("isVegetarian", m.isVegetarian)
            obj.put("imageUrl", m.imageUrl)
            
            cartDao.insertCartItem(
                CartEntity(
                    menuItemId = m.id,
                    restaurantId = m.restaurantId,
                    quantity = cartItem.quantity,
                    itemJson = obj.toString()
                )
            )
        }
    }

    private fun removeItemFromDb(menuItemId: String) {
        viewModelScope.launch {
            cartDao.deleteCartItem(menuItemId)
        }
    }

    private fun clearDb() {
        viewModelScope.launch {
            cartDao.clearCart()
        }
    }

    fun applyCoupon(code: String): Boolean {
        val upperCode = code.uppercase()
        if (validCoupons.contains(upperCode)) {
            _cartState.update { it.copy(appliedCoupon = upperCode) }
            return true
        }
        return false
    }

    fun removeCoupon() {
        _cartState.update { it.copy(appliedCoupon = null) }
    }

    fun addToCart(menuItem: MenuItem, restaurantId: String) {
        val currentState = _cartState.value
        
        // Check single restaurant rule
        if (currentState.currentRestaurantId != null && currentState.currentRestaurantId != restaurantId) {
            _crossRestaurantAttempt.value = CartItem(menuItem, 1) // Store the item to be added
            return
        }

        _cartState.update { state ->
            val updatedItems = state.items.toMutableMap()
            val existing = updatedItems[menuItem.id]
            val newItem = if (existing != null) {
                existing.copy(quantity = existing.quantity + 1)
            } else {
                CartItem(menuItem, 1)
            }
            updatedItems[menuItem.id] = newItem
            
            saveItemToDb(newItem)

            state.copy(
                currentRestaurantId = restaurantId,
                items = updatedItems
            )
        }
    }

    fun removeFromCart(menuItemId: String) {
        _cartState.update { state ->
            val updatedItems = state.items.toMutableMap()
            val existing = updatedItems[menuItemId]
            if (existing != null) {
                if (existing.quantity > 1) {
                    val newItem = existing.copy(quantity = existing.quantity - 1)
                    updatedItems[menuItemId] = newItem
                    saveItemToDb(newItem)
                } else {
                    updatedItems.remove(menuItemId)
                    removeItemFromDb(menuItemId)
                }
            }
            
            // If cart is empty, clear the current restaurant lock
            val newRestaurantId = if (updatedItems.isEmpty()) null else state.currentRestaurantId
            
            state.copy(
                currentRestaurantId = newRestaurantId,
                items = updatedItems
            )
        }
    }

    fun clearCartAndAdd() {
        val attempt = _crossRestaurantAttempt.value
        if (attempt != null) {
            clearDb()
            saveItemToDb(attempt)
            _cartState.value = CartState(
                currentRestaurantId = attempt.menuItem.restaurantId,
                items = mapOf(attempt.menuItem.id to attempt)
            )
        } else {
            clearDb()
            _cartState.value = CartState()
        }
        clearConflict()
    }

    fun clearConflict() {
        _crossRestaurantAttempt.value = null
    }

    fun clearCart() {
        clearDb()
        _cartState.value = CartState()
    }

    fun populateCart(restaurantId: String, newItems: List<CartItem>) {
        clearDb()
        newItems.forEach { saveItemToDb(it) }
        
        val itemsMap = newItems.associateBy { it.menuItem.id }
        _cartState.value = CartState(
            currentRestaurantId = restaurantId,
            items = itemsMap
        )
    }
}
