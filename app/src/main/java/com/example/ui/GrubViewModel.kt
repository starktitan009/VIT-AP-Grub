package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GrubPreferences
import com.example.data.model.*
import com.example.data.repository.ExcelMenuParser
import com.example.data.repository.GrubRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppScreen {
    STARTUP_SPLASH,
    ONBOARDING,
    MAIN_APP,
    ADMIN_PORTAL
}

enum class MainNavTab(val label: String) {
    HOME("Home"),
    FOOD_PLAZA("Plaza"),
    REVIEWS("Reviews"),
    DISCOVER("Discover"),
    SETTINGS("Settings")
}

class GrubViewModel(application: Application) : AndroidViewModel(application) {

    val preferences = GrubPreferences(application)
    val repository = GrubRepository(preferences)

    // Routing & Screen State
    private val _currentScreen = MutableStateFlow<AppScreen>(
        if (preferences.completedOnboarding) AppScreen.MAIN_APP else AppScreen.STARTUP_SPLASH
    )
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Navigation Tab
    private val _currentNavTab = MutableStateFlow(MainNavTab.HOME)
    val currentNavTab: StateFlow<MainNavTab> = _currentNavTab.asStateFlow()

    // Onboarding State
    private val _onboardingStep = MutableStateFlow(1)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    private val _tempHostel = MutableStateFlow(preferences.selectedHostel)
    val tempHostel: StateFlow<String> = _tempHostel.asStateFlow()

    private val _tempMessPlan = MutableStateFlow(preferences.selectedMessPlan)
    val tempMessPlan: StateFlow<MessPlan> = _tempMessPlan.asStateFlow()

    private val _tempNotifMeal = MutableStateFlow(preferences.notificationsUpcomingMeal)
    val tempNotifMeal: StateFlow<Boolean> = _tempNotifMeal.asStateFlow()

    private val _tempNotifFeast = MutableStateFlow(preferences.notificationsSpecialFeasts)
    val tempNotifFeast: StateFlow<Boolean> = _tempNotifFeast.asStateFlow()

    private val _tempUsername = MutableStateFlow(preferences.chatUsername)
    val tempUsername: StateFlow<String> = _tempUsername.asStateFlow()

    // Theme Mode
    private val _themeMode = MutableStateFlow(preferences.themeMode)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Calendar & Schedule
    private val _selectedDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()).let {
        if (it.startsWith("2026-10")) it else "2026-10-14"
    })
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _isCalendarModalVisible = MutableStateFlow(false)
    val isCalendarModalVisible: StateFlow<Boolean> = _isCalendarModalVisible.asStateFlow()

    // Timing customizer modal
    private val _isTimingCustomizerVisible = MutableStateFlow(false)
    val isTimingCustomizerVisible: StateFlow<Boolean> = _isTimingCustomizerVisible.asStateFlow()

    // Rewarded Ad Lock State
    private val _isRewardedAdActive = MutableStateFlow(false)
    val isRewardedAdActive: StateFlow<Boolean> = _isRewardedAdActive.asStateFlow()

    private val _adCountdown = MutableStateFlow(5)
    val adCountdown: StateFlow<Int> = _adCountdown.asStateFlow()

    // Confetti effect trigger
    private val _confettiTrigger = MutableStateFlow(0L)
    val confettiTrigger: StateFlow<Long> = _confettiTrigger.asStateFlow()

    // Admin State
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _adminActiveTabIndex = MutableStateFlow(0)
    val adminActiveTabIndex: StateFlow<Int> = _adminActiveTabIndex.asStateFlow()

    // Generated Mess Plan Menus (Veg, NV, Special) from API
    private val _generatedMenus = MutableStateFlow<List<ExcelMenuParser.GeneratedMessPlanMenu>>(emptyList())
    val generatedMenus: StateFlow<List<ExcelMenuParser.GeneratedMessPlanMenu>> = _generatedMenus.asStateFlow()

    private val _adminToast = MutableStateFlow<String?>(null)
    val adminToast: StateFlow<String?> = _adminToast.asStateFlow()

    private val aliasNames = listOf(
        "GrubVITian_42", "HungryBison", "CrispyDosa", "GrubMaster", "ChaiLover",
        "BiryaniFiend", "NightScholar", "MidnightMuncher", "SamosaSlayer"
    )

    fun navigateToScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun navigateNavTab(tab: MainNavTab) {
        _currentNavTab.value = tab
    }

    fun setStartupFinished() {
        if (preferences.completedOnboarding) {
            _currentScreen.value = AppScreen.MAIN_APP
        } else {
            _onboardingStep.value = 1
            _currentScreen.value = AppScreen.ONBOARDING
        }
    }

    // Onboarding Actions
    fun setTempHostel(hostel: String) {
        _tempHostel.value = hostel
    }

    fun setTempMessPlan(plan: MessPlan) {
        _tempMessPlan.value = plan
    }

    fun toggleTempNotifMeal() {
        _tempNotifMeal.value = !_tempNotifMeal.value
    }

    fun toggleTempNotifFeast() {
        _tempNotifFeast.value = !_tempNotifFeast.value
    }

    fun setTempUsername(username: String) {
        _tempUsername.value = username
    }

    fun randomizeAlias() {
        _tempUsername.value = aliasNames.random()
    }

    fun nextOnboardingStep() {
        if (_onboardingStep.value < 4) {
            _onboardingStep.value += 1
        }
    }

    fun prevOnboardingStep() {
        if (_onboardingStep.value > 1) {
            _onboardingStep.value -= 1
        }
    }

    fun completeOnboarding() {
        preferences.selectedHostel = _tempHostel.value
        preferences.selectedMessPlan = _tempMessPlan.value
        preferences.notificationsUpcomingMeal = _tempNotifMeal.value
        preferences.notificationsSpecialFeasts = _tempNotifFeast.value
        preferences.chatUsername = _tempUsername.value.ifBlank { "GrubVITian_42" }
        preferences.completedOnboarding = true

        triggerConfetti()
        viewModelScope.launch {
            delay(400)
            _currentScreen.value = AppScreen.MAIN_APP
        }
    }

    fun triggerConfetti() {
        _confettiTrigger.value = System.currentTimeMillis()
    }

    // Settings actions
    fun setThemeMode(mode: AppThemeMode) {
        preferences.themeMode = mode
        _themeMode.value = mode
    }

    fun setSelectedHostel(hostel: String) {
        preferences.selectedHostel = hostel
        _tempHostel.value = hostel
    }

    fun setSelectedMessPlan(plan: MessPlan) {
        preferences.selectedMessPlan = plan
        _tempMessPlan.value = plan
    }

    fun resetAppDataAndRestart() {
        preferences.resetAppData()
        _tempHostel.value = "MH 6"
        _tempMessPlan.value = MessPlan.NON_VEG
        _tempUsername.value = "GrubVITian_42"
        _themeMode.value = AppThemeMode.AMOLED
        _onboardingStep.value = 1
        _currentScreen.value = AppScreen.STARTUP_SPLASH
    }

    // Calendar Modal
    fun openCalendarModal() {
        _isCalendarModalVisible.value = true
    }

    fun closeCalendarModal() {
        _isCalendarModalVisible.value = false
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    // Timings
    fun openTimingCustomizer() {
        _isTimingCustomizerVisible.value = true
    }

    fun closeTimingCustomizer() {
        _isTimingCustomizerVisible.value = false
    }

    fun updateMealTiming(mealType: MealType, start: String, end: String) {
        preferences.setMealTiming(mealType, start, end)
    }

    // Rewarded Ad Simulation
    fun triggerRewardedAd() {
        _isRewardedAdActive.value = true
        _adCountdown.value = 5
        viewModelScope.launch {
            for (i in 5 downTo 1) {
                _adCountdown.value = i
                delay(1000)
            }
            _isRewardedAdActive.value = false
            triggerConfetti()
        }
    }

    fun dismissRewardedAd() {
        _isRewardedAdActive.value = false
    }

    // Admin Authentication
    fun authenticateAdmin(passcode: String): Boolean {
        if (passcode == preferences.adminPasscode || passcode == "grub2026") {
            _isAdminAuthenticated.value = true
            return true
        }
        return false
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
        _currentScreen.value = AppScreen.MAIN_APP
    }

    fun setAdminTab(index: Int) {
        _adminActiveTabIndex.value = index
    }

    // API Processing of Excel / Spreadsheet into Veg, Non-Veg, Special Mess
    fun processSpreadsheetViaApi(csvContent: String) {
        val generated = ExcelMenuParser.processSpreadsheetToMessPlans(csvContent)
        _generatedMenus.value = generated
    }

    fun updateGeneratedMenuDishes(menuId: String, updatedDishes: String) {
        _generatedMenus.value = _generatedMenus.value.map {
            if (it.id == menuId) it.copy(dishesSummary = updatedDishes) else it
        }
    }

    fun publishGeneratedMenus() {
        val count = _generatedMenus.value.size
        if (count > 0) {
            // Apply each generated menu to the repository
            for (m in _generatedMenus.value) {
                val dishes = m.dishesSummary.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                for (dish in dishes) {
                    val cat = ExcelMenuParser.categorizeDish(dish)
                    repository.addMenuItemManual(
                        date = m.date,
                        mealType = m.mealType,
                        name = dish,
                        category = cat,
                        isFeast = m.isFeast
                    )
                }
                if (m.isFeast && m.specialNote.isNotBlank()) {
                    repository.updateFeastNote(m.date, m.specialNote)
                }
            }
            showAdminToast("Published $count Veg, Non-Veg & Special mess menus to students!")
        }
    }

    fun showAdminToast(msg: String) {
        _adminToast.value = msg
        viewModelScope.launch {
            delay(3000)
            _adminToast.value = null
        }
    }
}
