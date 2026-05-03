package com.naturrre.core

import android.app.Activity
import android.content.Context
import android.view.View

interface AdProvider {
        // دریافت کلید از طریق پارامتر appKey
        fun initialize(context: Context, appKey: String)

        fun loadInterstitial(
            context: Context,
            adUnitId: String,
            onLoaded: () -> Unit,
            onError: (message: String) -> Unit
        )

        fun showInterstitial(activity: Activity, listener: AdListener? = null)
    fun loadRewarded(
        context: Context,
        adUnitId: String,
        onLoaded: () -> Unit,
        onError: (String) -> Unit
    )

    fun showRewarded(activity: Activity, listener: AdListener? = null)

    /**
     * بارگذاری و بازگرداندن یک بنر تبلیغاتی.
     * چون بنر یک ویو هست، به جای نمایش خودکار، آن را باز می‌گردانیم.
     */
    fun getBannerAd(
        context: Context,
        adUnitId: String,
        onLoaded: (View) -> Unit, // ارسال ویو به محض آماده شدن
        onError: (String) -> Unit
    )
}