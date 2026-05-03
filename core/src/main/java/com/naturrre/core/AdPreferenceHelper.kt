package com.naturrre.core

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject

class AdPreferenceHelper(val context: Context) {
    private val prefs = context.getSharedPreferences("ad_settings", Context.MODE_PRIVATE)

    var currentStrategy: AdStrategy
        get() {
            val name = prefs.getString("selected_strategy", AdStrategy.AUTO.name)
            return AdStrategy.valueOf(name ?: AdStrategy.AUTO.name)
        }
        set(value) {
            prefs.edit { putString("selected_strategy", value.name) }
        }

    var marketName: MarketName
        get() {
            val name = prefs.getString("MarketName", MarketName.GOOGLE_PLAY.name)
            return MarketName.valueOf(name ?: MarketName.GOOGLE_PLAY.name)
        }
        set(value) {
            prefs.edit { putString("MarketName", value.name) }
        }

    // ذخیره آخرین زمان موفقیت آمیز دریافت داده
    private var lastFetchTime: Long
        get() = prefs.getLong("last_fetch_time", 0L)
        set(value) = prefs.edit { putLong("last_fetch_time", value) }

    fun getDataRemote(action: () -> Unit) {
        val now = System.currentTimeMillis()
        val oneWeekMillis = 7L * 24 * 60 * 60 * 1000 // یک هفته به میلی‌ثانیه

        // بررسی اینکه یک هفته گذشته باشد
        if (now - lastFetchTime < oneWeekMillis) {
            Log.e("shaban", "یک هفته از آخرین fetch نگذشته، رد شد.")
            return
        }

        log("getDataRemote: ارسال درخواست به سرور، market=${marketName.name}")
        AndroidNetworking.initialize(context)
        AndroidNetworking.post("https://naturrregenius.ir/getAdsApp/getAppAds.php")
            .addBodyParameter("package_name", context.packageName)
            .addBodyParameter("market_name", marketName.name)
            .setTag("getDataRemote")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    lastFetchTime = now // ذخیره زمان موفق
                    log( "onResponse: $response")
                    response?.let {
                        parseAppConfig(it)?.let { appConfig ->
                            currentStrategy = appConfig.adStrategy

                            action()
                        }
                    }
                }

                override fun onError(error: ANError?) {
                }
            })
    }

    fun parseAppConfig(json: JSONObject): AppConfig? {
        return try {
            AppConfig(
                id = json.getInt("id"),
                appName = json.getString("app_name"),
                packageName = json.getString("package_name"),
                marketName = try {
                    MarketName.valueOf(json.getString("market_name"))
                } catch (e: Exception) {
                    MarketName.GOOGLE_PLAY
                },
                adStrategy = try {
                    AdStrategy.valueOf(json.getString("ad_strategy"))
                } catch (e: Exception) {
                    AdStrategy.AUTO
                },
                isActive = json.getInt("is_active") == 1,
                updatedAt = json.optString("updated_at", null)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}