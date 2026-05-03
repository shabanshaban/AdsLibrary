package com.naturrre.provider_yandex

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import com.naturrre.core.AdListener
import com.naturrre.core.AdProvider
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class YandexProvider : AdProvider {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private val TAG = "shaban"

    // سیستم لاگ هوشمند (بدون نیاز به BuildConfig برای جلوگیری از ارور)
    private fun log(message: String, type: Int = Log.DEBUG, context: Context? = null) {
        val isDebug = context?.let {
            (it.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } ?: true

        if (isDebug) {
            when (type) {
                Log.INFO -> Log.e(TAG, message)
                Log.WARN -> Log.e(TAG, message)
                Log.ERROR -> Log.e(TAG, message)
                else -> Log.e(TAG, message)
            }
        }
    }

    override fun initialize(context: Context, appKey: String) {
        log("🚀 Initializing Yandex Mobile Ads SDK...")
        MobileAds.initialize(context) {
            log("✅ Yandex: SDK Initialized Successfully", Log.INFO)
        }
    }

    // --- INTERSTITIAL ---
    override fun loadInterstitial(context: Context, adUnitId: String, onLoaded: () -> Unit, onError: (String) -> Unit) {
        log("⏳ Requesting Yandex Interstitial...")
        val loader = InterstitialAdLoader(context)
        loader.setAdLoadListener(object : InterstitialAdLoadListener {
            override fun onAdLoaded(ad: InterstitialAd) {
                log("✅ Yandex Interstitial Loaded", Log.INFO)
                interstitialAd = ad
                onLoaded()
            }
            override fun onAdFailedToLoad(error: AdRequestError) {
                log("❌ Yandex Interstitial Failed: ${error.description}", Log.ERROR)
                onError(error.description)
            }
        })
        val config = AdRequestConfiguration.Builder(adUnitId).build()
        loader.loadAd(config)
    }

    override fun showInterstitial(activity: Activity, listener: AdListener?) {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    log("📺 Yandex Interstitial Displayed")
                    listener?.onAdOpened()
                }
                override fun onAdFailedToShow(error: AdError) {
                    log("❌ Yandex Interstitial Show Failed: ${error.description}", Log.ERROR)
                    listener?.onAdClosed()
                }
                override fun onAdDismissed() {
                    log("🎬 Yandex Interstitial Dismissed")
                    listener?.onAdClosed()
                }
                override fun onAdClicked() {
                    log("🖱️ Yandex Interstitial Clicked")
                    listener?.onAdClicked()
                }
                override fun onAdImpression(p0: ImpressionData?) {
                    listener?.onAdImpression()
                }
            })
            show(activity)
        } ?: log("⚠️ Yandex Interstitial not ready to show", Log.WARN)
    }

    // --- REWARDED ---
    override fun loadRewarded(context: Context, adUnitId: String, onLoaded: () -> Unit, onError: (String) -> Unit) {
        log("⏳ Requesting Yandex Rewarded Video...")
        val loader = RewardedAdLoader(context)
        loader.setAdLoadListener(object : RewardedAdLoadListener {
            override fun onAdLoaded(ad: RewardedAd) {
                log("✅ Yandex Rewarded Loaded", Log.INFO)
                rewardedAd = ad
                onLoaded()
            }
            override fun onAdFailedToLoad(error: AdRequestError) {
                log("❌ Yandex Rewarded Failed: ${error.description}", Log.ERROR)
                onError(error.description)
            }
        })
        val config = AdRequestConfiguration.Builder(adUnitId).build()
        loader.loadAd(config)
    }

    override fun showRewarded(activity: Activity, listener: AdListener?) {
        rewardedAd?.apply {
            setAdEventListener(object : RewardedAdEventListener {
                override fun onAdShown() {
                    log("📺 Yandex Rewarded Displayed")
                    listener?.onAdOpened()
                }
                override fun onAdFailedToShow(error: AdError) {
                    log("❌ Yandex Rewarded Show Failed: ${error.description}", Log.ERROR)
                    listener?.onAdClosed()
                }
                override fun onAdDismissed() {
                    log("🎬 Yandex Rewarded Dismissed")
                    listener?.onAdClosed()
                }
                override fun onAdClicked() {
                    log("🖱️ Yandex Rewarded Clicked")
                    listener?.onAdClicked()
                }
                override fun onAdImpression(p0: ImpressionData?) {
                    listener?.onAdImpression()
                }
                override fun onRewarded(reward: Reward) {
                    log("💰 User earned Yandex reward: ${reward.amount} ${reward.type}")
                    listener?.onUserEarnedReward(reward.amount, reward.type)
                }
            })
            show(activity)
        } ?: log("⚠️ Yandex Rewarded not ready to show", Log.WARN)
    }

    // --- BANNER ---
    override fun getBannerAd(context: Context, adUnitId: String, onLoaded: (View) -> Unit, onError: (String) -> Unit) {
        log("⏳ Requesting Yandex Banner...")
        val bannerAdView = BannerAdView(context)
        bannerAdView.setAdUnitId(adUnitId)
        bannerAdView.setAdSize(BannerAdSize.stickySize(context, 320))
        bannerAdView.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                log("✅ Yandex Banner Loaded", Log.INFO)
                onLoaded(bannerAdView)
            }
            override fun onAdFailedToLoad(error: AdRequestError) {
                log("❌ Yandex Banner Failed: ${error.description}", Log.ERROR)
                onError(error.description)
            }
            override fun onAdClicked() {
                log("🖱️ Yandex Banner Clicked")
            }
            override fun onLeftApplication() {}
            override fun onReturnedToApplication() {}
            override fun onImpression(p0: ImpressionData?) {
                log("📊 Yandex Banner Impression")
            }
        })
        bannerAdView.loadAd(AdRequest.Builder().build())
    }
}