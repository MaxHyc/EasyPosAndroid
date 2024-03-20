package com.devhyc.easypos.utilidades

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import com.devhyc.easypos.MainActivity
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTFecha
import java.math.BigDecimal
import java.math.RoundingMode
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

    fun showKeyboard(view: View?,cnt:Context) {
        view?.let {
            val imm =
                cnt.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun VistaDeDrawerLauout(fragment:FragmentActivity,ver:Boolean)
    {
        if(ver)
        {
            val drawerLayout = fragment.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
        else
        {
            val drawerLayout = fragment.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }
}