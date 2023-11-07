package com.devhyc.easypos.utilidades

import android.annotation.SuppressLint
import com.devhyc.easypos.data.model.DTFecha
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class Herramientas @Inject constructor() {
    @SuppressLint("SimpleDateFormat")
    fun convertirYYYYMMDD(date:String): String {
        val dateconverted = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
        return dateconverted.toString()
    }

    fun ObtenerFechaActual(): DTFecha
    {
        var fecha:DTFecha;
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH).toString()
        val month = (c.get(Calendar.MONTH)+1).toString()
        val year = c.get(Calendar.YEAR).toString()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val second = c.get(Calendar.SECOND)
        if (month.length == 1)
        {
            if (day.length == 1)
            {
                fecha = DTFecha(
                    year + "0" + month + "0" + day,
                    "0${day}/0$month/$year",
                    "$year-0$month-0$day",
                    year + "0" + month + "0" + day + hour + minute + second
                )
            }
            else
            {
                fecha = DTFecha(
                    year + "0" + month + day,
                    "${day}/0$month/$year",
                    "$year-0$month-$day",
                    year + "0" + month + day + hour + minute + second
                )
            }
        }
        else
        {
            if (day.length == 1)
            {
                fecha = DTFecha(
                    year + month + "0" + day,
                    "0${day}/$month/$year",
                    "$year-$month-0$day",
                    year + month + "0" + day + hour + minute + second
                )
            }
            else
            {
                fecha = DTFecha(
                    year + month + day,
                    "${day}/$month/$year",
                    "$year-$month-$day",
                    year + month + day + hour + minute + second
                )
            }
        }
        return fecha
    }

    fun TransformarFecha(fechaOriginal: String, formatoOriginal: String, formatoDestino: String): String {
        try {
            val sdfOriginal = SimpleDateFormat(formatoOriginal, Locale.getDefault())
            val fecha = sdfOriginal.parse(fechaOriginal)

            if (fecha != null) {
                val sdfDestino = SimpleDateFormat(formatoDestino, Locale.getDefault())
                return sdfDestino.format(fecha)
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
        return ""
    }
}