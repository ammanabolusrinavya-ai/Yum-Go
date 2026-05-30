package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.models.MockData
import com.example.models.Restaurant
import com.example.ui.theme.GlassOutline
import com.example.ui.components.RestaurantCardSkeleton
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.models.MockData
import com.example.models.Restaurant
import com.example.ui.theme.GlassOutline

data class Coupon(
    val title: String, 
    val subtitle: String, 
    val code: String, 
    val rules: String, 
    val eligibility: String, 
    val expiration: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRestaurantClick: (String) -> Unit
) {
    var selectedCoupon by remember { mutableStateOf<Coupon?>(null) }
    
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000)
        isLoading = false
    }

    var searchQuery by remember { mutableStateOf("") }
    var vegOnly by remember { mutableStateOf(false) }
    var highRating by remember { mutableStateOf(false) }
    var costLowToHigh by remember { mutableStateOf(false) }

    val filteredRestaurants = remember(searchQuery, vegOnly, highRating, costLowToHigh) {
        var list = MockData.restaurants
        if (searchQuery.isNotBlank()) {
            list = list.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.cuisines.any { c -> c.contains(searchQuery, ignoreCase = true) } 
            }
        }
        if (vegOnly) list = list.filter { it.isVegOnly }
        if (highRating) list = list.filter { it.rating >= 4.0 }
        if (costLowToHigh) list = list.sortedBy { it.costForOne }
        list
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Home", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                            Text("123 Main St, New York, NY", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                    ) {
                        AsyncImage(model = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=200&q=80", contentDescription = "Profile", contentScale = ContentScale.Crop)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Sticky Search and Filters
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(bottom = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search \"biryani\" or \"pizza\"") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = GlassOutline
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = vegOnly,
                            onClick = { vegOnly = !vegOnly },
                            label = { Text("Veg Only", fontWeight = FontWeight.Bold) },
                            leadingIcon = if (vegOnly) { { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) } } else null
                        )
                    }
                    item {
                        FilterChip(
                            selected = highRating,
                            onClick = { highRating = !highRating },
                            label = { Text("Rating 4.0+", fontWeight = FontWeight.Bold) },
                            leadingIcon = if (highRating) { { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) } } else null
                        )
                    }
                    item {
                        FilterChip(
                            selected = costLowToHigh,
                            onClick = { costLowToHigh = !costLowToHigh },
                            label = { Text("Cost: Low to High", fontWeight = FontWeight.Bold) },
                            leadingIcon = if (costLowToHigh) { { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) } } else null
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val banners = listOf(
                        Coupon("50% OFF", "UPTO ₹100", "WELCOME50", "Apply at checkout to get 50% max ₹100.", "Valid for new users only.", "Expires in 2 days"),
                        Coupon("FREE DELIVERY", "ON ORDERS ABOVE ₹199", "TRYNEW", "Get free delivery on orders above ₹199.", "Valid for all users.", "Expires today"),
                        Coupon("FLAT 20% OFF", "UPTO ₹50", "CRAZY20", "Flat 20% off on your order.", "Valid on select restaurants.", "Expires in 1 week")
                    )
                    items(banners.size) { index ->
                        val banner = banners[index]
                        Surface(
                            modifier = Modifier
                                .width(300.dp)
                                .height(160.dp)
                                .clickable { selectedCoupon = banner }
                                .testTag("coupon_banner_${index}"),
                            shape = RoundedCornerShape(24.dp),
                            color = if (index % 2 == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                            border = BorderStroke(1.dp, GlassOutline)
                        ) {
                            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(banner.title, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineMedium, color = if (index % 2 == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer)
                                    Spacer(Modifier.height(8.dp))
                                    Text(banner.subtitle, fontWeight = FontWeight.Bold, color = if (index % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Use code ${banner.code}", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "What's on your mind?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    val categories = listOf("Burgers" to "🍔", "Pizza" to "🍕", "Sushi" to "🍣", "Healthy" to "🥗", "Desserts" to "🍰", "Biryani" to "🥘", "Drinks" to "🥤")
                    items(categories, key = { "cat_${it.first}" }) { category ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, GlassOutline),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(category.second, style = MaterialTheme.typography.headlineMedium)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(category.first, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Top brands for you",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val topBrands = MockData.restaurants.take(3)
                    items(topBrands, key = { "brand_${it.id}" }) { brand ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onRestaurantClick(brand.id) }) {
                            Surface(
                                shape = CircleShape,
                                border = BorderStroke(1.dp, GlassOutline),
                                modifier = Modifier.size(80.dp)
                            ) {
                                AsyncImage(model = brand.imageUrl, contentDescription = brand.name, contentScale = ContentScale.Crop)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(brand.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text(brand.deliveryTimeInfo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "${filteredRestaurants.size} restaurants to explore",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
            
            if (isLoading) {
                items(3) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        RestaurantCardSkeleton()
                    }
                }
            } else {
                items(filteredRestaurants, key = { "rest_${it.id}" }) { restaurant ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        RestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.id) }
                        )
                    }
                }
            }
            
            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    selectedCoupon?.let { coupon ->
        AlertDialog(
            onDismissRequest = { selectedCoupon = null },
            title = { Text(text = coupon.title, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                    Text(text = "Code: ${coupon.code}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Details", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text(text = coupon.subtitle, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Rules", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text(text = coupon.rules, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Eligibility", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text(text = coupon.eligibility, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Expiration", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text(text = coupon.expiration, style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedCoupon = null },
                    modifier = Modifier.testTag("coupon_close_button")
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, GlassOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                if (restaurant.offers.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = restaurant.offers.first(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = Color(0xFF1B5E20),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = restaurant.rating.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = Color.White)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color.White, modifier = Modifier.size(12.dp))
                       }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = restaurant.cuisines.joinToString(" • "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "₹${restaurant.costForOne} for one",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = GlassOutline)
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = restaurant.deliveryTimeInfo,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

