package com.evolvarc.smartlens.util

import java.util.concurrent.TimeUnit

object TimeUtils {
    fun getTimeAgoString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            }
            diff < TimeUnit.DAYS.toMillis(30) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "$days ${if (days == 1L) "day" else "days"} ago"
            }
            else -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "${days / 30} ${if (days / 30 == 1L) "month" else "months"} ago"
            }
        }
    }
    
    fun isCacheExpired(timestamp: Long, maxAgeInDays: Int = 30): Boolean {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return diff > TimeUnit.DAYS.toMillis(maxAgeInDays.toLong())
    }
}
