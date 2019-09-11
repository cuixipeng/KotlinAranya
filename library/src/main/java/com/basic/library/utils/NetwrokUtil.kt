package com.basic.library.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import java.io.IOException
import java.net.NetworkInterface
import java.net.SocketException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * @author cuixipeng
 * @date 2019-08-21.
 * @description 网络工具类
 */
class NetwrokUtil {
    companion object {
        var NER_CNNT_BAIDU_OK = 1//networkavailable
        var NET_CNNT_BAIDU_TIMEOUT = 2//no networkavailable
        var NET_NOT_PREPARE = 3//net no ready
        var NET_ERROR = 4
        private var TIMEOUT = 3000
        /**
         * check nerworkavailable
         */
        @JvmStatic
        fun isNetWorkAvailable(context: Context): Boolean {
            val manager = context.applicationContext.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return !(null == info || !info.isAvailable)
        }

        /**
         * 得到ip地址
         */
        @JvmStatic
        fun getLocalIpAddress(): String {
            var ret = ""
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val enumIpAdress = en.nextElement().inetAddresses
                    while (enumIpAdress.hasMoreElements()) {
                        val netAddress = enumIpAdress.nextElement()
                        if (!netAddress.isLoopbackAddress) {
                            ret = netAddress.hostAddress.toString()
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }
            return ret
        }

        /**
         * ping "http://www.baidu.com"
         */
        private fun pingNetWork(): Boolean {
            var result = false
            var httpUrl: HttpsURLConnection? = null
            try {
                httpUrl = URL("http://www.baidu.com").openConnection() as HttpsURLConnection
                httpUrl.connectTimeout = TIMEOUT
                httpUrl.connect()
                result = true
            } catch (e: IOException) {
            } finally {
                if (null != httpUrl) {
                    httpUrl.disconnect()
                }
            }
            return result
        }

        /**
         * check 3G
         */
        @JvmStatic
        fun is3G(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            return activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_MOBILE
        }

        /**
         * is WiFi
         */
        @JvmStatic
        private fun isWifi(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            return activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI
        }

        /**
         * is 2g
         */
        fun is2g(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            return activeNetInfo != null && (activeNetInfo.subtype == TelephonyManager.NETWORK_TYPE_EDGE
                    || activeNetInfo.subtype == TelephonyManager.NETWORK_TYPE_GPRS
                    || activeNetInfo.subtype == TelephonyManager.NETWORK_TYPE_CDMA)
        }

        /**
         * is wife on
         */
        @JvmStatic
        fun isWifiEnabled(context: Context): Boolean {
            val mgrCon=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mgrTel=context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return mgrCon.activeNetworkInfo!=null&&mgrCon.activeNetworkInfo.state==NetworkInfo.State.CONNECTED||mgrTel.networkType==TelephonyManager.NETWORK_TYPE_UMTS
        }
    }

}