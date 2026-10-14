package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppThemeMode
import com.example.data.model.DisciplinaryHold
import com.example.data.model.MealTiming
import com.example.data.model.MealType
import com.example.data.model.MessPlan

class GrubPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("vit_ap_grub_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_COMPLETED_ONBOARDING = "completed_onboarding"
        private const val KEY_SELECTED_HOSTEL = "selected_hostel"
        private const val KEY_SELECTED_MESS_PLAN = "selected_mess_plan"
        private const val KEY_NOTIF_UPCOMING_MEAL = "notif_upcoming_meal"
        private const val KEY_NOTIF_SPECIAL_FEASTS = "notif_special_feasts"
        private const val KEY_CHAT_USERNAME = "chat_username"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DISCIPLINARY_ACTIVE = "disciplinary_active"
        private const val KEY_DISCIPLINARY_TARGET = "disciplinary_target"
        private const val KEY_DISCIPLINARY_REASON = "disciplinary_reason"
        private const val KEY_ADMIN_PASSCODE = "admin_passcode"
        private const val KEY_ADS_BANNER_ENABLED = "ads_banner_enabled"
        private const val KEY_REWARDED_AD_LOCK = "rewarded_ad_lock"
        private const val KEY_SUPABASE_URL = "supabase_url"
        private const val KEY_SUPABASE_ANON = "supabase_anon"
    }

    var completedOnboarding: Boolean
        get() = prefs.getBoolean(KEY_COMPLETED_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_COMPLETED_ONBOARDING, value).apply()

    var selectedHostel: String
        get() = prefs.getString(KEY_SELECTED_HOSTEL, "MH 2") ?: "MH 2"
        set(value) = prefs.edit().putString(KEY_SELECTED_HOSTEL, value).apply()

    var selectedMessPlan: MessPlan
        get() {
            val name = prefs.getString(KEY_SELECTED_MESS_PLAN, MessPlan.NON_VEG.name)
            return try {
                MessPlan.valueOf(name ?: MessPlan.NON_VEG.name)
            } catch (e: Exception) {
                MessPlan.NON_VEG
            }
        }
        set(value) = prefs.edit().putString(KEY_SELECTED_MESS_PLAN, value.name).apply()

    var notificationsUpcomingMeal: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_UPCOMING_MEAL, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_UPCOMING_MEAL, value).apply()

    var notificationsSpecialFeasts: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_SPECIAL_FEASTS, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_SPECIAL_FEASTS, value).apply()

    var chatUsername: String
        get() = prefs.getString(KEY_CHAT_USERNAME, "GrubExplorer") ?: "GrubExplorer"
        set(value) = prefs.edit().putString(KEY_CHAT_USERNAME, value).apply()

    var themeMode: AppThemeMode
        get() {
            val name = prefs.getString(KEY_THEME_MODE, AppThemeMode.SLATE_DARK.name)
            return try {
                AppThemeMode.valueOf(name ?: AppThemeMode.SLATE_DARK.name)
            } catch (e: Exception) {
                AppThemeMode.SLATE_DARK
            }
        }
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value.name).apply()

    fun getMealTiming(mealType: MealType): MealTiming {
        val startKey = "timing_${mealType.name}_start"
        val endKey = "timing_${mealType.name}_end"
        val start = prefs.getString(startKey, mealType.defaultStart) ?: mealType.defaultStart
        val end = prefs.getString(endKey, mealType.defaultEnd) ?: mealType.defaultEnd
        return MealTiming(mealType, start, end)
    }

    fun setMealTiming(mealType: MealType, startTime: String, endTime: String) {
        prefs.edit()
            .putString("timing_${mealType.name}_start", startTime)
            .putString("timing_${mealType.name}_end", endTime)
            .apply()
    }

    fun getDisciplinaryHold(): DisciplinaryHold {
        val active = prefs.getBoolean(KEY_DISCIPLINARY_ACTIVE, false)
        val target = prefs.getString(KEY_DISCIPLINARY_TARGET, "ALL") ?: "ALL"
        val reason = prefs.getString(KEY_DISCIPLINARY_REASON, "Formal Notice from Warden Committee") ?: "Notice"
        return DisciplinaryHold(isActive = active, targetAlias = target, reason = reason)
    }

    fun setDisciplinaryHold(hold: DisciplinaryHold) {
        prefs.edit()
            .putBoolean(KEY_DISCIPLINARY_ACTIVE, hold.isActive)
            .putString(KEY_DISCIPLINARY_TARGET, hold.targetAlias)
            .putString(KEY_DISCIPLINARY_REASON, hold.reason)
            .apply()
    }

    var adminPasscode: String
        get() = prefs.getString(KEY_ADMIN_PASSCODE, "grub2026") ?: "grub2026"
        set(value) = prefs.edit().putString(KEY_ADMIN_PASSCODE, value).apply()

    var isAdBannerEnabled: Boolean
        get() = prefs.getBoolean(KEY_ADS_BANNER_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ADS_BANNER_ENABLED, value).apply()

    var isRewardedAdLockEnabled: Boolean
        get() = prefs.getBoolean(KEY_REWARDED_AD_LOCK, false)
        set(value) = prefs.edit().putBoolean(KEY_REWARDED_AD_LOCK, value).apply()

    var supabaseUrl: String
        get() = prefs.getString(KEY_SUPABASE_URL, "https://vit-ap-grub-cloud.supabase.co") ?: "https://vit-ap-grub-cloud.supabase.co"
        set(value) = prefs.edit().putString(KEY_SUPABASE_URL, value).apply()

    var supabaseAnonKey: String
        get() = prefs.getString(KEY_SUPABASE_ANON, "sb_publishable_anon_key_demo_vitap_2026") ?: "sb_publishable_anon_key_demo_vitap_2026"
        set(value) = prefs.edit().putString(KEY_SUPABASE_ANON, value).apply()

    fun resetAppData() {
        prefs.edit().clear().apply()
    }
}
