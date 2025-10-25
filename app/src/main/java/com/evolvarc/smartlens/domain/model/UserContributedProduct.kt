package com.evolvarc.smartlens.domain.model

data class UserContributedProduct(
    val barcode: String = "",
    val productName: String = "",
    val brandName: String? = null,
    val category: String = "UNKNOWN",
    val frontImageUrl: String? = null,
    val backImageUrl: String? = null,
    val contributedBy: String = "", // User ID
    val deviceId: String = "",
    val contributedAt: Long = System.currentTimeMillis(),
    val status: String = "pending" // pending, approved, rejected
)

data class DeviceContributionLimit(
    val deviceId: String = "",
    val contributionCount: Int = 0,
    val lastContributedAt: Long = 0L
)
