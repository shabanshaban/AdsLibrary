package com.naturrre.core

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.androidnetworking.AndroidNetworking

class AdManager(

    private val providers: List<AdProvider>
) {



    // در کلاس AdManager
    // داخل کلاس AdManager

    fun init(context: Context, appKeys: Map<AdServiceType, String>) {


        log("🚀 Initializing AdManager...")
        log("Total Providers Found: ${providers.size}")

        providers.forEach { provider ->
            val type = getProviderType(provider)
            val key = appKeys[type]

            if (key != null) {
                log("✅ Initializing $type with key: $key")
                provider.initialize(context, key)
            } else {
                log("⏩ Skipping $type: No key provided in appKeys.", Log.WARN)
            }
        }
    }

    fun loadSmartBanner(
        context: Context,
        container: ViewGroup,
        config: AdRemoteConfig
    ) {
        log("🎬 loadSmartBanner requested")
        tryNextBannerPriority(0, context, container, config)
    }

    private fun tryNextBannerPriority(
        priorityIndex: Int,
        context: Context,
        container: ViewGroup,
        config: AdRemoteConfig
    ) {

        val priorityList = config.priorityList(context)

        if (priorityIndex >= priorityList.size) {
            log("❌ شکست نهایی: تمام پروایدرهای بنر بررسی شدند و هیچکدام لود نشدند.", Log.ERROR)
            return
        }

        val currentType = priorityList[priorityIndex]
        val adUnitId = config.adUnitIds[currentType]
        val provider = providers.find { getProviderType(it) == currentType }

        if (provider == null) {
            log("⚠️ هشدار: پروایدر برای $currentType در لیست تزریق پیدا نشد. بعدی...", Log.WARN)
            tryNextBannerPriority(priorityIndex + 1, context, container, config)
            return
        }

        if (adUnitId == null) {
            log("⚠️ هشدار: آیدی بنر برای $currentType در کانفیگ وجود ندارد.", Log.WARN)
            tryNextBannerPriority(priorityIndex + 1, context, container, config)
            return
        }

        log("⏳ در حال تلاش برای لود بنر از: $currentType با آیدی: $adUnitId")

        provider.getBannerAd(context, adUnitId,
            onLoaded = { bannerView ->
                log("✅ موفقیت: بنر $currentType با موفقیت لود و جایگذاری شد.", Log.INFO)
                container.removeAllViews()
                container.addView(bannerView)
            },
            onError = { error ->
                log("❌ خطا در $currentType: $error. تلاش برای اولویت بعدی...", Log.ERROR)
                tryNextBannerPriority(priorityIndex + 1, context, container, config)
            }
        )
    }

    fun loadAndShowRewardedSmart(
        activity: Activity,
        config: AdRemoteConfig,
        listener: AdListener? = null
    ) {
        log("🎬 loadAndShowRewardedSmart requested")
        tryNextRewardedPriority(0, activity, config, listener)
    }

    private fun tryNextRewardedPriority(
        priorityIndex: Int,
        activity: Activity,
        config: AdRemoteConfig,
        listener: AdListener?
    ) {
        val priorityList = config.priorityList(activity)

        if (priorityIndex >= priorityList.size) {
            log("❌ هیچ ویدیوی جایزه‌ای در لیست اولویت‌ها لود نشد.", Log.ERROR)
            return
        }

        val currentServiceType = priorityList[priorityIndex]
        val adUnitId = config.adUnitIds[currentServiceType]
        val provider = providers.find { getProviderType(it) == currentServiceType }

        if (provider != null && adUnitId != null) {
            log("⏳ در حال تلاش برای بارگذاری ویدیوی جایزه‌ای: $currentServiceType")

            provider.loadRewarded(
                context = activity,
                adUnitId = adUnitId,
                onLoaded = {
                    log("✅ ویدیوی $currentServiceType لود شد. در حال نمایش...")
                    provider.showRewarded(activity, listener)
                },
                onError = { error ->
                    log("❌ خطای ویدیو در $currentServiceType: $error. بعدی...", Log.ERROR)
                    tryNextRewardedPriority(priorityIndex + 1, activity, config, listener)
                }
            )
        } else {
            tryNextRewardedPriority(priorityIndex + 1, activity, config, listener)
        }
    }

    fun loadAndShowSmart(activity: Activity, config: AdRemoteConfig, listener: AdListener? = null) {
        log("🎬 loadAndShowSmart (Interstitial) requested")
        tryNextPriority(activity,0, activity, config, listener)
    }

    private fun tryNextPriority(context: Context,priorityIndex: Int, activity: Activity, config: AdRemoteConfig, listener: AdListener?) {
        val priorityList = config.priorityList(context)

        if (priorityIndex >= priorityList.size) {
            log("❌ تمام سرویس‌های لیست اولویت با خطا مواجه شدند.", Log.ERROR)
            return
        }

        val currentServiceType = priorityList[priorityIndex]
        val adUnitId = config.adUnitIds[currentServiceType]
        val provider = providers.find { getProviderType(it) == currentServiceType }

        if (provider != null && adUnitId != null) {
            log("⏳ در حال تلاش برای میان‌صفحه‌ای: $currentServiceType")

            provider.loadInterstitial(
                context = activity,
                adUnitId = adUnitId,
                onLoaded = {
                    log("✅ میان‌صفحه‌ای $currentServiceType لود شد. در حال نمایش...")
                    provider.showInterstitial(activity, listener)
                },
                onError = { error ->
                    log("❌ سرویس $currentServiceType شکست خورد. بعدی...", Log.ERROR)
                    tryNextPriority(context,priorityIndex + 1, activity, config, listener)
                }
            )
        } else {
            tryNextPriority(context,priorityIndex + 1, activity, config, listener)
        }
    }

    private fun getProviderType(provider: AdProvider): AdServiceType {
        val className = provider.javaClass.simpleName
        return when {
            className.contains("Tapsell", ignoreCase = true) -> AdServiceType.TAPSELL
            className.contains("AdMob", ignoreCase = true) -> AdServiceType.ADMOB
            className.contains("Yandex", ignoreCase = true) -> AdServiceType.YANDEX
            else -> AdServiceType.NONE
        }
    }
}