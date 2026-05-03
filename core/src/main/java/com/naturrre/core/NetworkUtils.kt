package com.naturrre.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtils {

    fun isVpnActive(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            
            // چک کردن اینکه آیا شبکه فعال از نوع VPN است یا خیر
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } else {
            // برای اندرویدهای قدیمی (زیر نسخه 6)
            val networks = connectivityManager.allNetworks
            networks.any { network ->
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
            }
        }
    }
}