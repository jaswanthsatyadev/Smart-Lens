package com.evolvarc.smartlens.util

object Constants {
    const val CACHE_EXPIRY_DAYS = 30L
    const val DATABASE_NAME = "smartlens_database"
    
    const val FOOD_API_BASE_URL = "https://world.openfoodfacts.org/"
    const val BEAUTY_API_BASE_URL = "https://world.openbeautyfacts.org/"
    const val PRODUCTS_API_BASE_URL = "https://world.openproductsfacts.org/"
    
    const val SCAN_COOLDOWN_MS = 3000L
    
    val HARMFUL_INGREDIENTS = listOf(
        "Sodium Laureth Sulfate",
        "Sodium Lauryl Sulfate",
        "Methylparaben",
        "Propylparaben",
        "Butylparaben",
        "Ethylparaben",
        "Fragrance",
        "Parfum",
        "Phthalate",
        "Triclosan",
        "Formaldehyde",
        "Coal Tar",
        "Hydroquinone",
        "BHA",
        "BHT"
    )
}
