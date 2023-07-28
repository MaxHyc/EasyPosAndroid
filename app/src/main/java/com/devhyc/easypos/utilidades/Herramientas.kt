package com.devhyc.easypos.utilidades

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    @SuppressLint("SimpleDateFormat")
    fun convertirYYYYMMDD(date:String): String {
        val dateconverted = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
        return dateconverted.toString()
    }
}