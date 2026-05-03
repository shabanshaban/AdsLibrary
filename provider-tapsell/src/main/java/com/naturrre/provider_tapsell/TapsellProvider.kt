package com.naturrre.provider_tapsell

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.naturrre.core.AdListener
import com.naturrre.core.AdProvider
import com.naturrre.core.log
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusBannerType
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class TapsellProvider : AdProvider {

    private var responseId: String? = null
    private var rewardedResponseId: String? = null
    private val TAG = "shaban"



    override fun initialize(context: Context, appKey: String) {
        log("🚀 Initializing Tapsell Plus SDK...$appKey")
        TapsellPlus.initialize(context, appKey, object : TapsellPlusInitListener {
            override fun onInitializeSuccess(p0: AdNetworks?) {
                log("✅ Tapsell: Initialized Successfully", Log.INFO)
            }

            override fun onInitializeFailed(p0: AdNetworks?, error: AdNetworkError?) {
                log("❌ Tapsell Initialize Failed: ${error?.errorMessage}", Log.ERROR)
            }
        })
    }

    override fun getBannerAd(
        context: Context,
        adUnitId: String,
        onLoaded: (View) -> Unit,
        onError: (String) -> Unit
    ) {
        log("⏳ Requesting Tapsell Standard Banner...adUnitId="+adUnitId)
        val activity = context as? Activity
        if (activity == null) {
            log("❌ Error: Context is not an Activity", Log.ERROR)
            onError("Context is not an Activity")
            return
        }

        TapsellPlus.requestStandardBannerAd(
            activity,
            adUnitId,
            TapsellPlusBannerType.BANNER_320x50,
            object : AdRequestCallback() {
                override fun response(adModel: TapsellPlusAdModel) {
                    log("✅ Tapsell Banner Loaded", Log.INFO)
                    val container = FrameLayout(context)
                    TapsellPlus.showStandardBannerAd(activity, adModel.responseId, container, object : AdShowListener() {})
                    onLoaded(container)
                }

                override fun error(message: String) {
                    log("❌ Tapsell Banner Failed: $message", Log.ERROR)
                    onError(message)
                }
            }
        )
    }

    override fun loadInterstitial(
        context: Context,
        adUnitId: String,
        onLoaded: () -> Unit,
        onError: (String) -> Unit
    ) {

        log("⏳ Requesting Tapsell Interstitial...")
        val activity = context as? Activity
        if (activity == null) {
            log("❌ Error: Context is not an Activity", Log.ERROR)
            onError("Context is not an Activity")
            return
        }

        TapsellPlus.requestInterstitialAd(
            activity,
            adUnitId,
            object : AdRequestCallback() {
                override fun response(adModel: TapsellPlusAdModel) {
                    log("✅ Tapsell Interstitial Loaded", Log.INFO)
                    responseId = adModel.responseId
                    onLoaded()
                }

                override fun error(message: String) {
                    log("❌ Tapsell Interstitial Failed: $message", Log.ERROR)
                    onError(message)
                }
            }
        )
    }

    override fun showInterstitial(activity: Activity, listener: AdListener?) {
        if (responseId == null) {
            log("⚠️ Tapsell: Interstitial not ready (responseId is null)", Log.WARN)
            return
        }
        log("⚠️ Tapsell: responseId = "+ responseId)

        TapsellPlus.showInterstitialAd(activity, responseId, object : AdShowListener() {
            override fun onOpened(adModel: TapsellPlusAdModel) {
                log("📺 Tapsell Interstitial Displayed")
                listener?.onAdOpened()
            }

            override fun onClosed(adModel: TapsellPlusAdModel) {
                log("🎬 Tapsell Interstitial Closed")
                responseId = null
                listener?.onAdClosed()
            }

            override fun onError(p0: TapsellPlusErrorModel?) {
                log("❌ Tapsell Interstitial Show Error: ${p0?.errorMessage}", Log.ERROR)
                listener?.onAdClosed()
            }
        })
    }

    override fun loadRewarded(
        context: Context,
        adUnitId: String,
        onLoaded: () -> Unit,
        onError: (String) -> Unit
    ) {
        log("⏳ Requesting Tapsell Rewarded Video...")
        val activity = context as? Activity
        if (activity == null) {
            onError("Context is not an Activity")
            return
        }

        TapsellPlus.requestRewardedVideoAd(activity, adUnitId, object : AdRequestCallback() {
            override fun response(adModel: TapsellPlusAdModel) {
                log("✅ Tapsell Rewarded Loaded", Log.INFO)
                rewardedResponseId = adModel.responseId
                onLoaded()
            }

            override fun error(message: String) {
                log("❌ Tapsell Rewarded Failed: $message", Log.ERROR)
                onError(message)
            }
        })
    }

    override fun showRewarded(activity: Activity, listener: AdListener?) {
        if (rewardedResponseId == null) {
            log("⚠️ Tapsell: Rewarded not ready (rewardedResponseId is null)", Log.WARN)
            return
        }

        TapsellPlus.showRewardedVideoAd(activity, rewardedResponseId, object : AdShowListener() {
            override fun onOpened(adModel: TapsellPlusAdModel) {
                log("📺 Tapsell Rewarded Displayed")
                listener?.onAdOpened()
            }

            override fun onClosed(adModel: TapsellPlusAdModel) {
                log("🎬 Tapsell Rewarded Closed")
                rewardedResponseId = null
                listener?.onAdClosed()
            }

            override fun onRewarded(adModel: TapsellPlusAdModel) {
                log("💰 User earned reward from Tapsell")
                listener?.onUserEarnedReward(1, "credit")
            }

            override fun onError(p0: TapsellPlusErrorModel?) {
                log("❌ Tapsell Rewarded Show Error: ${p0?.errorMessage}", Log.ERROR)
                listener?.onAdClosed()
            }
        })
    }
}