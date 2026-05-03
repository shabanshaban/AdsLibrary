package com.naturrre.core

import androidx.annotation.Keep

@Keep
data class AppConfig(
    val id: Int,
    val appName: String,
    val packageName: String,
    val marketName: MarketName = MarketName.GOOGLE_PLAY, // مقدار پیش‌فرض
    val adStrategy: AdStrategy = AdStrategy.AUTO,
    val isActive: Boolean = true,
    val updatedAt: String? = null // یا LocalDateTime
)

