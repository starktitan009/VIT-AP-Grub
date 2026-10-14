package com.example.data.model

enum class AppThemeMode(val title: String) {
    SLATE_DARK("Slate Dark (Neon Accent)"),
    AMOLED("AMOLED (Pure Black)"),
    LIGHT("Light Mode (Clean Slate)")
}

enum class HostelType(val title: String, val shortName: String) {
    MH("Men's Hostel", "MH"),
    LH("Ladies Hostel", "LH")
}

data class HostelBlock(
    val id: String,
    val type: HostelType,
    val name: String,
    val floorCount: Int = 10,
    val diningHall: String = "Ground Floor Central"
)

enum class MessPlan(val title: String, val emoji: String, val description: String) {
    VEG("Veg Mess", "🥦", "Pure vegetarian meal rotation with paneer, dal makhani, curries & curd."),
    NON_VEG("Non-Veg Mess", "🍗", "Standard menu plus chicken curry, egg masala, fish fry & biryani feasts."),
    SPECIAL("Special Mess", "✨", "Premium executive buffet with live counters, ice creams, pasta & delicacies.")
}

enum class MealType(val title: String, val defaultStart: String, val defaultEnd: String) {
    BREAKFAST("Breakfast", "07:30", "09:30"),
    LUNCH("Lunch", "12:00", "14:15"),
    SNACKS("Snacks", "16:30", "18:00"),
    DINNER("Dinner", "19:30", "21:30")
}

enum class DishCategory(val label: String) {
    COMMON("Common"),
    VEG("Veg"),
    NON_VEG("Non-Veg"),
    SPECIAL("Special")
}

data class MenuItem(
    val id: String,
    val name: String,
    val category: DishCategory,
    val isSpecialFeast: Boolean = false,
    val calories: String = "",
    val allergens: String = ""
)

data class MealMenu(
    val mealType: MealType,
    val items: List<MenuItem>,
    val feastAnnouncement: String? = null
)

data class DailyMessSchedule(
    val date: String, // Format: YYYY-MM-DD
    val dayName: String,
    val meals: Map<MealType, MealMenu>,
    val specialFeastNote: String? = null
)

data class MealTiming(
    val mealType: MealType,
    val startTime: String,
    val endTime: String
)

enum class LocationZone(val label: String) {
    CAMPUS_FOOD_STREET("Food Street"),
    ROCK_PLAZA("Rock Plaza"),
    NEAR_CAMPUS("Near Campus"),
    VIJAYAWADA("Vijayawada (~25km)")
}

data class Eatery(
    val id: String,
    val name: String,
    val locationZone: LocationZone,
    val cuisines: List<String>,
    val hours: String,
    val phone: String,
    val mapsUrl: String,
    val rating: Float,
    val priceRange: String,
    val menuHighlights: List<String>,
    val isEnabled: Boolean = true
)

data class CampusDeliveryService(
    val id: String,
    val name: String,
    val hotline: String,
    val whatsappNumber: String,
    val dropPoints: List<String>,
    val hours: String,
    val minOrder: String,
    val deliveryFee: String,
    val isOnline: Boolean = true
)

data class FoodReview(
    val id: String,
    val authorAlias: String,
    val hostelBlock: String,
    val mealType: MealType,
    val date: String,
    val rating: Int,
    val dishName: String,
    val comment: String,
    val likesCount: Int = 0,
    val timeAgo: String = "Just now",
    val isLikedByUser: Boolean = false
)

data class BroadcastAlert(
    val id: String,
    val title: String,
    val message: String,
    val urgency: String = "INFO", // INFO, SPECIAL_DINNER, TIMING_CHANGE, EMERGENCY
    val timestamp: Long = System.currentTimeMillis()
)

data class DisciplinaryHold(
    val isActive: Boolean = false,
    val targetAlias: String = "ALL", // "ALL" or specific alias
    val reason: String = "Notice: Violation of Mess Regulations or Code of Conduct.",
    val issuedBy: String = "Office of Chief Warden & Mess Committee, VIT-AP",
    val dateIssued: String = "October 14, 2026"
)

data class AdminConfig(
    val supabaseUrl: String = "https://vit-ap-grub-cloud.supabase.co",
    val supabaseAnonKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    val adsensePublisherId: String = "ca-pub-8492038472910293",
    val isAdBannerEnabled: Boolean = true,
    val isRewardedAdLockEnabled: Boolean = false,
    val publishedVersion: String = "v1.0.4-release",
    val gitHubRepo: String = "github.com/vit-ap-students/vit-ap-grub-android"
)
