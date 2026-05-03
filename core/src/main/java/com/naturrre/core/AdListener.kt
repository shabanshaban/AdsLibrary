package com.naturrre.core

interface AdListener {
    fun onAdOpened()      // وقتی تبلیغ نمایش داده شد
    fun onAdClosed()      // وقتی کاربر تبلیغ را بست
    fun onAdClicked()     // وقتی روی تبلیغ کلیک شد
    fun onAdImpression()  // ثبت بازدید موفق
    fun onUserEarnedReward(amount: Int, type: String) // متد جدید برای جایزه
}