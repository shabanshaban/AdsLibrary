package com.naturrre.core

import android.content.Context

object AdSdk {
    private var _adManager: AdManager? = null
    private var _adConfigs: AdConfigs? = null

    @JvmStatic // این باعث می‌شود در جاوا بنویسد AdSdk.init(...)
    fun init(
        context: Context,
        providers: List<AdProvider>,
        bannerUnits: Map<AdServiceType, String>? = null, // اختیاری
        interstitialUnits: Map<AdServiceType, String>? = null // اختیاری
    ) {
        val appContext = context.applicationContext
        val prefHelper = AdPreferenceHelper(appContext)

        // اگر کاربر آیدی فرستاده بود استفاده کن، وگرنه بگذار AdConfigs از پیش‌فرض خودش استفاده کند
        _adConfigs = when {
            bannerUnits != null && interstitialUnits != null ->
                AdConfigs(prefHelper, bannerUnits, interstitialUnits)
            bannerUnits != null -> AdConfigs(prefHelper, bannerUnits = bannerUnits)
            else -> AdConfigs(prefHelper)
        }

        _adManager = AdManager(providers).apply {
            init(appContext, (_adConfigs!!.bannerUnits + _adConfigs!!.interstitialUnits))
        }
    }
    @JvmStatic
    val manager: AdManager get() = _adManager!!
    val configs: AdConfigs get() = _adConfigs!!
}