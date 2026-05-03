package com.naturrre.libraryad

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.naturrre.core.AdConfigs
import com.naturrre.core.AdListener
import com.naturrre.core.AdManager
import com.naturrre.core.AdPreferenceHelper
import com.naturrre.provider_tapsell.TapsellProvider
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    // ۱. تزریق وابستگی‌ها در ابتدای کلاس Activity
    private val adManager: AdManager by inject()
    private val adConfigs: AdConfigs by inject()
    private val adPreferenceHelper: AdPreferenceHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)




        // ۲. نحوه استفاده در onCreate یا هر متد دیگر
        val bannerContainer = findViewById<FrameLayout>(R.id.bannerContainer)

        adManager.loadSmartBanner(
            this,
            bannerContainer,
            // استفاده از adConfigs تزریق شده به جای AdSdk
            adConfigs.standardBanner
        )

        adManager.loadAndShowSmart(
            this,
            adConfigs.mainInterstitial,
            object : AdListener {
                override fun onAdOpened() { /* تبلیغ باز شد */
                }

                override fun onAdClosed() { /* تبلیغ بسته شد */
                }

                override fun onAdClicked() { /* روی تبلیغ کلیک شد */
                }

                override fun onAdImpression() { /* تبلیغ دیده شد */
                }

                override fun onUserEarnedReward(amount: Int, type: String) {
                    // منطق جایزه در ویدیوهای جایزه‌دار
                }
            }
        )

    }


}