package com.naturrre.core

import android.content.Context
import java.util.TimeZone

object DeviceUtils {
    fun isLikelyInIran(context: Context): Boolean {
        val tz = TimeZone.getDefault()
        val timeZoneId = tz.id

        // بررسی نام منطقه زمانی
        val isIranTZ = timeZoneId.contains("Tehran", ignoreCase = true) ||
                timeZoneId.contains("Iran", ignoreCase = true)

        // بررسی انحراف زمانی (GMT+3:30)
        val offsetInMinutes = tz.rawOffset / (1000 * 60)
        val isIranOffset = offsetInMinutes == 210

        return (isIranTZ || isIranOffset) && !NetworkUtils.isVpnActive(context = context)
    }
}