# Ads Manager Core

A Kotlin library for managing **AdMob**, **Yandex Ads**, and **Tapsell** with a unified API, allowing easy switching between ad networks in Android apps.

## Installation

Add this to your app module `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.shabanshaban:ads-manager-core:1.0.0'
}
Quick Start
1. Initialize Koin and Ad Manager
class MyApp : Application() {

    private val adManager: AdManager by inject()
    private val adConfigs: AdConfigs by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(coreModule, admobModule, yandexModule, tapsellModule))
        }

        val appKeys = mapOf(
            AdServiceType.ADMOB to "ca-app-pub-3940256099942544~3347511713",
            AdServiceType.TAPSELL to "tapsell-app-key",
            AdServiceType.YANDEX to "demo-app-yandex"
        )

        adManager.init(this, appKeys)
        adConfigs.prefHelper.marketName = MarketName.CAFE_BAZAAR
        adConfigs.prefHelper.currentStrategy = AdStrategy.FORCE_TAPSELL
    }
}
2. Load and show a banner ad
val bannerContainer = findViewById<FrameLayout>(R.id.bannerContainer)

adManager.loadSmartBanner(
    this,
    bannerContainer,
    adConfigs.standardBanner
)
3. Load and show an interstitial or rewarded ad
adManager.loadAndShowSmart(
    this,
    adConfigs.mainInterstitial,
    object : AdListener {
        override fun onAdOpened() {}
        override fun onAdClosed() {}
        override fun onAdClicked() {}
        override fun onAdImpression() {}
        override fun onUserEarnedReward(amount: Int, type: String) {}
    }
)

Features
Easily switch between AdMob, Yandex Ads, and Tapsell
Unified management for Banner, Interstitial, and Rewarded Video ads
Simple integration using Koin
Configurable ad strategy and target market
