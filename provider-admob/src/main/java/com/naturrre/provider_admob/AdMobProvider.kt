package com.naturrre.provider_admob

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.naturrre.core.AdListener
import com.naturrre.core.AdProvider
import com.naturrre.core.log
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class AdMobProvider : AdProvider {

    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null

    // سیستم لاگ هوشمند (بدون نیاز به BuildConfig)


    override fun initialize(context: Context, appKey: String) {
        log("🚀 Initializing AdMob SDK...")
        MobileAds.initialize(context) {
            log("✅ AdMob SDK Initialized Successfully")
        }
    }

    override fun getBannerAd(
        context: Context,
        adUnitId: String,
        onLoaded: (View) -> Unit,
        onError: (String) -> Unit
    ) {
        log("⏳ Requesting AdMob Banner...")
        val adView = AdView(context)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = adUnitId

        adView.adListener = object : com.google.android.gms.ads.AdListener() {
            override fun onAdLoaded() {
                log("✅ AdMob Banner Loaded", Log.INFO)
                onLoaded(adView)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                log("❌ AdMob Banner Failed: ${error.message}", Log.ERROR)
                onError(error.message)
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    override fun loadInterstitial(
        context: Context,
        adUnitId: String,
        onLoaded: () -> Unit,
        onError: (message: String) -> Unit
    ) {
        log("⏳ Requesting AdMob Interstitial...")
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                log("❌ AdMob Interstitial Failed: ${adError.message}", Log.ERROR)
                mInterstitialAd = null
                onError(adError.message)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                log("✅ AdMob Interstitial Loaded", Log.INFO)
                mInterstitialAd = interstitialAd
                onLoaded()
            }
        })
    }

    override fun showInterstitial(activity: Activity, listener: AdListener?) {
        mInterstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    log("🎬 AdMob Interstitial Dismissed")
                    mInterstitialAd = null
                    listener?.onAdClosed()
                }
                override fun onAdShowedFullScreenContent() {
                    log("📺 AdMob Interstitial Displayed")
                    listener?.onAdOpened()
                }
                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    log("❌ AdMob Interstitial Show Failed: ${adError.message}", Log.ERROR)
                    mInterstitialAd = null
                }
                override fun onAdClicked() {
                    log("🖱️ AdMob Interstitial Clicked")
                    listener?.onAdClicked()
                }
            }
            ad.show(activity)
        } ?: log("⚠️ AdMob Interstitial not ready to show", Log.WARN)
    }

    override fun loadRewarded(context: Context, adUnitId: String, onLoaded: () -> Unit, onError: (String) -> Unit) {
        log("⏳ Requesting AdMob Rewarded...")
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                log("❌ AdMob Rewarded Failed: ${adError.message}", Log.ERROR)
                mRewardedAd = null
                onError(adError.message)
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                log("✅ AdMob Rewarded Loaded", Log.INFO)
                mRewardedAd = rewardedAd
                onLoaded()
            }
        })
    }

    override fun showRewarded(activity: Activity, listener: AdListener?) {
        mRewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    log("🎬 AdMob Rewarded Dismissed")
                    mRewardedAd = null
                    listener?.onAdClosed()
                }
            }
            ad.show(activity) { rewardItem ->
                log("💰 User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                listener?.onUserEarnedReward(rewardItem.amount, rewardItem.type)
            }
        } ?: log("⚠️ AdMob Rewarded not ready to show", Log.WARN)
    }
}