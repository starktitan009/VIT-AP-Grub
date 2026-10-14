package com.example.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.AppScreen
import com.example.ui.GrubViewModel
import com.example.ui.MainNavTab
import com.example.ui.calendar.CalendarSearchModal
import com.example.ui.common.ConfettiOverlay
import com.example.ui.common.DisciplinaryHoldDialog
import com.example.ui.common.RewardedVideoAdDialog
import com.example.ui.discover.DiscoverScreen
import com.example.ui.foodplaza.FoodPlazaScreen
import com.example.ui.home.HomeScreen
import com.example.ui.reviews.ReviewsScreen
import com.example.ui.settings.SettingsScreen

@Composable
fun GrubMainApp(
    viewModel: GrubViewModel
) {
    val currentTab by viewModel.currentNavTab.collectAsState()
    val isCalendarOpen by viewModel.isCalendarModalVisible.collectAsState()
    val isRewardedAdActive by viewModel.isRewardedAdActive.collectAsState()
    val adCountdown by viewModel.adCountdown.collectAsState()
    val confettiTrigger by viewModel.confettiTrigger.collectAsState()
    val disciplinaryHold by viewModel.repository.disciplinaryHold.collectAsState()
    val userAlias = viewModel.preferences.chatUsername

    // Back button handling
    BackHandler(enabled = currentTab != MainNavTab.HOME) {
        viewModel.navigateNavTab(MainNavTab.HOME)
    }

    Scaffold(
        containerColor = Color(0xFF000000),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF080D1A),
                contentColor = Color(0xFFFF6B00),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("grub_bottom_navigation")
            ) {
                MainNavTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateNavTab(tab) },
                        label = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        icon = {
                            val iconVector = when (tab) {
                                MainNavTab.HOME -> if (isSelected) Icons.Filled.Restaurant else Icons.Outlined.Restaurant
                                MainNavTab.FOOD_PLAZA -> if (isSelected) Icons.Filled.Storefront else Icons.Outlined.Storefront
                                MainNavTab.REVIEWS -> if (isSelected) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline
                                MainNavTab.DISCOVER -> if (isSelected) Icons.Filled.Explore else Icons.Outlined.Explore
                                MainNavTab.SETTINGS -> if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings
                            }
                            Icon(imageVector = iconVector, contentDescription = tab.label)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF6B00),
                            selectedTextColor = Color(0xFFFF6B00),
                            indicatorColor = Color(0xFF261408),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainNavTab.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onOpenCalendar = { viewModel.openCalendarModal() },
                    onSecretAdminTrigger = { viewModel.navigateToScreen(AppScreen.ADMIN_PORTAL) }
                )
                MainNavTab.FOOD_PLAZA -> FoodPlazaScreen(
                    repository = viewModel.repository
                )
                MainNavTab.REVIEWS -> ReviewsScreen(
                    viewModel = viewModel,
                    repository = viewModel.repository
                )
                MainNavTab.DISCOVER -> DiscoverScreen(
                    repository = viewModel.repository
                )
                MainNavTab.SETTINGS -> SettingsScreen(
                    viewModel = viewModel
                )
            }

            // Interactive Calendar Modal
            if (isCalendarOpen) {
                CalendarSearchModal(
                    viewModel = viewModel,
                    repository = viewModel.repository,
                    onDismiss = { viewModel.closeCalendarModal() }
                )
            }

            // Rewarded Video Ad Modal Simulator
            if (isRewardedAdActive) {
                RewardedVideoAdDialog(
                    secondsRemaining = adCountdown,
                    onDismiss = { viewModel.dismissRewardedAd() }
                )
            }

            // Disciplinary Warning Hold Dialog
            DisciplinaryHoldDialog(
                hold = disciplinaryHold,
                userAlias = userAlias,
                onAdminLoginClick = { viewModel.navigateToScreen(AppScreen.ADMIN_PORTAL) }
            )

            // Celebratory Confetti Animation
            ConfettiOverlay(triggerTime = confettiTrigger)
        }
    }
}
