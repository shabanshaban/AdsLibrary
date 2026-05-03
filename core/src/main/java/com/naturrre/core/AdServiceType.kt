package com.naturrre.core

import android.content.Context
import androidx.annotation.Keep

@Keep
enum class AdServiceType { ADMOB, YANDEX, TAPSELL, NONE }

@Keep
data class AdRemoteConfig(
    val strategy: AdStrategy, // اضافه شدن نوع استراتژی
    val adUnitIds: Map<AdServiceType, String>
) {
    fun priorityList(context: Context): List<AdServiceType> {
        return when (strategy) {
            AdStrategy.FORCE_TAPSELL -> listOf(AdServiceType.TAPSELL)
            AdStrategy.FORCE_ADMOB -> listOf(AdServiceType.ADMOB)
            AdStrategy.FORCE_YANDEX -> listOf(AdServiceType.YANDEX)
            AdStrategy.AUTO -> {
                // همان منطق هوشمند قبلی که با هم نوشتیم
                val isInIran = DeviceUtils.isLikelyInIran(context)
                val isVpnOn = NetworkUtils.isVpnActive(context)

                if (isInIran && !isVpnOn) {
                    listOf(AdServiceType.TAPSELL, AdServiceType.YANDEX, AdServiceType.ADMOB)
                } else {
                    listOf(AdServiceType.ADMOB, AdServiceType.YANDEX, AdServiceType.TAPSELL)
                }
            }
        }
    }
}