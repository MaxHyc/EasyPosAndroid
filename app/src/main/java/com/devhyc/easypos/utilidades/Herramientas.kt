package com.devhyc.easypos.utilidades

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

class Herramientas @Inject constructor() {
    fun convertirFechaHora(date: String): String {
        var targetDate: String = ""
        val targetFormat = "dd/MM/YYYY HH:mm:ss"
        val currentFormat = "yyyy-MM-dd'T'HH:mm:ss"
        val timezone = "CDT"
        val srcDf: DateFormat = SimpleDateFormat(currentFormat)
        srcDf.setTimeZone(TimeZone.getTimeZone(timezone))
        val destDf: DateFormat = SimpleDateFormat(targetFormat)
        try {
            val date: Date = srcDf.parse(date)
            targetDate = destDf.format(date)
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return targetDate
    }
}