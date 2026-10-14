package com.example.ui.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MealType
import com.example.data.repository.GrubRepository
import com.example.ui.GrubViewModel

@Composable
fun ReviewsScreen(
    viewModel: GrubViewModel,
    repository: GrubRepository
) {
    val reviews by repository.reviews.collectAsState()
    val preferences = viewModel.preferences
    val userAlias = preferences.chatUsername
    val userHostel = preferences.selectedHostel

    var selectedFilterMeal by remember { mutableStateOf<MealType?>(null) } // null = All

    // Bottom chat composer state
    var messageText by remember { mutableStateOf("") }
    var selectedPostMeal by remember { mutableStateOf<MealType?>(null) } // null = General
    var selectedRating by remember { mutableIntStateOf(5) }
    var hasPhotoAttached by remember { mutableStateOf(false) }
    var isMealDropdownExpanded by remember { mutableStateOf(false) }

    val filterOptions = listOf("All", "Breakfast", "Lunch", "Snacks", "Dinner")

    val filteredReviews = remember(reviews, selectedFilterMeal) {
        if (selectedFilterMeal == null) reviews
        else reviews.filter { it.mealType == selectedFilterMeal }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .statusBarsPadding()
            .testTag("reviews_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp) // space for bottom composer
        ) {
            // Header matching user screenshot
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Food Reviews & Live Chat",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Real-time honest campus mess reviews & dish photos",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Green live counter pill: 🟢 482 Live
                    Surface(
                        color = Color(0xFF062816),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF15803D)),
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "482 Live",
                                color = Color(0xFF22C55E),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Card: Posting as GrubVITian_42
                Surface(
                    color = Color(0xFF080D1A),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFFFF8A00),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Posting as ",
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = userAlias,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF8A00)
                                    )
                                }
                                Text(
                                    text = "Hostel Block: $userHostel • Totally Anonymous to Mess Staff",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Live Sync pill
                        Surface(
                            color = Color(0xFF062816),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF15803D))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Live Sync",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF22C55E)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter pills: [All], [Breakfast], [Lunch], [Snacks], [Dinner]
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterOptions) { opt ->
                        val isSelected = when (opt) {
                            "All" -> selectedFilterMeal == null
                            "Breakfast" -> selectedFilterMeal == MealType.BREAKFAST
                            "Lunch" -> selectedFilterMeal == MealType.LUNCH
                            "Snacks" -> selectedFilterMeal == MealType.SNACKS
                            "Dinner" -> selectedFilterMeal == MealType.DINNER
                            else -> false
                        }
                        Surface(
                            color = if (isSelected) Color(0xFFFF6B00) else Color(0xFF0F172A),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFFF6B00) else Color(0xFF1E293B)
                            ),
                            modifier = Modifier.clickable {
                                selectedFilterMeal = when (opt) {
                                    "Breakfast" -> MealType.BREAKFAST
                                    "Lunch" -> MealType.LUNCH
                                    "Snacks" -> MealType.SNACKS
                                    "Dinner" -> MealType.DINNER
                                    else -> null
                                }
                            }
                        ) {
                            Text(
                                text = opt,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Reviews Chat Stream
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredReviews) { review ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(18.dp))
                            .testTag("review_bubble_${review.id}"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: Avatar Initial, Name, Hostel Badge, Time, Star Rating Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Circle Initial (Orange "G")
                                    Surface(
                                        color = Color(0xFFFF6B00),
                                        shape = CircleShape,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = review.authorAlias.take(1).uppercase(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = review.authorAlias,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = Color(0xFF2B1506),
                                                shape = RoundedCornerShape(6.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C2D12))
                                            ) {
                                                Text(
                                                    text = review.hostelBlock,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFF8A00),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = review.timeAgo,
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                // Star Rating Pill: ★ 5
                                Surface(
                                    color = Color(0xFF2B1D04),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF78350F))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "★", color = Color(0xFFFBBF24), fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${review.rating}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFBBF24)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Review Message Text
                            Text(
                                text = review.comment,
                                color = Color(0xFFF1F5F9),
                                fontSize = 14.sp,
                                lineHeight = 19.sp
                            )

                            // Optional photo preview
                            if (review.dishName.contains("Biryani") || review.dishName.contains("Photo") || review.id == "rev-1") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.hero_biryani_feast),
                                        contentDescription = "Meal Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFF1E293B))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Bottom Row: Helpful like button, Verified Campus Meal
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { repository.toggleLikeReview(review.id) }
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (review.isLikedByUser) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                        contentDescription = "Helpful",
                                        tint = if (review.isLikedByUser) Color(0xFFFF6B00) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${review.likesCount} Helpful",
                                        fontSize = 11.sp,
                                        color = if (review.isLikedByUser) Color(0xFFFF6B00) else Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Text(
                                    text = "Verified Campus Meal",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }

        // DOCKED BOTTOM CHAT BAR (Exactly matching image 2)
        Surface(
            color = Color(0xFF080D1A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Top controls bar: "Meal: [General v]" and Rating Stars (★ ★ ★ ★ ★)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Meal: ",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Box {
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { isMealDropdownExpanded = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedPostMeal?.title ?: "General",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isMealDropdownExpanded,
                                onDismissRequest = { isMealDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("General Feedback") },
                                    onClick = {
                                        selectedPostMeal = null
                                        isMealDropdownExpanded = false
                                    }
                                )
                                MealType.values().forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m.title) },
                                        onClick = {
                                            selectedPostMeal = m
                                            isMealDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 5 Rating Stars
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "$i stars",
                                tint = if (i <= selectedRating) Color(0xFFFBBF24) else Color(0xFF475569),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { selectedRating = i }
                                    .padding(horizontal = 1.dp)
                            )
                        }
                    }
                }

                // If user toggled attached image preview
                if (hasPhotoAttached) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📸 Attached: Meal Plate Photo", color = Color(0xFFFF8A00), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = { hasPhotoAttached = false },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Input Row: Image button, TextField, Send Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { hasPhotoAttached = !hasPhotoAttached },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("attach_photo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Attach dish photo",
                            tint = if (hasPhotoAttached) Color(0xFFFF6B00) else Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Post live review or dish feedback...", fontSize = 13.sp, color = Color(0xFF64748B)) },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B00),
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_textfield")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Circular orange send button
                    Surface(
                        color = Color(0xFFFF6B00),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                if (messageText.isNotBlank()) {
                                    val dishTag = if (hasPhotoAttached) "Photo: Mess Dish" else (selectedPostMeal?.title ?: "Campus Meal")
                                    repository.addReview(
                                        authorAlias = userAlias,
                                        hostelBlock = userHostel,
                                        mealType = selectedPostMeal ?: MealType.LUNCH,
                                        rating = selectedRating,
                                        dishName = dishTag,
                                        comment = messageText.trim()
                                    )
                                    viewModel.triggerConfetti()
                                    messageText = ""
                                    hasPhotoAttached = false
                                }
                            }
                            .testTag("send_review_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
