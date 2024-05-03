package com.devhyc.easypos.mercadopago

import com.devhyc.easypos.mercadopago.model.*
import com.google.gson.Gson
import com.ingenico.fiservitdapi.transaction.Transaction.Companion.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime

class MpOrdenes(private val token:String,private val url:String) {

    suspend fun crearOrden(
        sucursalCodigo: String,
        cajaCodigo: String,
        referencia: String,
        monedaCodigo: String,
        monto: Double
    ): DtOrdenResultado? {
        return withContext(Dispatchers.IO) {
            try {
                // Ignorar el certificado
                System.setProperty("https.protocols", "TLSv1.2")
                System.setProperty("http.keepAlive", "false")

                val url = URL("$url" + "Ordenes")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", token)
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Cargar la orden
                val orden = DtOrden(sucursalCodigo, cajaCodigo, referencia, monedaCodigo, monto)

                // Cargar el objeto en el body
                val gson = Gson()
                val jsonOrden = gson.toJson(orden)
                val wr = OutputStreamWriter(connection.outputStream)
                wr.write(jsonOrden)
                wr.flush()

                // Consulta la respuesta
                val responseCode = connection.responseCode

                val ordenResult: DtOrdenResultado? = if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val json = reader.use(BufferedReader::readText)
                    gson.fromJson(json, DtOrdenResultado::class.java)
                } else {
                    val reader = BufferedReader(InputStreamReader(connection.errorStream))
                    val json = reader.use(BufferedReader::readText)
                    gson.fromJson(json, DtOrdenResultado::class.java)
                }

                if (ordenResult?.conError == true) {
                    throw Exception(ordenResult.mensaje)
                }

                ordenResult
            } catch (ex: Exception) {
                throw Exception("Ocurrió un error al crear orden MERCADOPAGO: \n${ex.message}")
            }
        }
    }

    suspend fun consultarOrden(nroOrden: Long): DtOrdenEstado {
        return withContext(Dispatchers.IO) {
            try {
                // Ignorar el certificado
                System.setProperty("https.protocols", "TLSv1.2")
                System.setProperty("http.keepAlive", "false")

                val url = URL("$url" + "Ordenes/" + nroOrden.toString())
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", token)

                // Consulta la respuesta
                var ordenResult: DtOrdenEstado?

                try {
                    val res = connection.responseCode
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val json = reader.use(BufferedReader::readText)
                    ordenResult = Gson().fromJson(json, DtOrdenEstado::class.java)

                    if (ordenResult.conError) {
                        throw Exception(ordenResult.mensaje)
                    }
                } catch (e: Exception) {
                    val reader = BufferedReader(InputStreamReader(connection.errorStream))
                    val json = reader.use(BufferedReader::readText)
                    ordenResult = Gson().fromJson(json, DtOrdenEstado::class.java)

                    if (ordenResult.conError) {
                        throw Exception(ordenResult.mensaje)
                    } else {
                        throw Exception(e.message)
                    }
                }

                ordenResult
            } catch (ex: Exception) {
                throw Exception("Ocurrió un error al consultar orden MERCADOPAGO:\n${ex.message}")
            }
        }
    }

    suspend fun cancelarOrden(nroOrden: Long) {
        return withContext(Dispatchers.IO) {
            try {
                // Ignorar el certificado
                System.setProperty("https.protocols", "TLSv1.2")
                System.setProperty("http.keepAlive", "false")

                val url = URL("$url" + "Ordenes/" + nroOrden.toString())
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "DELETE"
                connection.setRequestProperty("Authorization", token)
                // Consulta la respuesta
                try {
                    val res = connection.responseCode
                    res.toString()
                } catch (e: Exception) {
                    throw Exception(e.message)
                }
            } catch (ex: Exception) {
                throw Exception(ex.message)
            }
        }
    }

    suspend fun reembolsoDeOrden(
        sucursalCodigo: String,
        cajaCodigo: String,
        referencia: String,
        monedaCodigo: String,
        monto: Double,
        nroOrden: String
    ): DtOrdenResultado {
        return withContext(Dispatchers.IO) {
            try {
                // Ignorar el certificado
                System.setProperty("https.protocols", "TLSv1.2")
                System.setProperty("http.keepAlive", "false")

                val url = URL("$url" + "Ordenes/transacciones/" + nroOrden)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", token)
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Cargar la orden
                val orden = DtOrden(
                    sucursalCodigo = sucursalCodigo,
                    numeroReferencia = referencia,
                    cajaCodigo = cajaCodigo,
                    monedaCodigo = monedaCodigo,
                    monto = monto
                )

                // Cargar el objeto en el body
                val jsonOrden = gson.toJson(orden)
                val wr = OutputStreamWriter(connection.outputStream)
                wr.write(jsonOrden)
                wr.flush()

                // Consulta la respuesta
                val responseCode = connection.responseCode

                val ordenResult: DtOrdenResultado = if (responseCode == HttpURLConnection.HTTP_OK) {
                    val json = connection.inputStream.bufferedReader().use { it.readText() }
                    gson.fromJson(json, DtOrdenResultado::class.java)
                } else {
                    val json = connection.errorStream.bufferedReader().use { it.readText() }
                    gson.fromJson(json, DtOrdenResultado::class.java)
                }

                if (ordenResult.conError) {
                    throw Exception(ordenResult.mensaje)
                }

                ordenResult
            } catch (ex: Exception) {
                throw Exception(ex.message)
            }
        }
    }

    suspend fun reporteOrdenes(
        cajaCodigo: String,
        sucursalCodigo: String,
        fdesde: LocalDateTime,
        fhasta: LocalDateTime
    ): DtOrdenReporteRespuesta {
        return withContext(Dispatchers.IO) {
            try {
                // Ignorar el certificado
                System.setProperty("https.protocols", "TLSv1.2")
                System.setProperty("http.keepAlive", "false")

                val url = URL("$url" + "Ordenes/reporte")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", token)
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Cargar la orden
                val orden = DtOrdenConsulta(
                    cajaCodigo = cajaCodigo,
                    fechaDesde = fdesde,
                    fechaHasta = fhasta,
                    sucursalCodigo = sucursalCodigo
                )

                // Cargar el objeto en el body
                val jsonOrden = gson.toJson(orden)
                val wr = OutputStreamWriter(connection.outputStream)
                wr.write(jsonOrden)
                wr.flush()

                // Consulta la respuesta
                val responseCode = connection.responseCode

                val ordenResult: DtOrdenReporteRespuesta = if (responseCode == HttpURLConnection.HTTP_OK) {
                    val json = connection.inputStream.bufferedReader().use { it.readText() }
                    gson.fromJson(json, DtOrdenReporteRespuesta::class.java)
                } else {
                    val json = connection.errorStream.bufferedReader().use { it.readText() }
                    gson.fromJson(json, DtOrdenReporteRespuesta::class.java)
                }

                ordenResult
            } catch (ex: Exception) {
                throw Exception("Ocurrió un error al crear el reporte de MERCADOPAGO:\n${ex.message}")
            }
        }
    }

    suspend fun obtenerOrdenPendiente(caja: String, sucursal: String): DtTransaccion {
        return withContext(Dispatchers.IO) {
            try {
                // Ignorar el certificado
                System.setProperty("https.protocols", "TLSv1.2")
                System.setProperty("http.keepAlive", "false")

                val url = URL("$url" + "Ordenes/pendiente/$sucursal/$caja")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", token)

                // Consulta la respuesta
                val transResult: DtTransaccion = if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val json = connection.inputStream.bufferedReader().use { it.readText() }
                    gson.fromJson(json, DtTransaccion::class.java)
                } else {
                    val json = connection.errorStream.bufferedReader().use { it.readText() }
                    gson.fromJson(json, DtTransaccion::class.java)
                }

                transResult
            } catch (ex: Exception) {
                throw Exception("Ocurrió un error al obtener la orden pendiente de MERCADOPAGO:\n${ex.message}")
            }
        }
    }
}