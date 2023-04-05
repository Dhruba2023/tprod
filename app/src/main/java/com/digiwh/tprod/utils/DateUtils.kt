package com.digiwh.tprod.utils

import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "dd MMMM yyyy"
const val DATE_TIME_FORMAT = "dd-MM-yyyy/HH:mm:ss"

object DateUtils{

    fun getDateString(date : Date, format : String): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(date)
    }

}