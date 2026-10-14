package com.example.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppThemeMode
import com.example.data.model.MealType
import com.example.data.model.MessPlan
import com.example.ui.GrubViewModel

@Composable
fun SettingsScreen(
    viewModel: GrubViewModel
) {
    val preferences = viewModel.preferences
    val currentTheme by viewModel.themeMode.collectAsState()
    val currentHostel = preferences.selectedHostel
    val currentMessPlan = preferences.selectedMessPlan

    var showTimingModal by remember { mutableStateOf(false) }
    var showResetConfirmation by remember { mutableStateOf(false) }

    var mhExpanded by remember { mutableStateOf(false) }
    var lhExpanded by remember { mutableStateOf(false) }

    val mhBlocks = listOf("MH 1", "MH 2", "MH 3", "MH 4", "MH 5", "MH 6", "MH 7")
    val lhBlocks = listOf("LH 1", "LH 2", "LH 3", "LH 4")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = "Preferences & Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Configure visual theme, mess plans & hostel timings",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Switcher Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFFFF8A00))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Visual Theme Mode",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AppThemeMode.values().forEach { mode ->
                        val isSelected = currentTheme == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setThemeMode(mode) }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = mode.title,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFFFF8A00) else Color(0xFFCBD5E1)
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setThemeMode(mode) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF6B00))
                            )
                        }
                    }
                }
            }

            // Mess Plan Switcher Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = Color(0xFFFF8A00))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mess Plan Category",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MessPlan.values().forEach { plan ->
                            val isSel = currentMessPlan == plan
                            Button(
                                onClick = { viewModel.setSelectedMessPlan(plan) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSel) Color(0xFFFF6B00) else Color(0xFF1E293B)
                                ),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "${plan.emoji} ${plan.title.replace(" Mess", "")}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Hostel Residence Switcher Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Apartment, contentDescription = null, tint = Color(0xFFFF8A00))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hostel Residence",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            color = Color(0xFF261408),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C2D12))
                        ) {
                            Text(
                                text = "Current: $currentHostel",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF8A00),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Expandable MH
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                mhExpanded = !mhExpanded
                                if (mhExpanded) lhExpanded = false
                            },
                        color = Color(0xFF131B2A)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Men's Hostel (MH 1 to MH 7)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Icon(if (mhExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                            }
                            AnimatedVisibility(visible = mhExpanded) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    mhBlocks.take(4).forEach { b ->
                                        Button(
                                            onClick = { viewModel.setSelectedHostel(b) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (currentHostel == b) Color(0xFFFF6B00) else Color(0xFF1E293B)
                                            ),
                                            contentPadding = PaddingValues(2.dp)
                                        ) {
                                            Text(b, fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Expandable LH
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                lhExpanded = !lhExpanded
                                if (lhExpanded) mhExpanded = false
                            },
                        color = Color(0xFF131B2A)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Ladies Hostel (LH 1 to LH 4)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Icon(if (lhExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                            }
                            AnimatedVisibility(visible = lhExpanded) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    lhBlocks.forEach { b ->
                                        Button(
                                            onClick = { viewModel.setSelectedHostel(b) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (currentHostel == b) Color(0xFFFF6B00) else Color(0xFF1E293B)
                                            ),
                                            contentPadding = PaddingValues(2.dp)
                                        ) {
                                            Text(b, fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Customizable Meal Timings Modal Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimingModal = true },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFFFF8A00))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Customize Meal Timings",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Adjust Breakfast, Lunch, Snacks & Dinner slots",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
                }
            }

            // Reset App Data Button
            Button(
                onClick = { showResetConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_app_data_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFEF4444))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset App Data & Go to Welcome Screen", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Meal Timing Customization Dialog
    if (showTimingModal) {
        Dialog(onDismissRequest = { showTimingModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Customize Meal Timings ⏰",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Set dining hall open & close hours (24h format HH:MM)",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MealType.values().forEach { m ->
                        val timing = preferences.getMealTiming(m)
                        var startVal by remember { mutableStateOf(timing.startTime) }
                        var endVal by remember { mutableStateOf(timing.endTime) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(m.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White, modifier = Modifier.width(80.dp))
                            OutlinedTextField(
                                value = startVal,
                                onValueChange = {
                                    startVal = it
                                    viewModel.updateMealTiming(m, startVal, endVal)
                                },
                                singleLine = true,
                                modifier = Modifier.width(90.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color.White)
                            )
                            Text("-", fontSize = 16.sp, color = Color.White)
                            OutlinedTextField(
                                value = endVal,
                                onValueChange = {
                                    endVal = it
                                    viewModel.updateMealTiming(m, startVal, endVal)
                                },
                                singleLine = true,
                                modifier = Modifier.width(90.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color.White)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showTimingModal = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Timings", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Reset All App Data?") },
            text = { Text("This will clear your selected hostel, mess type, notification preferences, and alias, returning you to the first-time onboarding screen.") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmation = false
                        viewModel.resetAppDataAndRestart()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Yes, Reset & Restart", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
