package com.example.ui.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.ExcelMenuParser
import com.example.ui.GrubViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: GrubViewModel,
    onBackToStudentApp: () -> Unit
) {
    val context = LocalContext.current
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsState()
    val preferences = viewModel.preferences
    val repository = viewModel.repository
    val adminToast by viewModel.adminToast.collectAsState()

    var adminUsernameInput by remember { mutableStateOf("admin") }
    var adminPasscodeInput by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    val adminTabs = listOf(
        "Excel AI Importer",
        "Manual Menu",
        "Restaurants",
        "Delivery Hotlines",
        "Campus Alerts",
        "Disciplinary Center",
        "Monetization & Ads",
        "Supabase & GitHub"
    )
    val activeTabIndex by viewModel.adminActiveTabIndex.collectAsState()

    if (!isAuthenticated) {
        // SECURED ADMIN LOGIN SCREEN
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000))
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("admin_login_screen"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.5.dp, Color(0xFFFF6B00), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A))
            ) {
                Column(
                    modifier = Modifier.padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color(0xFF261408),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🛡️ VIT-AP MESS ADMINISTRATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFF6B00),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Secured Web Portal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Authorized access for Mess Committee & Chief Warden Office",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    OutlinedTextField(
                        value = adminUsernameInput,
                        onValueChange = { adminUsernameInput = it },
                        label = { Text("Admin Username") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminPasscodeInput,
                        onValueChange = {
                            adminPasscodeInput = it
                            loginError = false
                        },
                        label = { Text("Admin Passcode") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (loginError) {
                        Text(
                            text = "Invalid credentials! Default: admin / grub2026",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    } else {
                        Text(
                            text = "Default passcode: grub2026",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val success = viewModel.authenticateAdmin(adminPasscodeInput)
                            if (!success) {
                                loginError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("admin_submit_login_button")
                    ) {
                        Text("Authenticate & Enter Portal 🔓", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onBackToStudentApp) {
                        Text("← Return to Student App", color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    } else {
        // AUTHENTICATED ADMIN CONSOLE
        Scaffold(
            containerColor = Color(0xFF000000),
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF080D1A))
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBackToStudentApp) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Column {
                                Text(
                                    text = "VIT AP Grub Admin Console",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Chief Warden & Mess Committee Portal",
                                    fontSize = 11.sp,
                                    color = Color(0xFF22C55E)
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.logoutAdmin() }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color(0xFFEF4444))
                        }
                    }

                    // Admin Navigation Tabs
                    ScrollableTabRow(
                        selectedTabIndex = activeTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFFF6B00),
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        adminTabs.forEachIndexed { idx, title ->
                            Tab(
                                selected = activeTabIndex == idx,
                                onClick = { viewModel.setAdminTab(idx) },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = if (activeTabIndex == idx) FontWeight.Bold else FontWeight.Normal,
                                        color = if (activeTabIndex == idx) Color(0xFFFF6B00) else Color(0xFF94A3B8)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("admin_dashboard_content")
            ) {
                when (activeTabIndex) {
                    0 -> ExcelMenuImporterTab(viewModel, context)
                    1 -> ManualMenuEditorTab(viewModel)
                    2 -> RestaurantManagerTab(viewModel)
                    3 -> DeliveryManagerTab(viewModel)
                    4 -> CampusAlertsTab(viewModel)
                    5 -> DisciplinaryCenterTab(viewModel)
                    6 -> MonetizationTab(viewModel)
                    7 -> SupabaseAndPublishingTab(viewModel, context)
                }

                if (adminToast != null) {
                    Surface(
                        color = Color(0xFF22C55E),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = adminToast ?: "",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

// 1. AI & Heuristic Excel Menu Importer Tab with Date, Mess Plan (Veg, NV, Special) and Menu Review & Edit
@Composable
fun ExcelMenuImporterTab(viewModel: GrubViewModel, context: Context) {
    var rawCsvText by remember { mutableStateOf(ExcelMenuParser.getSampleBlankTemplate()) }
    val generatedMenus by viewModel.generatedMenus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                    Column {
                        Text(
                            text = "Excel Menu AI Takeover API 📊",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Auto-generates separate menus for Veg, Non-Veg & Special Mess",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Template", ExcelMenuParser.getSampleBlankTemplate())
                            clipboard.setPrimaryClip(clip)
                            viewModel.showAdminToast("Template copied to clipboard!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF6B00))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Template", fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = rawCsvText,
                    onValueChange = { rawCsvText = it },
                    label = { Text("Upload or Paste Mess Spreadsheet Data") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("excel_import_textfield"),
                    textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, color = Color(0xFFCBD5E1))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.processSpreadsheetViaApi(rawCsvText) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Process with API ⚡", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (generatedMenus.isNotEmpty()) {
                        Button(
                            onClick = { viewModel.publishGeneratedMenus() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publish Menus 🚀", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Display Date, Mess Type, and Menu with inline editing
        if (generatedMenus.isNotEmpty()) {
            Text(
                text = "Generated Mess Menus (${generatedMenus.size} entries): Inspect & Edit Before Publishing",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF8A00)
            )

            generatedMenus.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📅 ${item.date} • ${item.mealType.title}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )

                            Surface(
                                color = when (item.messPlan) {
                                    MessPlan.VEG -> Color(0x3322C55E)
                                    MessPlan.NON_VEG -> Color(0x33EF4444)
                                    MessPlan.SPECIAL -> Color(0x33A855F7)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${item.messPlan.emoji} ${item.messPlan.title}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (item.messPlan) {
                                        MessPlan.VEG -> Color(0xFF22C55E)
                                        MessPlan.NON_VEG -> Color(0xFFEF4444)
                                        MessPlan.SPECIAL -> Color(0xFFA855F7)
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Menu Items (Editable):",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        var dishesInput by remember(item.dishesSummary) { mutableStateOf(item.dishesSummary) }
                        OutlinedTextField(
                            value = dishesInput,
                            onValueChange = {
                                dishesInput = it
                                viewModel.updateGeneratedMenuDishes(item.id, it)
                            },
                            singleLine = false,
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color.White)
                        )
                    }
                }
            }
        }
    }
}

// 2. Manual Menu Editor Tab
@Composable
fun ManualMenuEditorTab(viewModel: GrubViewModel) {
    val repository = viewModel.repository
    var editDate by remember { mutableStateOf("2026-10-15") }
    var selectedMeal by remember { mutableStateOf(MealType.DINNER) }
    var newDishName by remember { mutableStateOf("") }
    var newDishCat by remember { mutableStateOf(DishCategory.SPECIAL) }
    var feastNoteInput by remember { mutableStateOf("✨ Grand Royal Biryani Buffet") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Manual Menu & Feast Editor ✍️",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = editDate,
                    onValueChange = { editDate = it },
                    label = { Text("Target Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MealType.values().forEach { m ->
                        val isSel = selectedMeal == m
                        Button(
                            onClick = { selectedMeal = m },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) Color(0xFFFF6B00) else Color(0xFF1E293B)
                            ),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text(m.title.take(3), fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newDishName,
                    onValueChange = { newDishName = it },
                    label = { Text("Dish Item Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DishCategory.values().forEach { c ->
                        val isSel = newDishCat == c
                        Button(
                            onClick = { newDishCat = c },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) Color(0xFFFF6B00) else Color(0xFF1E293B)
                            ),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text(c.label, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (newDishName.isNotBlank()) {
                            repository.addMenuItemManual(
                                date = editDate,
                                mealType = selectedMeal,
                                name = newDishName,
                                category = newDishCat,
                                isFeast = newDishCat == DishCategory.SPECIAL
                            )
                            viewModel.showAdminToast("Added '$newDishName' to $editDate ${selectedMeal.title}!")
                            newDishName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Dish to Schedule", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Configure Special Feast Announcement Note:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFFFA726)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = feastNoteInput,
                    onValueChange = { feastNoteInput = it },
                    label = { Text("Announcement Ticker (e.g. Biryani & Jamun Feast)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        repository.updateFeastNote(editDate, feastNoteInput)
                        viewModel.showAdminToast("Feast note updated for $editDate!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Feast Announcement", color = Color.White)
                }
            }
        }
    }
}

// 3. Restaurant Listings Manager
@Composable
fun RestaurantManagerTab(viewModel: GrubViewModel) {
    val repository = viewModel.repository
    val eateries by repository.eateries.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var cuisineInput by remember { mutableStateOf("Biryani, Tiffins") }
    var areaZone by remember { mutableStateOf(LocationZone.NEAR_CAMPUS) }
    var hoursInput by remember { mutableStateOf("11:00 AM - 11:00 PM") }
    var phoneInput by remember { mutableStateOf("+91 98480 99999") }
    var mapsQueryInput by remember { mutableStateOf("Amaravati Restaurant") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Add New Restaurant Listing 🍽️",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Eatery Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(LocationZone.NEAR_CAMPUS, LocationZone.VIJAYAWADA).forEach { z ->
                        val isSel = areaZone == z
                        Button(
                            onClick = { areaZone = z },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) Color(0xFFFF6B00) else Color(0xFF1E293B)
                            )
                        ) {
                            Text(z.label, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cuisineInput,
                    onValueChange = { cuisineInput = it },
                    label = { Text("Cuisines (comma separated)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mapsQueryInput,
                    onValueChange = { mapsQueryInput = it },
                    label = { Text("Google Maps Query / Area") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            val newEatery = Eatery(
                                id = "admin-${System.currentTimeMillis()}",
                                name = nameInput,
                                locationZone = areaZone,
                                cuisines = cuisineInput.split(",").map { it.trim() },
                                hours = hoursInput,
                                phone = phoneInput,
                                mapsUrl = "https://www.google.com/maps/search/?api=1&query=${mapsQueryInput.replace(" ", "+")}",
                                rating = 4.5f,
                                priceRange = "₹120 - ₹280",
                                menuHighlights = listOf("Student Special Combo", "Fresh Meals")
                            )
                            repository.addEatery(newEatery)
                            viewModel.showAdminToast("Added '$nameInput'!")
                            nameInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publish Restaurant to Discover", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text(
            text = "Active Restaurant Listings (${eateries.size}):",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        eateries.forEach { e ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(e.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("${e.locationZone.label} • ${if (e.isEnabled) "Visible" else "Hidden"}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }

                    Row {
                        IconButton(onClick = { repository.toggleEatery(e.id) }) {
                            Icon(
                                if (e.isEnabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle",
                                tint = if (e.isEnabled) Color(0xFF22C55E) else Color(0xFF64748B)
                            )
                        }
                        IconButton(onClick = { repository.deleteEatery(e.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}

// 4. Campus Delivery Services Manager
@Composable
fun DeliveryManagerTab(viewModel: GrubViewModel) {
    val repository = viewModel.repository
    val services by repository.deliveryServices.collectAsState()

    var nameInput by remember { mutableStateOf("") }
    var hotlineInput by remember { mutableStateOf("+91 98480 12345") }
    var hoursInput by remember { mutableStateOf("08:00 PM - 03:00 AM") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Add Campus Delivery Hotline 🛵",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Service Name (e.g. Amaravati Night Bites)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = hotlineInput,
                    onValueChange = { hotlineInput = it },
                    label = { Text("Phone / WhatsApp Hotline") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = hoursInput,
                    onValueChange = { hoursInput = it },
                    label = { Text("Delivery Operating Hours") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            val newService = CampusDeliveryService(
                                id = "del-${System.currentTimeMillis()}",
                                name = nameInput,
                                hotline = hotlineInput,
                                whatsappNumber = hotlineInput,
                                dropPoints = listOf("MH 1-7 Lobby", "LH Gate 2"),
                                hours = hoursInput,
                                minOrder = "₹100",
                                deliveryFee = "FREE",
                                isOnline = true
                            )
                            repository.addDeliveryService(newService)
                            viewModel.showAdminToast("Added '$nameInput'!")
                            nameInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register Delivery Service", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        services.forEach { s ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(s.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Text("Hotline: ${s.hotline} • ${s.hours}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
            }
        }
    }
}

// 5. Campus Broadcast Alerts
@Composable
fun CampusAlertsTab(viewModel: GrubViewModel) {
    val repository = viewModel.repository
    var alertTitle by remember { mutableStateOf("") }
    var alertMsg by remember { mutableStateOf("") }
    var alertUrgency by remember { mutableStateOf("SPECIAL_DINNER") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Broadcast Instant Campus Alert 📢",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = alertTitle,
                    onValueChange = { alertTitle = it },
                    label = { Text("Alert Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = alertMsg,
                    onValueChange = { alertMsg = it },
                    label = { Text("Announcement Details") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (alertTitle.isNotBlank() && alertMsg.isNotBlank()) {
                            repository.postAlert(
                                BroadcastAlert(
                                    id = "alert-${System.currentTimeMillis()}",
                                    title = alertTitle,
                                    message = alertMsg,
                                    urgency = alertUrgency
                                )
                            )
                            viewModel.showAdminToast("Broadcast dispatched!")
                            alertTitle = ""
                            alertMsg = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dispatch Alert Broadcast", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 6. Student Disciplinary Warning Center
@Composable
fun DisciplinaryCenterTab(viewModel: GrubViewModel) {
    val repository = viewModel.repository
    val hold by repository.disciplinaryHold.collectAsState()

    var targetAliasInput by remember { mutableStateOf(hold.targetAlias) }
    var reasonInput by remember { mutableStateOf(hold.reason) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A0A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Disciplinary Warning Center ⚖️",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )
                Text(
                    text = "Issues an administrative hold blocking student devices until revoked.",
                    fontSize = 12.sp,
                    color = Color(0xFFFCA5A5)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = targetAliasInput,
                    onValueChange = { targetAliasInput = it },
                    label = { Text("Target Alias ('ALL' or specific student alias)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = { reasonInput = it },
                    label = { Text("Disciplinary Reason") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val newHold = DisciplinaryHold(
                                isActive = true,
                                targetAlias = targetAliasInput.ifBlank { "ALL" },
                                reason = reasonInput.ifBlank { "Violation of mess conduct" }
                            )
                            repository.updateDisciplinaryHold(newHold)
                            viewModel.showAdminToast("Hold ACTIVATED for $targetAliasInput!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Enforce Hold 🔒", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val revoked = DisciplinaryHold(isActive = false, targetAlias = "NONE", reason = "")
                            repository.updateDisciplinaryHold(revoked)
                            viewModel.showAdminToast("Hold REVOKED!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Revoke Hold 🔓", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 7. Monetization & Ads Controls
@Composable
fun MonetizationTab(viewModel: GrubViewModel) {
    val preferences = viewModel.preferences
    var adBannerEnabled by remember { mutableStateOf(preferences.isAdBannerEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Monetization & Ad Controls 💰",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Show Google AdSense Banner", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("Display sponsor unit slot under the home header", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                    Switch(
                        checked = adBannerEnabled,
                        onCheckedChange = {
                            adBannerEnabled = it
                            preferences.isAdBannerEnabled = it
                            viewModel.showAdminToast(if (it) "Banner shown" else "Banner hidden")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.triggerRewardedAd() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test 5-Sec Rewarded Ad Lock", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 8. Supabase & GitHub Publishing
@Composable
fun SupabaseAndPublishingTab(viewModel: GrubViewModel, context: Context) {
    val preferences = viewModel.preferences
    var supabaseUrlInput by remember { mutableStateOf(preferences.supabaseUrl) }
    var anonKeyInput by remember { mutableStateOf(preferences.supabaseAnonKey) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Supabase Cloud Database Config ⚡",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = supabaseUrlInput,
                    onValueChange = { supabaseUrlInput = it },
                    label = { Text("Supabase Project URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = anonKeyInput,
                    onValueChange = { anonKeyInput = it },
                    label = { Text("Supabase Anon Key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        preferences.supabaseUrl = supabaseUrlInput
                        preferences.supabaseAnonKey = anonKeyInput
                        viewModel.showAdminToast("Supabase credentials saved!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save & Sync to Cloud", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "GitHub Pages Web Portal Hosting 🌐",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "The admin panel is also provided as a standalone webpage in /admin for hosting on GitHub Pages",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("GitHub Pages", "git push origin main && gh-pages deploy")
                        clipboard.setPrimaryClip(clip)
                        viewModel.showAdminToast("GitHub deploy command copied!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Copy GitHub Pages Deploy Command", color = Color.White)
                }
            }
        }
    }
}
