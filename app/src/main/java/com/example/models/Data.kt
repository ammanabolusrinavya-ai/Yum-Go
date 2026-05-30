package com.example.models

data class Restaurant(
    val id: String,
    val name: String,
    val cuisines: List<String>,
    val rating: Double,
    val deliveryTimeInfo: String,
    val imageUrl: String,
    val costForOne: Int,
    val isVegOnly: Boolean,
    val offers: List<String> = emptyList()
)

data class MenuItemCategory(
    val name: String,
    val items: List<MenuItem>
)

data class MenuItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val price: Double,
    val isVegetarian: Boolean,
    val imageUrl: String? = null
)

data class DiningSpot(
    val id: String,
    val name: String,
    val cuisines: List<String>,
    val rating: Double,
    val location: String,
    val imageUrl: String,
    val priceForTwo: String,
    val openHours: String,
    val features: List<String> = emptyList()
)

object MockData {
    val restaurants = listOf(
        Restaurant(
            id = "r1",
            name = "Burger King",
            cuisines = listOf("Burgers", "American"),
            rating = 4.3,
            deliveryTimeInfo = "20-30 mins",
            imageUrl = "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=500&auto=format&fit=crop&q=60",
            costForOne = 250,
            isVegOnly = false,
            offers = listOf("50% OFF up to ₹50")
        ),
        Restaurant(
            id = "r2",
            name = "Pizza Hut",
            cuisines = listOf("Pizzas", "Italian"),
            rating = 4.1,
            deliveryTimeInfo = "35-45 mins",
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500&auto=format&fit=crop&q=60",
            costForOne = 400,
            isVegOnly = false,
            offers = listOf("Flat 20% OFF")
        ),
        Restaurant(
            id = "r3",
            name = "Sushi Go",
            cuisines = listOf("Sushi", "Japanese"),
            rating = 4.7,
            deliveryTimeInfo = "30-40 mins",
            imageUrl = "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=500&auto=format&fit=crop&q=60",
            costForOne = 600,
            isVegOnly = false,
            offers = emptyList()
        ),
        Restaurant(
            id = "r4",
            name = "Taco Fiesta",
            cuisines = listOf("Tacos", "Mexican"),
            rating = 4.5,
            deliveryTimeInfo = "25-35 mins",
            imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=500&auto=format&fit=crop&q=60",
            costForOne = 300,
            isVegOnly = false,
            offers = listOf("Free Drink with Combo")
        ),
        Restaurant(
            id = "r5",
            name = "Sweet Bites Bakery",
            cuisines = listOf("Desserts", "Bakery"),
            rating = 4.8,
            deliveryTimeInfo = "15-25 mins",
            imageUrl = "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=500&auto=format&fit=crop&q=60",
            costForOne = 150,
            isVegOnly = true,
            offers = listOf("Buy 1 Get 1 Free")
        ),
        Restaurant(
            id = "r6",
            name = "Green Bowl",
            cuisines = listOf("Healthy", "Salads"),
            rating = 4.6,
            deliveryTimeInfo = "15-25 mins",
            imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500&auto=format&fit=crop&q=60",
            costForOne = 350,
            isVegOnly = true,
            offers = emptyList()
        ),
        Restaurant(
            id = "r7",
            name = "Curry House",
            cuisines = listOf("Indian", "Biryani"),
            rating = 4.4,
            deliveryTimeInfo = "35-50 mins",
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500&auto=format&fit=crop&q=60",
            costForOne = 200,
            isVegOnly = false,
            offers = listOf("10% OFF on all orders")
        ),
        Restaurant(
            id = "r8",
            name = "Pasta Bella",
            cuisines = listOf("Italian", "Pasta"),
            rating = 4.7,
            deliveryTimeInfo = "25-40 mins",
            imageUrl = "https://images.unsplash.com/photo-1555949258-eb67b1ef0ceb?w=500&auto=format&fit=crop&q=60",
            costForOne = 500,
            isVegOnly = true,
            offers = listOf("Free Garlic Bread")
        )
    )

    val menuByRestaurant = mapOf(
        "r1" to listOf(
            MenuItemCategory(
                name = "Whopper",
                items = listOf(
                    MenuItem("m1", "r1", "Veg Whopper", "Our signature veg whopper", 4.99, true),
                    MenuItem("m2", "r1", "Chicken Whopper", "Classic flame-grilled chicken", 5.99, false)
                )
            ),
            MenuItemCategory(
                name = "Sides",
                items = listOf(
                    MenuItem("m3", "r1", "Fries", "Crispy french fries", 2.99, true)
                )
            )
        ),
        "r2" to listOf(
            MenuItemCategory(
                name = "Pizzas",
                items = listOf(
                    MenuItem("m4", "r2", "Margherita", "Classic cheese pizza", 8.99, true),
                    MenuItem("m5", "r2", "Pepperoni", "Double pepperoni pizza", 10.99, false),
                    MenuItem("m13", "r2", "BBQ Chicken", "BBQ sauce, grilled chicken", 12.99, false),
                    MenuItem("m14", "r2", "Veggie Supreme", "Peppers, onions, mushrooms", 11.99, true)
                )
            )
        ),
        "r3" to listOf(
            MenuItemCategory(
                name = "Rolls",
                items = listOf(
                    MenuItem("m6", "r3", "California Roll", "Crab, avocado, cucumber", 6.99, false),
                    MenuItem("m7", "r3", "Spicy Tuna", "Fresh tuna with spicy mayo", 7.99, false)
                )
            )
        ),
        "r4" to listOf(
            MenuItemCategory(
                name = "Tacos",
                items = listOf(
                    MenuItem("m8", "r4", "Chicken Taco", "Grilled chicken, salsa, cilantro", 3.99, false),
                    MenuItem("m9", "r4", "Beef Taco", "Ground beef, cheese, lettuce", 4.49, false),
                    MenuItem("m10", "r4", "Veggie Taco", "Black beans, corn, avocado", 3.49, true)
                )
            )
        ),
        "r5" to listOf(
            MenuItemCategory(
                name = "Desserts",
                items = listOf(
                    MenuItem("m11", "r5", "Chocolate Cake", "Rich dark chocolate slice", 5.99, true),
                    MenuItem("m12", "r5", "Cheesecake", "New York style cheesecake", 6.49, true)
                )
            )
        ),
        "r6" to listOf(
            MenuItemCategory(
                name = "Salads",
                items = listOf(
                    MenuItem("m15", "r6", "Caesar Salad", "Classic Caesar with croutons", 7.99, true),
                    MenuItem("m16", "r6", "Quinoa Bowl", "Mixed greens, quinoa, lemon vinaigrette", 9.49, true)
                )
            )
        ),
        "r7" to listOf(
            MenuItemCategory(
                name = "Biryani",
                items = listOf(
                    MenuItem("m17", "r7", "Chicken Biryani", "Aromatic basmati rice with spiced chicken", 12.99, false),
                    MenuItem("m18", "r7", "Veg Biryani", "Fragrant rice cooked with mixed vegetables", 10.99, true)
                )
            )
        ),
        "r8" to listOf(
            MenuItemCategory(
                name = "Pasta",
                items = listOf(
                    MenuItem("m19", "r8", "Penne Arrabbiata", "Spicy tomato sauce with olives", 11.49, true),
                    MenuItem("m20", "r8", "Fettuccine Alfredo", "Creamy parmesan sauce", 12.99, true)
                )
            )
        )
    )

    val diningSpots = listOf(
        DiningSpot(
            id = "d1",
            name = "The Luxe Cafe",
            cuisines = listOf("Continental", "Cafe"),
            rating = 4.8,
            location = "Downtown Avenue",
            imageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=500&auto=format&fit=crop&q=60",
            priceForTwo = "₹800",
            openHours = "10 AM - 11 PM",
            features = listOf("Outdoor Seating", "Live Music", "Free WiFi")
        ),
        DiningSpot(
            id = "d2",
            name = "Seaside Grille",
            cuisines = listOf("Seafood", "Italian"),
            rating = 4.6,
            location = "Ocean Drive",
            imageUrl = "https://images.unsplash.com/photo-1544148103-0773bf10d330?w=500&auto=format&fit=crop&q=60",
            priceForTwo = "₹1500",
            openHours = "12 PM - 10 PM",
            features = listOf("Sea View", "Bar Available", "Romantic")
        ),
        DiningSpot(
            id = "d3",
            name = "Royal Indian Dining",
            cuisines = listOf("North Indian", "Mughlai"),
            rating = 4.5,
            location = "Heritage Square",
            imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500&auto=format&fit=crop&q=60",
            priceForTwo = "₹600",
            openHours = "11 AM - 10:30 PM",
            features = listOf("Family Friendly", "Valet Parking")
        )
    )

}
