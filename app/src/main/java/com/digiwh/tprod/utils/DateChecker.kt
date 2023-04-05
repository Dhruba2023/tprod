package com.digiwh.tprod.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

fun interface DateChecker {
    suspend fun checkSystemDate(onSuccess: () -> Unit, onFailure: () -> Unit)
}

class DateCheckerImpl : DateChecker {

    override suspend fun checkSystemDate(onSuccess: () -> Unit, onFailure: () -> Unit) {

        val timeServerUrl = "https://www.google.com/"

        try {
            val url = URL(timeServerUrl)
            val connection = withContext(Dispatchers.IO) {
                url.openConnection()
            } as HttpURLConnection
            connection.connectTimeout = 10000 // set connection timeout to 10 seconds
            connection.readTimeout = 10000 // set read timeout to 10 seconds
            connection.requestMethod = "GET"

            try {
                connection.connect()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val dateHeader: String? = connection.getHeaderField("Date")

            val currentDate: Date =
                SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).parse(dateHeader)
            val systemDate: Date = Date()

            val timeDiffInMillis: Long = Math.abs(currentDate.time - systemDate.time)
            val maxTimeDiffInMillis: Long =
                1000 * 60 * 5 // set a maximum time difference of 5 minutes
            if (timeDiffInMillis <= maxTimeDiffInMillis) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}