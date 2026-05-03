package com.naturrre.core

import android.util.Log

private val TAG = "shaban"

// تابع کمکی برای مدیریت لاگ‌ها (فقط در حالت دیباگ چاپ می‌کند)
  fun log(message: String, type: Int = Log.DEBUG) {
    if (BuildConfig.DEBUG) {
        Log.e(TAG, message)
    }
}