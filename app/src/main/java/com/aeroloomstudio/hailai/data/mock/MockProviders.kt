package com.aeroloomstudio.hailai.data.mock

import com.aeroloomstudio.hailai.data.model.Provider

object MockProviders {

    val allProviders: List<Provider> = listOf(
        // ─── HVAC / AC Technicians ───
        Provider(
            id = "prov_ac_001", name = "Ali AC Services", category = "hvac",
            subcategory = "ac_technician", phone = "+92-311-0000001",
            area = "G-13", city = "Islamabad", lat = 33.6844, lng = 73.0479,
            rating = 4.7, totalReviews = 89, priceMin = 800, priceMax = 1200,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","11:00","14:00","15:00","16:00"),
            verified = true, experienceYears = 6, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_ac_002", name = "Cool Breeze AC Repair", category = "hvac",
            subcategory = "ac_technician", phone = "+92-312-0000002",
            area = "F-10", city = "Islamabad", lat = 33.6980, lng = 73.0100,
            rating = 4.5, totalReviews = 65, priceMin = 1000, priceMax = 1500,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri"),
            availableSlots = listOf("10:00","11:00","14:00","15:00"),
            verified = true, experienceYears = 8, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_ac_003", name = "Faisal Cooling Solutions", category = "hvac",
            subcategory = "ac_technician", phone = "+92-313-0000003",
            area = "Gulberg", city = "Lahore", lat = 31.5204, lng = 74.3587,
            rating = 4.3, totalReviews = 112, priceMin = 700, priceMax = 1100,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","11:00","13:00","14:00"),
            verified = true, experienceYears = 10, languages = listOf("urdu","punjabi")
        ),
        Provider(
            id = "prov_ac_004", name = "Karachi Cool Tech", category = "hvac",
            subcategory = "ac_technician", phone = "+92-314-0000004",
            area = "DHA Phase 6", city = "Karachi", lat = 24.8075, lng = 67.0550,
            rating = 4.6, totalReviews = 73, priceMin = 900, priceMax = 1400,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun"),
            availableSlots = listOf("08:00","09:00","10:00","14:00","15:00","16:00","17:00"),
            verified = true, experienceYears = 5, languages = listOf("urdu","english","sindhi")
        ),

        // ─── Plumbing ───
        Provider(
            id = "prov_plumb_001", name = "Ahmed Plumbing Works", category = "plumbing",
            subcategory = "plumber", phone = "+92-315-0000005",
            area = "I-8", city = "Islamabad", lat = 33.6690, lng = 73.0510,
            rating = 4.4, totalReviews = 56, priceMin = 500, priceMax = 1000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","11:00","14:00","15:00"),
            verified = true, experienceYears = 12, languages = listOf("urdu")
        ),
        Provider(
            id = "prov_plumb_002", name = "Master Pipes Solutions", category = "plumbing",
            subcategory = "plumber", phone = "+92-316-0000006",
            area = "G-9", city = "Islamabad", lat = 33.6910, lng = 73.0350,
            rating = 4.8, totalReviews = 134, priceMin = 600, priceMax = 1200,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri"),
            availableSlots = listOf("08:00","09:00","10:00","11:00","14:00","15:00","16:00"),
            verified = true, experienceYears = 15, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_plumb_003", name = "Lahore Pro Plumbers", category = "plumbing",
            subcategory = "plumber", phone = "+92-317-0000007",
            area = "DHA Phase 5", city = "Lahore", lat = 31.4700, lng = 74.3800,
            rating = 4.2, totalReviews = 45, priceMin = 400, priceMax = 900,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","14:00","15:00","16:00"),
            verified = true, experienceYears = 7, languages = listOf("urdu","punjabi")
        ),
        Provider(
            id = "prov_plumb_004", name = "Rashid Water Works", category = "plumbing",
            subcategory = "plumber", phone = "+92-318-0000008",
            area = "Clifton", city = "Karachi", lat = 24.8138, lng = 67.0300,
            rating = 4.1, totalReviews = 67, priceMin = 500, priceMax = 1100,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun"),
            availableSlots = listOf("09:00","10:00","11:00","13:00","14:00","15:00"),
            verified = false, experienceYears = 9, languages = listOf("urdu","sindhi")
        ),

        // ─── Electrical ───
        Provider(
            id = "prov_elec_001", name = "Waqas Electric", category = "electrical",
            subcategory = "electrician", phone = "+92-319-0000009",
            area = "F-7", city = "Islamabad", lat = 33.7170, lng = 73.0560,
            rating = 4.9, totalReviews = 201, priceMin = 600, priceMax = 1500,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","11:00","14:00","15:00","16:00"),
            verified = true, experienceYears = 14, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_elec_002", name = "Bright Spark Electricians", category = "electrical",
            subcategory = "electrician", phone = "+92-320-0000010",
            area = "Blue Area", city = "Islamabad", lat = 33.7100, lng = 73.0600,
            rating = 4.6, totalReviews = 88, priceMin = 800, priceMax = 2000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri"),
            availableSlots = listOf("10:00","11:00","14:00","15:00"),
            verified = true, experienceYears = 11, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_elec_003", name = "Hamza Electric Works", category = "electrical",
            subcategory = "electrician", phone = "+92-321-0000011",
            area = "Model Town", city = "Lahore", lat = 31.4835, lng = 74.3190,
            rating = 4.3, totalReviews = 56, priceMin = 500, priceMax = 1200,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","11:00","14:00","15:00","16:00"),
            verified = true, experienceYears = 8, languages = listOf("urdu","punjabi","english")
        ),
        Provider(
            id = "prov_elec_004", name = "PowerFix Karachi", category = "electrical",
            subcategory = "electrician", phone = "+92-322-0000012",
            area = "PECHS", city = "Karachi", lat = 24.8700, lng = 67.0600,
            rating = 4.5, totalReviews = 92, priceMin = 700, priceMax = 1800,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun"),
            availableSlots = listOf("08:00","09:00","10:00","14:00","15:00","16:00","17:00"),
            verified = true, experienceYears = 6, languages = listOf("urdu","english")
        ),

        // ─── Cleaning / Maid ───
        Provider(
            id = "prov_clean_001", name = "Sparkle Home Cleaners", category = "cleaning",
            subcategory = "home_cleaner", phone = "+92-323-0000013",
            area = "E-11", city = "Islamabad", lat = 33.6860, lng = 73.0130,
            rating = 4.7, totalReviews = 145, priceMin = 1500, priceMax = 3000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("08:00","09:00","10:00","14:00"),
            verified = true, experienceYears = 4, languages = listOf("urdu")
        ),
        Provider(
            id = "prov_clean_002", name = "Clean Home Services", category = "cleaning",
            subcategory = "home_cleaner", phone = "+92-324-0000014",
            area = "G-11", city = "Islamabad", lat = 33.6770, lng = 73.0270,
            rating = 4.4, totalReviews = 78, priceMin = 1200, priceMax = 2500,
            availableDays = listOf("Mon","Tue","Wed","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","11:00","14:00","15:00"),
            verified = true, experienceYears = 3, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_clean_003", name = "Fresh & Clean Lahore", category = "cleaning",
            subcategory = "home_cleaner", phone = "+92-325-0000015",
            area = "Johar Town", city = "Lahore", lat = 31.4600, lng = 74.2900,
            rating = 4.6, totalReviews = 99, priceMin = 1000, priceMax = 2200,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("08:00","09:00","10:00","14:00","15:00"),
            verified = true, experienceYears = 5, languages = listOf("urdu","punjabi")
        ),
        Provider(
            id = "prov_clean_004", name = "Neat & Tidy Karachi", category = "cleaning",
            subcategory = "home_cleaner", phone = "+92-326-0000016",
            area = "Gulshan-e-Iqbal", city = "Karachi", lat = 24.9200, lng = 67.0800,
            rating = 4.2, totalReviews = 53, priceMin = 1300, priceMax = 2800,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun"),
            availableSlots = listOf("08:00","09:00","10:00","14:00","15:00","16:00"),
            verified = false, experienceYears = 2, languages = listOf("urdu","english")
        ),

        // ─── Tutoring ───
        Provider(
            id = "prov_tutor_001", name = "Sir Kamran Tutoring", category = "tutoring",
            subcategory = "home_tutor", phone = "+92-327-0000017",
            area = "F-8", city = "Islamabad", lat = 33.7100, lng = 73.0400,
            rating = 4.9, totalReviews = 234, priceMin = 3000, priceMax = 6000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri"),
            availableSlots = listOf("15:00","16:00","17:00","18:00"),
            verified = true, experienceYears = 12, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_tutor_002", name = "Miss Ayesha Academy", category = "tutoring",
            subcategory = "home_tutor", phone = "+92-328-0000018",
            area = "G-10", city = "Islamabad", lat = 33.6930, lng = 73.0200,
            rating = 4.7, totalReviews = 167, priceMin = 2500, priceMax = 5000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("14:00","15:00","16:00","17:00","18:00"),
            verified = true, experienceYears = 8, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_tutor_003", name = "Learn Smart Lahore", category = "tutoring",
            subcategory = "home_tutor", phone = "+92-329-0000019",
            area = "Garden Town", city = "Lahore", lat = 31.5100, lng = 74.3400,
            rating = 4.5, totalReviews = 89, priceMin = 2000, priceMax = 4500,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri"),
            availableSlots = listOf("14:00","15:00","16:00","17:00"),
            verified = true, experienceYears = 6, languages = listOf("urdu","english","punjabi")
        ),
        Provider(
            id = "prov_tutor_004", name = "Karachi Tutor Hub", category = "tutoring",
            subcategory = "home_tutor", phone = "+92-330-0000020",
            area = "North Nazimabad", city = "Karachi", lat = 24.9400, lng = 67.0300,
            rating = 4.4, totalReviews = 76, priceMin = 2000, priceMax = 5000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("15:00","16:00","17:00","18:00","19:00"),
            verified = true, experienceYears = 7, languages = listOf("urdu","english")
        ),

        // ─── Carpentry ───
        Provider(
            id = "prov_carp_001", name = "Usman Furniture & Repair", category = "carpentry",
            subcategory = "carpenter", phone = "+92-331-0000021",
            area = "I-10", city = "Islamabad", lat = 33.6550, lng = 73.0250,
            rating = 4.6, totalReviews = 78, priceMin = 1000, priceMax = 3000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","11:00","14:00","15:00"),
            verified = true, experienceYears = 15, languages = listOf("urdu")
        ),
        Provider(
            id = "prov_carp_002", name = "WoodCraft Masters", category = "carpentry",
            subcategory = "carpenter", phone = "+92-332-0000022",
            area = "Bahria Town", city = "Lahore", lat = 31.3600, lng = 74.1800,
            rating = 4.8, totalReviews = 122, priceMin = 1500, priceMax = 4000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri"),
            availableSlots = listOf("09:00","10:00","11:00","14:00","15:00","16:00"),
            verified = true, experienceYears = 20, languages = listOf("urdu","punjabi","english")
        ),
        Provider(
            id = "prov_carp_003", name = "Tariq Wood Works", category = "carpentry",
            subcategory = "carpenter", phone = "+92-333-0000023",
            area = "Korangi", city = "Karachi", lat = 24.8300, lng = 67.1300,
            rating = 4.1, totalReviews = 34, priceMin = 800, priceMax = 2500,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun"),
            availableSlots = listOf("08:00","09:00","10:00","14:00","15:00"),
            verified = false, experienceYears = 10, languages = listOf("urdu")
        ),

        // ─── Painting ───
        Provider(
            id = "prov_paint_001", name = "Color Pro Painters", category = "painting",
            subcategory = "painter", phone = "+92-334-0000024",
            area = "F-11", city = "Islamabad", lat = 33.6890, lng = 73.0250,
            rating = 4.5, totalReviews = 67, priceMin = 2000, priceMax = 5000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("08:00","09:00","10:00"),
            verified = true, experienceYears = 9, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_paint_002", name = "Rainbow Paints Lahore", category = "painting",
            subcategory = "painter", phone = "+92-335-0000025",
            area = "Cantt", city = "Lahore", lat = 31.5200, lng = 74.3700,
            rating = 4.3, totalReviews = 45, priceMin = 1800, priceMax = 4500,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri"),
            availableSlots = listOf("08:00","09:00","10:00","14:00"),
            verified = true, experienceYears = 7, languages = listOf("urdu","punjabi")
        ),
        Provider(
            id = "prov_paint_003", name = "Perfect Finish Karachi", category = "painting",
            subcategory = "painter", phone = "+92-336-0000026",
            area = "Defence", city = "Karachi", lat = 24.7950, lng = 67.0650,
            rating = 4.7, totalReviews = 91, priceMin = 2500, priceMax = 6000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun"),
            availableSlots = listOf("08:00","09:00","10:00","14:00","15:00"),
            verified = true, experienceYears = 12, languages = listOf("urdu","english","sindhi")
        ),

        // ─── Pest Control ───
        Provider(
            id = "prov_pest_001", name = "Shield Pest Control", category = "pest_control",
            subcategory = "pest_control", phone = "+92-337-0000027",
            area = "H-13", city = "Islamabad", lat = 33.6500, lng = 73.0000,
            rating = 4.4, totalReviews = 54, priceMin = 2000, priceMax = 5000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","14:00","15:00"),
            verified = true, experienceYears = 6, languages = listOf("urdu","english")
        ),
        Provider(
            id = "prov_pest_002", name = "BugFree Lahore", category = "pest_control",
            subcategory = "pest_control", phone = "+92-338-0000028",
            area = "Wapda Town", city = "Lahore", lat = 31.4500, lng = 74.2700,
            rating = 4.6, totalReviews = 82, priceMin = 1800, priceMax = 4500,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri"),
            availableSlots = listOf("09:00","10:00","11:00","14:00","15:00"),
            verified = true, experienceYears = 8, languages = listOf("urdu","punjabi")
        ),
        Provider(
            id = "prov_pest_003", name = "SafeHome Pest Karachi", category = "pest_control",
            subcategory = "pest_control", phone = "+92-339-0000029",
            area = "Nazimabad", city = "Karachi", lat = 24.9100, lng = 67.0300,
            rating = 4.3, totalReviews = 41, priceMin = 2200, priceMax = 5500,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun"),
            availableSlots = listOf("08:00","09:00","10:00","14:00","15:00","16:00"),
            verified = true, experienceYears = 5, languages = listOf("urdu","english")
        ),

        // ─── Extra Providers for variety ───
        Provider(
            id = "prov_ac_005", name = "Royal AC Services", category = "hvac",
            subcategory = "ac_technician", phone = "+92-340-0000030",
            area = "G-6", city = "Islamabad", lat = 33.7200, lng = 73.0650,
            rating = 4.2, totalReviews = 38, priceMin = 700, priceMax = 1100,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri"),
            availableSlots = listOf("10:00","11:00","14:00","15:00","16:00"),
            verified = false, experienceYears = 3, languages = listOf("urdu")
        ),
        Provider(
            id = "prov_plumb_005", name = "FlowFix Plumbing", category = "plumbing",
            subcategory = "plumber", phone = "+92-341-0000031",
            area = "G-14", city = "Islamabad", lat = 33.6700, lng = 73.0000,
            rating = 4.5, totalReviews = 61, priceMin = 500, priceMax = 1000,
            availableDays = listOf("Mon","Tue","Wed","Thu","Fri","Sat"),
            availableSlots = listOf("09:00","10:00","11:00","14:00","15:00"),
            verified = true, experienceYears = 9, languages = listOf("urdu","english")
        ),
    )

    fun getProvidersByCategory(category: String): List<Provider> =
        allProviders.filter { it.category == category }

    fun getProvidersByCity(city: String): List<Provider> =
        allProviders.filter { it.city.equals(city, ignoreCase = true) }

    fun getProviderById(id: String): Provider? =
        allProviders.find { it.id == id }

    fun searchProviders(category: String, city: String? = null, area: String? = null): List<Provider> {
        return allProviders.filter { provider ->
            provider.category == category &&
            (city == null || provider.city.equals(city, ignoreCase = true)) &&
            (area == null || provider.area.contains(area, ignoreCase = true))
        }
    }
}
