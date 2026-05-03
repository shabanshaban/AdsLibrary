package com.naturrre.core

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


// CoreModule.kt
// در فایل ماژول (مثلاً coreModule)
val coreModule = module {
    single { AdPreferenceHelper(androidContext()) }
    single { AdManager(getAll()) }
    single {
        AdConfigs(
            prefHelper = get(),
            bannerUnits = mapOf(
                AdServiceType.TAPSELL to "5cfaaa30e8d17f0001ffb294", // 👈 فقط Zone ID بنر
                AdServiceType.ADMOB to "ca-app-pub-3940256099942544/6300978111",
                AdServiceType.YANDEX to "demo-banner-yandex"
            ),
            interstitialUnits = mapOf(
                AdServiceType.TAPSELL to "5cfaa942e8d17f0001ffb292", // 👈 فقط Zone ID اینترستیشال
                AdServiceType.ADMOB to "ca-app-pub-3940256099942544/1033173712",
                AdServiceType.YANDEX to "demo-interstitial-yandex"
            )
        )
    }
}