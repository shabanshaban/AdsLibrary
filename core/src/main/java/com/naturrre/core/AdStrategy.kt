package com.naturrre.core

import androidx.annotation.Keep


@Keep
enum class AdStrategy {
    AUTO,           // حالت هوشمند (بر اساس لوکیشن و VPN)
    FORCE_TAPSELL,  // فقط تپسل
    FORCE_ADMOB,    // فقط ادموب
    FORCE_YANDEX    // فقط یاندکس (گزینه چهارم که احتمالا منظورت یاندکس بود)
}