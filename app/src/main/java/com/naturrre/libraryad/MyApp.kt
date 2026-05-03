package com.naturrre.libraryad

import android.app.Application
import com.naturrre.core.AdConfigs
import com.naturrre.core.AdManager
import com.naturrre.core.AdServiceType
import com.naturrre.core.MarketName
import com.naturrre.core.coreModule
import com.naturrre.provider_admob.admobModule
import com.naturrre.provider_tapsell.tapsellModule
import com.naturrre.provider_yandex.yandexModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

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
            // 👈 کلید اصلی تپسل فقط برای اینیت کردن
            AdServiceType.TAPSELL to "alsoatsrtrotpqacegkehkaiieckldhrgsbspqtgqnbrrfccrtbdomgjtahflchkqtqosa",
            AdServiceType.YANDEX to "demo-app-yandex"
        )
        // فقط همین یک خط برای اینیت کردن کل سیستم کافیست

        adConfigs.prefHelper.marketName= MarketName.CAFE_BAZAAR
        adManager.init(this, appKeys)
        adConfigs.prefHelper.getDataRemote{

        }
    }
}