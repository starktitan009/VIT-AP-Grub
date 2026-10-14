package com.example.data.repository

import com.example.data.local.GrubPreferences
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class GrubRepository(private val preferences: GrubPreferences) {

    // Broadcast alerts
    private val _alerts = MutableStateFlow<List<BroadcastAlert>>(
        listOf(
            BroadcastAlert(
                id = "alert-1",
                title = "🍗 Grand Biryani & Gulab Jamun Feast Tonight!",
                message = "Dinner in all MH & LH dining halls features authentic Hyderabadi Dum Biryani, Mirchi Ka Salan, Paneer Makhani & Hot Gulab Jamun. Serving 19:30 - 21:30.",
                urgency = "SPECIAL_DINNER",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30
            ),
            BroadcastAlert(
                id = "alert-2",
                title = "⏰ Extended Breakfast Timings for Exams",
                message = "Morning breakfast window extended until 09:45 AM during Mid-Term week across all blocks.",
                urgency = "TIMING_CHANGE",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120
            )
        )
    )
    val alerts: StateFlow<List<BroadcastAlert>> = _alerts.asStateFlow()

    // October 2026 Mess Schedules (In-memory map populated with real delicious daily menus)
    private val schedules = mutableMapOf<String, DailyMessSchedule>()

    // Eateries
    private val _eateries = MutableStateFlow<List<Eatery>>(emptyList())
    val eateries: StateFlow<List<Eatery>> = _eateries.asStateFlow()

    // Campus Delivery Services
    private val _deliveryServices = MutableStateFlow<List<CampusDeliveryService>>(emptyList())
    val deliveryServices: StateFlow<List<CampusDeliveryService>> = _deliveryServices.asStateFlow()

    // Reviews
    private val _reviews = MutableStateFlow<List<FoodReview>>(emptyList())
    val reviews: StateFlow<List<FoodReview>> = _reviews.asStateFlow()

    // Disciplinary Hold
    private val _disciplinaryHold = MutableStateFlow(preferences.getDisciplinaryHold())
    val disciplinaryHold: StateFlow<DisciplinaryHold> = _disciplinaryHold.asStateFlow()

    // Active live users counter
    val onlineUsersCount = 247

    init {
        initMessSchedules()
        initEateries()
        initDeliveryServices()
        initReviews()
    }

    private fun initMessSchedules() {
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        // Pre-populate October 2026 (31 days)
        for (day in 1..31) {
            val dateStr = String.format(Locale.US, "2026-10-%02d", day)
            val dayName = days[(day + 2) % 7] // Approximate day name

            val isFeast = (day % 3 == 0) || day == 14 || day == 18 || day == 25 || day == 31
            val feastTitle = if (isFeast) {
                if (day % 2 == 0) "✨ Royal Hyderabadi Dum Biryani & Gulab Jamun Feast"
                else "🍗 Butter Chicken & Shahi Paneer Feast with Malabar Parotta"
            } else null

            val breakfastItems = listOf(
                MenuItem("b1_$day", if (day % 2 == 0) "Mysore Masala Dosa & Sambar" else "Idli & Medu Vada", DishCategory.VEG, calories = "220 kcal"),
                MenuItem("b2_$day", "Coconut & Tomato Chutney", DishCategory.COMMON, calories = "80 kcal"),
                MenuItem("b3_$day", if (day % 3 == 0) "Boiled Egg (2 pcs)" else "Aloo Paratha with Curd", if (day % 3 == 0) DishCategory.NON_VEG else DishCategory.VEG, calories = "160 kcal"),
                MenuItem("b4_$day", "Fresh Banana / Seasonal Fruit", DishCategory.COMMON, calories = "90 kcal"),
                MenuItem("b5_$day", "Filter Coffee, Masala Tea & Hot Milk", DishCategory.COMMON, calories = "110 kcal"),
                MenuItem("b6_$day", "Bread, Butter & Mixed Fruit Jam", DishCategory.COMMON, calories = "140 kcal")
            )

            val lunchItems = listOf(
                MenuItem("l1_$day", "Steamed Sona Masoori Rice", DishCategory.COMMON, calories = "200 kcal"),
                MenuItem("l2_$day", "Soft Phulkas / Rotis with Ghee", DishCategory.COMMON, calories = "150 kcal"),
                MenuItem("l3_$day", if (day % 2 == 0) "Andhra Tomato Dal" else "Dal Makhani", DishCategory.VEG, calories = "130 kcal"),
                MenuItem("l4_$day", if (day % 2 == 0) "Aloo Gobi Roast" else "Paneer Butter Masala", if (day % 2 == 0) DishCategory.VEG else DishCategory.SPECIAL, isSpecialFeast = day % 2 != 0, calories = "240 kcal"),
                MenuItem("l5_$day", if (day % 4 == 0) "Andhra Chicken Curry" else if (day % 4 == 2) "Chettinad Egg Masala" else "Bhindi Do Pyaza", if (day % 2 == 0) DishCategory.NON_VEG else DishCategory.VEG, calories = "280 kcal"),
                MenuItem("l6_$day", "Traditional Pepper Rasam & Papad", DishCategory.COMMON, calories = "50 kcal"),
                MenuItem("l7_$day", "Thick Fresh Curd / Buttermilk", DishCategory.COMMON, calories = "90 kcal")
            )

            val snacksItems = listOf(
                MenuItem("s1_$day", if (day % 3 == 0) "Crispy Onion Pakoda" else if (day % 3 == 1) "Veg Samosa with Chutney" else "Pav Bhaji", DishCategory.VEG, calories = "180 kcal"),
                MenuItem("s2_$day", "Hot Masala Chai & Green Tea", DishCategory.COMMON, calories = "90 kcal"),
                MenuItem("s3_$day", "Bournvita Milk / Horlicks", DishCategory.COMMON, calories = "120 kcal")
            )

            val dinnerItems = if (isFeast) {
                listOf(
                    MenuItem("d1_$day", "Hyderabadi Chicken Dum Biryani", DishCategory.NON_VEG, isSpecialFeast = true, calories = "450 kcal"),
                    MenuItem("d2_$day", "Shahi Paneer Dum Biryani", DishCategory.SPECIAL, isSpecialFeast = true, calories = "410 kcal"),
                    MenuItem("d3_$day", "Mirchi Ka Salan & Cucumber Raita", DishCategory.COMMON, calories = "110 kcal"),
                    MenuItem("d4_$day", "Malabar Parotta & Dal Tadka", DishCategory.VEG, calories = "260 kcal"),
                    MenuItem("d5_$day", "Warm Gulab Jamun & Vanilla Ice Cream", DishCategory.SPECIAL, isSpecialFeast = true, calories = "290 kcal"),
                    MenuItem("d6_$day", "Steamed Rice, Rasam & Curd", DishCategory.COMMON, calories = "180 kcal")
                )
            } else {
                listOf(
                    MenuItem("d1_$day", "Phulkas with Butter", DishCategory.COMMON, calories = "140 kcal"),
                    MenuItem("d2_$day", "Jeera Rice & Dal Fry", DishCategory.VEG, calories = "210 kcal"),
                    MenuItem("d3_$day", if (day % 2 == 0) "Egg Curry (2 Eggs)" else "Mixed Veg Korma", if (day % 2 == 0) DishCategory.NON_VEG else DishCategory.VEG, calories = "230 kcal"),
                    MenuItem("d4_$day", "Gobi Manchurian Dry", DishCategory.SPECIAL, isSpecialFeast = false, calories = "220 kcal"),
                    MenuItem("d5_$day", "Kheer / Fruit Custard", DishCategory.SPECIAL, calories = "160 kcal"),
                    MenuItem("d6_$day", "Steamed Rice, Sambar & Papad", DishCategory.COMMON, calories = "190 kcal")
                )
            }

            val mealMap = mapOf(
                MealType.BREAKFAST to MealMenu(MealType.BREAKFAST, breakfastItems),
                MealType.LUNCH to MealMenu(MealType.LUNCH, lunchItems),
                MealType.SNACKS to MealMenu(MealType.SNACKS, snacksItems),
                MealType.DINNER to MealMenu(MealType.DINNER, dinnerItems, feastAnnouncement = feastTitle)
            )

            schedules[dateStr] = DailyMessSchedule(
                date = dateStr,
                dayName = dayName,
                meals = mealMap,
                specialFeastNote = feastTitle
            )
        }
    }

    private fun initEateries() {
        _eateries.value = listOf(
            // Campus Food Street
            Eatery(
                id = "fs-1",
                name = "Rolls Mania & Frankies",
                locationZone = LocationZone.CAMPUS_FOOD_STREET,
                cuisines = listOf("Kathi Rolls", "Wraps", "Momos"),
                hours = "11:00 AM - 11:30 PM",
                phone = "+91 91234 56781",
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=VIT+AP+University+Food+Street",
                rating = 4.6f,
                priceRange = "₹80 - ₹160",
                menuHighlights = listOf("Double Egg Paneer Roll", "Chicken Tikka Frankie", "Peri Peri Fries")
            ),
            Eatery(
                id = "fs-2",
                name = "Juice Factory & Shakes",
                locationZone = LocationZone.CAMPUS_FOOD_STREET,
                cuisines = listOf("Fresh Juices", "Thickshakes", "Smoothies"),
                hours = "09:00 AM - 11:45 PM",
                phone = "+91 91234 56782",
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=VIT+AP+University+Food+Street",
                rating = 4.7f,
                priceRange = "₹50 - ₹120",
                menuHighlights = listOf("Nutella Hazelnut Shake", "Avocado Smoothie", "Mosambi Juice")
            ),
            Eatery(
                id = "fs-3",
                name = "Southern Spice Tiffins",
                locationZone = LocationZone.CAMPUS_FOOD_STREET,
                cuisines = listOf("South Indian", "Tiffins", "Filter Coffee"),
                hours = "07:00 AM - 10:30 PM",
                phone = "+91 91234 56783",
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=VIT+AP+University+Food+Street",
                rating = 4.4f,
                priceRange = "₹40 - ₹100",
                menuHighlights = listOf("Ghee Podi Masala Dosa", "Punugulu", "Idli Vada Combo")
            ),
            // Rock Plaza
            Eatery(
                id = "rp-1",
                name = "Rock Food Court & Pizzas",
                locationZone = LocationZone.ROCK_PLAZA,
                cuisines = listOf("Woodfired Pizza", "Pasta", "Burgers"),
                hours = "12:00 PM - 11:00 PM",
                phone = "+91 91234 56784",
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=VIT+AP+University+Rock+Plaza",
                rating = 4.5f,
                priceRange = "₹120 - ₹280",
                menuHighlights = listOf("Farmhouse Veg Pizza", "BBQ Chicken Burger", "White Sauce Penne")
            ),
            Eatery(
                id = "rp-2",
                name = "Waffle & Scoop Corner",
                locationZone = LocationZone.ROCK_PLAZA,
                cuisines = listOf("Belgian Waffles", "Ice Creams", "Sundae"),
                hours = "01:00 PM - Midnight",
                phone = "+91 91234 56785",
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=VIT+AP+University+Rock+Plaza",
                rating = 4.8f,
                priceRange = "₹90 - ₹200",
                menuHighlights = listOf("Triple Chocolate Waffle", "Belgian Dark Chocolate Scoop", "Red Velvet Sundae")
            ),
            // Near Campus
            Eatery(
                id = "nc-1",
                name = "Amaravati Spicy Kitchen",
                locationZone = LocationZone.NEAR_CAMPUS,
                cuisines = listOf("Andhra Meals", "Biryani", "Spicy Starters"),
                hours = "11:30 AM - 11:00 PM",
                phone = "+91 86321 09876",
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=Amaravati+Spicy+Kitchen+Inavolu+VIT+AP",
                rating = 4.5f,
                priceRange = "₹150 - ₹350",
                menuHighlights = listOf("Potlam Biryani", "Guntur Chicken Fry", "Paneer 65")
            ),
            Eatery(
                id = "nc-2",
                name = "Reddy's Biryani House",
                locationZone = LocationZone.NEAR_CAMPUS,
                cuisines = listOf("Dum Biryani", "Mughlai", "Tandoori"),
                hours = "12:00 PM - Midnight",
                phone = "+91 86321 45678",
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=Reddy+Biryani+Thullur+Amaravati",
                rating = 4.6f,
                priceRange = "₹180 - ₹380",
                menuHighlights = listOf("Special Chicken Dum Biryani", "Tandoori Roti with Kaju Curry")
            ),
            // Vijayawada
            Eatery(
                id = "vj-1",
                name = "Sweet Magic Restaurant & Sweets",
                locationZone = LocationZone.VIJAYAWADA,
                cuisines = listOf("South Indian Thali", "Sweets", "Snacks"),
                hours = "07:30 AM - 10:30 PM",
                phone = "+91 866 247 1234",
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=Sweet+Magic+MG+Road+Vijayawada",
                rating = 4.7f,
                priceRange = "₹120 - ₹300",
                menuHighlights = listOf("Royal Andhra Thali", "Kaju Katli", "Badam Milk")
            ),
            Eatery(
                id = "vj-2",
                name = "Crossroads Restaurant",
                locationZone = LocationZone.VIJAYAWADA,
                cuisines = listOf("Multi-Cuisine", "Chinese", "North Indian"),
                hours = "12:00 PM - 11:00 PM",
                phone = "+91 866 257 5678",
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=Crossroads+Restaurant+Siddhartha+Nagar+Vijayawada",
                rating = 4.4f,
                priceRange = "₹250 - ₹600",
                menuHighlights = listOf("Crispy Corn", "Crossroads Special Biryani", "Sizzling Brownie")
            )
        )
    }

    private fun initDeliveryServices() {
        _deliveryServices.value = listOf(
            CampusDeliveryService(
                id = "del-1",
                name = "Amaravati Night Bites",
                hotline = "+91 98480 12345",
                whatsappNumber = "+919848012345",
                dropPoints = listOf("MH 1-7 Ground Lobby", "LH 1-4 Main Security Gate", "Academic Block 1"),
                hours = "08:00 PM - 03:00 AM",
                minOrder = "₹150",
                deliveryFee = "₹20 (Free over ₹300)"
            ),
            CampusDeliveryService(
                id = "del-2",
                name = "Royal Kitchen Campus Express",
                hotline = "+91 99887 65432",
                whatsappNumber = "+919988765432",
                dropPoints = listOf("Direct MH Block Entrance", "LH Gate 2 Drop Point"),
                hours = "07:00 PM - 02:30 AM",
                minOrder = "₹120",
                deliveryFee = "FREE"
            ),
            CampusDeliveryService(
                id = "del-3",
                name = "VIT Midnight Munchies Hub",
                hotline = "+91 97000 88990",
                whatsappNumber = "+919700088990",
                dropPoints = listOf("All MH & LH Drop Points", "Central Library Lawn"),
                hours = "10:00 PM - 04:00 AM",
                minOrder = "₹100",
                deliveryFee = "₹15"
            )
        )
    }

    private fun initReviews() {
        _reviews.value = listOf(
            FoodReview(
                id = "rev-screen-1",
                authorAlias = "GrubVITian_42",
                hostelBlock = "MH 6",
                mealType = MealType.DINNER,
                date = "2026-10-14",
                rating = 5,
                dishName = "Chicken Dum Biryani",
                comment = "hi",
                likesCount = 0,
                timeAgo = "Just now"
            ),
            FoodReview(
                id = "rev-screen-2",
                authorAlias = "GrubVITian_42",
                hostelBlock = "MH-2",
                mealType = MealType.LUNCH,
                date = "2026-10-14",
                rating = 5,
                dishName = "Paneer Butter Masala",
                comment = "Super fresh paneer and hot rotis today! Best lunch in MH dining hall this week.",
                likesCount = 3,
                timeAgo = "Just now"
            ),
            FoodReview(
                id = "rev-1",
                authorAlias = "BiryaniFiend",
                hostelBlock = "MH 2",
                mealType = MealType.DINNER,
                date = "2026-10-14",
                rating = 5,
                dishName = "Hyderabadi Chicken Biryani",
                comment = "Juicy chicken pieces, aromatic basmati rice, and hot gulab jamun! Pure perfection.",
                likesCount = 42,
                timeAgo = "18 mins ago"
            ),
            FoodReview(
                id = "rev-2",
                authorAlias = "CrispyDosa",
                hostelBlock = "LH 1",
                mealType = MealType.BREAKFAST,
                date = "2026-10-14",
                rating = 4,
                dishName = "Mysore Masala Dosa",
                comment = "Crispy dosa at 8:15 AM! Coconut chutney needs a pinch more salt but sambar was great.",
                likesCount = 19,
                timeAgo = "1 hour ago"
            )
        )
    }

    fun getDailySchedule(date: String): DailyMessSchedule {
        return schedules[date] ?: schedules["2026-10-14"] ?: DailyMessSchedule(
            date = date,
            dayName = "Today",
            meals = emptyMap()
        )
    }

    fun getSpecialFeastDates(): Set<String> {
        return schedules.filter { it.value.specialFeastNote != null }.keys
    }

    fun searchDishes(query: String): List<Pair<String, MenuItem>> {
        val q = query.lowercase().trim()
        if (q.isEmpty()) return emptyList()

        val results = mutableListOf<Pair<String, MenuItem>>()
        for ((date, sched) in schedules) {
            for ((mealType, meal) in sched.meals) {
                for (item in meal.items) {
                    if (item.name.lowercase().contains(q) || item.category.name.lowercase().contains(q)) {
                        results.add("${date} (${mealType.title})" to item)
                    }
                }
            }
        }
        return results
    }

    fun getSortedMealTypesByTime(currentTimeHours: Int, currentTimeMins: Int): List<MealType> {
        val timeVal = currentTimeHours * 60 + currentTimeMins
        val breakfastEnd = 9 * 60 + 30
        val lunchEnd = 14 * 60 + 15
        val snacksEnd = 18 * 60
        val dinnerEnd = 21 * 60 + 30

        return when {
            timeVal <= breakfastEnd -> listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.SNACKS, MealType.DINNER)
            timeVal <= lunchEnd -> listOf(MealType.LUNCH, MealType.SNACKS, MealType.DINNER, MealType.BREAKFAST)
            timeVal <= snacksEnd -> listOf(MealType.SNACKS, MealType.DINNER, MealType.BREAKFAST, MealType.LUNCH)
            timeVal <= dinnerEnd -> listOf(MealType.DINNER, MealType.BREAKFAST, MealType.LUNCH, MealType.SNACKS)
            else -> listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.SNACKS, MealType.DINNER)
        }
    }

    fun addReview(authorAlias: String, hostelBlock: String, mealType: MealType, rating: Int, dishName: String, comment: String) {
        val newReview = FoodReview(
            id = UUID.randomUUID().toString(),
            authorAlias = authorAlias.ifBlank { "Anonymous Grubbie" },
            hostelBlock = hostelBlock,
            mealType = mealType,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            rating = rating,
            dishName = dishName,
            comment = comment,
            likesCount = 0,
            timeAgo = "Just now",
            isLikedByUser = false
        )
        _reviews.value = listOf(newReview) + _reviews.value
    }

    fun toggleLikeReview(reviewId: String) {
        _reviews.value = _reviews.value.map { review ->
            if (review.id == reviewId) {
                val newLiked = !review.isLikedByUser
                val newCount = if (newLiked) review.likesCount + 1 else (review.likesCount - 1).coerceAtLeast(0)
                review.copy(isLikedByUser = newLiked, likesCount = newCount)
            } else {
                review
            }
        }
    }

    // Admin Operations
    fun commitGeneratedMessPlanMenus(menus: List<ExcelMenuParser.GeneratedMessPlanMenu>) {
        for (m in menus) {
            val sched = schedules[m.date] ?: DailyMessSchedule(
                date = m.date,
                dayName = "Updated",
                meals = emptyMap()
            )
            val existingMeals = sched.meals.toMutableMap()
            val existingMealMenu = existingMeals[m.mealType] ?: MealMenu(m.mealType, emptyList())
            val dishes = m.dishesSummary.split(",", ";").map { it.trim() }.filter { it.isNotEmpty() }
            val newItems = dishes.map { dish ->
                MenuItem(
                    id = UUID.randomUUID().toString(),
                    name = dish,
                    category = ExcelMenuParser.categorizeDish(dish),
                    isSpecialFeast = m.isFeast,
                    calories = "220 kcal"
                )
            }
            existingMeals[m.mealType] = existingMealMenu.copy(items = existingMealMenu.items + newItems)
            val note = if (m.isFeast && m.specialNote.isNotBlank()) m.specialNote else sched.specialFeastNote
            schedules[m.date] = sched.copy(meals = existingMeals, specialFeastNote = note)
        }
    }

    fun addMenuItemManual(date: String, mealType: MealType, name: String, category: DishCategory, isFeast: Boolean) {
        val sched = schedules[date] ?: DailyMessSchedule(date = date, dayName = "Updated", meals = emptyMap())
        val existingMeals = sched.meals.toMutableMap()
        val existingMealMenu = existingMeals[mealType] ?: MealMenu(mealType, emptyList())
        val updated = existingMealMenu.items + MenuItem(
            id = UUID.randomUUID().toString(),
            name = name,
            category = category,
            isSpecialFeast = isFeast,
            calories = "200 kcal"
        )
        existingMeals[mealType] = existingMealMenu.copy(items = updated)
        schedules[date] = sched.copy(meals = existingMeals)
    }

    fun updateFeastNote(date: String, note: String) {
        val sched = schedules[date] ?: return
        schedules[date] = sched.copy(specialFeastNote = note.ifBlank { null })
    }

    fun addEatery(eatery: Eatery) {
        _eateries.value = listOf(eatery) + _eateries.value
    }

    fun toggleEatery(id: String) {
        _eateries.value = _eateries.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun deleteEatery(id: String) {
        _eateries.value = _eateries.value.filter { it.id != id }
    }

    fun addDeliveryService(service: CampusDeliveryService) {
        _deliveryServices.value = listOf(service) + _deliveryServices.value
    }

    fun postAlert(alert: BroadcastAlert) {
        _alerts.value = listOf(alert) + _alerts.value
    }

    fun updateDisciplinaryHold(hold: DisciplinaryHold) {
        preferences.setDisciplinaryHold(hold)
        _disciplinaryHold.value = hold
    }
}
