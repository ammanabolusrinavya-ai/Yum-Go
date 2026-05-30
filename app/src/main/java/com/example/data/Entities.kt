package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restaurantId: String,
    val restaurantName: String,
    val date: Long,
    val itemsJson: String, // Stored as JSON string
    val totalAmount: Double
)

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1, // Singleton profile
    val name: String,
    val email: String,
    val phone: String
)

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val menuItemId: String,
    val restaurantId: String,
    val quantity: Int,
    val itemJson: String // Serialized MenuItem
)
