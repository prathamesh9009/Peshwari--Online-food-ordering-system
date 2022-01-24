package com.example.peshwari.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectivityManager {

    fun connectiityCheck(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val actieNetworkInfo : NetworkInfo? = connectivityManager.activeNetworkInfo
        if(actieNetworkInfo?.isConnected != null)
        {
            return actieNetworkInfo.isConnected
        }else
        {
            return false
        }
    }
}