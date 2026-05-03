package com.naturrre.core

class AdConfigs(
    val prefHelper: AdPreferenceHelper,
    // مقادیر پیش‌فرض برای بنر و اینترستیشال
    val bannerUnits: Map<AdServiceType, String> = mapOf(
        AdServiceType.TAPSELL to "5cfaaa30e8d17f0001ffb294",
        AdServiceType.ADMOB to "ca-app-pub-3940256099942544/6300978111",
        AdServiceType.YANDEX to "demo-banner-yandex"
    ),
    val interstitialUnits: Map<AdServiceType, String> = mapOf(
        AdServiceType.TAPSELL to "5cfaa942e8d17f0001ffb292",
        AdServiceType.ADMOB to "ca-app-pub-3940256099942544/1033173712",
        AdServiceType.YANDEX to "demo-interstitial-yandex"
    )
) {
    val standardBanner: AdRemoteConfig
        get() = AdRemoteConfig(prefHelper.currentStrategy, bannerUnits)

    val mainInterstitial: AdRemoteConfig
        get() = AdRemoteConfig(prefHelper.currentStrategy, interstitialUnits)
}