package com.devhyc.easypos.utilidades

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
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

    //YYYY-MM-DD
    fun fechaActual(): String
    {
        var fecha:String=""
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)+1
        val year = c.get(Calendar.YEAR)
        if (month.toString().length == 1)
        {
            if (day.toString().length == 1)
            {
                fecha = "$year-0$month-0$day"
            }
            else
            {
                fecha = "$year-0$month-$day"
            }
        }
        else
        {
            if (day.toString().length == 1)
            {
                fecha = "$year-0$month-0$day"
            }
            else
            {
                fecha = "$year-$month-$day"
            }
        }
        return fecha
    }
}