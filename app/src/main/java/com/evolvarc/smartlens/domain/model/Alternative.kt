package com.evolvarc.smartlens.domain.model

data class Alternative(
    val product: Product,
    val improvementReason: String,
    val scoreDifference: Int
)
