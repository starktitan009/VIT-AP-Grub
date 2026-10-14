package com.example.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.*
import com.example.ui.GrubViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: GrubViewModel,
    onOpenCalendar: () -> Unit,
    onSecretAdminTrigger: () -> Unit
) {
    val preferences = viewModel.preferences
    val repository = viewModel.repository
    val selectedDate by viewModel.selectedDate.collectAsState()
    val schedule = remember(selectedDate) { repository.getDailySchedule(selectedDate) }
    val alerts by repository.alerts.collectAsState()

    val currentHostel = preferences.selectedHostel
    val messPlan = preferences.selectedMessPlan
    val isAdBannerEnabled = preferences.isAdBannerEnabled

    var filterByMyPlanOnly by remember { mutableStateOf(true) }
    var showAlertsModal by remember { mutableStateOf(false) }

    // Secret tap counter on header logo for invisible admin access
    var secretTapCount by remember { mutableIntStateOf(0) }

    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMin = calendar.get(Calendar.MINUTE)
    val sortedMealTypes = remember(currentHour, currentMin) {
        repository.getSortedMealTypesByTime(currentHour, currentMin)
    }

    fun getMealStatus(mealType: MealType): Pair<Boolean, String> {
        val timing = preferences.getMealTiming(mealType)
        val startParts = timing.startTime.split(":").mapNotNull { it.toIntOrNull() }
        val endParts = timing.endTime.split(":").mapNotNull { it.toIntOrNull() }
        val startVal = if (startParts.size >= 2) startParts[0] * 60 + startParts[1] else 0
        val endVal = if (endParts.size >= 2) endParts[0] * 60 + endParts[1] else 0
        val nowVal = currentHour * 60 + currentMin

        return when {
            nowVal in startVal..endVal -> {
                val minsLeft = endVal - nowVal
                true to "Serving Now • $minsLeft mins left"
            }
            nowVal < startVal -> {
                val minsUntil = startVal - nowVal
                val hours = minsUntil / 60
                val mins = minsUntil % 60
                val msg = if (hours > 0) "Opens in ${hours}h ${mins}m" else "Opens in ${mins}m"
                false to msg
            }
            else -> false to "Closed"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .statusBarsPadding()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // TOP APP BAR: Matching user screenshot (VIT AP Grub in orange, [MH 6] badge, Bell icon with orange dot, Calendar icon)
        // NO ADMIN BUTTON VISIBLE ANYWHERE!
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF000000))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            secretTapCount++
                            if (secretTapCount >= 5) {
                                secretTapCount = 0
                                onSecretAdminTrigger()
                            }
                        }
                    ) {
                        Text(
                            text = "VIT AP Grub",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFF6B00)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Surface(
                            color = Color(0xFF062816),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF15803D))
                        ) {
                            Text(
                                text = currentHostel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF22C55E),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Calendar Button
                        IconButton(
                            onClick = onOpenCalendar,
                            modifier = Modifier.testTag("calendar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Calendar",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Notification Bell with Orange Dot
                        Box(
                            modifier = Modifier
                                .clickable { showAlertsModal = true }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alerts",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(22.dp)
                            )
                            if (alerts.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF8A00))
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Google Ads Banner Unit Slot (as requested: sponsor/AdSense banner under header)
        if (isAdBannerEnabled) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("adsense_banner_slot"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFFBBF24),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Ad",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Campus Swiggy & Zomato Mega Deals",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Flat ₹125 OFF to VIT-AP Inavolu Gate • Code: CAMPUS125",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open Ad",
                            tint = Color(0xFFFF6B00),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Feast of the Day Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_biryani_feast),
                        contentDescription = "Today's Feast Feature",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xEE000000))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Surface(
                            color = Color(0xFFFF6B00),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "✨ OFFICIAL OCTOBER FEAST",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = schedule.specialFeastNote ?: "Royal Hyderabadi Dum Biryani Feast",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Authentic Dum Biryani, Mirchi Ka Salan, Paneer Butter Masala & Hot Gulab Jamun",
                            fontSize = 11.sp,
                            color = Color(0xFFFFE0B2)
                        )
                    }
                }
            }
        }

        // Today's Date & Plan Filter Switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today: $selectedDate",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Time-ordered meal rotation for ${schedule.dayName}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                FilterChip(
                    selected = filterByMyPlanOnly,
                    onClick = { filterByMyPlanOnly = !filterByMyPlanOnly },
                    label = {
                        Text(
                            text = if (filterByMyPlanOnly) "${messPlan.emoji} ${messPlan.title}" else "All Dishes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF261408),
                        selectedLabelColor = Color(0xFFFF8A00)
                    )
                )
            }
        }

        // TIME-ORDERED MEAL TILES
        items(sortedMealTypes) { mealType ->
            val mealMenu = schedule.meals[mealType]
            val (isServingNow, statusText) = getMealStatus(mealType)
            val timing = preferences.getMealTiming(mealType)

            val rawItems = mealMenu?.items ?: emptyList()
            val filteredItems = if (filterByMyPlanOnly) {
                rawItems.filter { item ->
                    when (messPlan) {
                        MessPlan.VEG -> item.category == DishCategory.VEG || item.category == DishCategory.COMMON
                        MessPlan.NON_VEG -> true
                        MessPlan.SPECIAL -> true
                    }
                }
            } else {
                rawItems
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = if (isServingNow) 2.dp else 1.dp,
                        color = if (isServingNow) Color(0xFFFF6B00) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .testTag("meal_card_${mealType.name.lowercase()}"),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF080D1A)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Meal Card Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (mealType) {
                                    MealType.BREAKFAST -> "🥞"
                                    MealType.LUNCH -> "🍛"
                                    MealType.SNACKS -> "☕"
                                    MealType.DINNER -> "🍗"
                                },
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = mealType.title,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (isServingNow) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = Color(0xFF22C55E),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "SERVING NOW",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${timing.startTime} - ${timing.endTime}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Surface(
                            color = if (isServingNow) Color(0x3322C55E) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isServingNow) Color(0xFF22C55E) else Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (mealMenu?.feastAnnouncement != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFF261408),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C2D12)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✨", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mealMenu.feastAnnouncement,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF8A00)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Dish List
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (item.category) {
                                                    DishCategory.NON_VEG -> Color(0xFFEF4444)
                                                    DishCategory.SPECIAL -> Color(0xFFA855F7)
                                                    DishCategory.VEG -> Color(0xFF22C55E)
                                                    DishCategory.COMMON -> Color(0xFF38BDF8)
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.name,
                                        fontSize = 14.sp,
                                        fontWeight = if (item.isSpecialFeast) FontWeight.Bold else FontWeight.Medium,
                                        color = Color.White
                                    )
                                    if (item.isSpecialFeast) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "🔥", fontSize = 12.sp)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (item.calories.isNotBlank()) {
                                        Text(
                                            text = item.calories,
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Surface(
                                        color = when (item.category) {
                                            DishCategory.NON_VEG -> Color(0x22EF4444)
                                            DishCategory.SPECIAL -> Color(0x22A855F7)
                                            DishCategory.VEG -> Color(0x2222C55E)
                                            DishCategory.COMMON -> Color(0x2238BDF8)
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = item.category.label,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (item.category) {
                                                DishCategory.NON_VEG -> Color(0xFFEF4444)
                                                DishCategory.SPECIAL -> Color(0xFFA855F7)
                                                DishCategory.VEG -> Color(0xFF22C55E)
                                                DishCategory.COMMON -> Color(0xFF38BDF8)
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Alerts Modal dialog
    if (showAlertsModal) {
        Dialog(onDismissRequest = { showAlertsModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Campus Broadcast Alerts 📢", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        IconButton(onClick = { showAlertsModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    alerts.forEach { alert ->
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(alert.title, fontWeight = FontWeight.Bold, color = Color(0xFFFF8A00), fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(alert.message, color = Color(0xFFCBD5E1), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
