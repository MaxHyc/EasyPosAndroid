package com.devhyc.easypos.fiserv.device

import com.usdk.apiservice.aidl.UDeviceService
import com.usdk.apiservice.aidl.printer.UPrinter

interface IDeviceService {


    /**
     * Esta interfaz define métodos para obtener servicios de dispositivos, en esta demo la impresora.
     */

        /**
         * Inicializa el manager del dispositivo
         *
         * @return Resultado de la conexion.
         * @see ErrorCode para mas informacion
         */
        suspend fun connect(): ErrorCode

        /**
         * Release the device manager.
         *
         * @return Resultado de liberar el servicio cuando ya no se utiliza
         * @see ErrorCode para mas informacion
         */
        suspend fun release(): ErrorCode

        /**
         * Conseguir instancia de la impresora
         */
        fun getPrinter(): UPrinter?

        /**
         * Conseguir instancia del manager del dispositivo
         */
        fun getDeviceService(): UDeviceService?

    }


    /**
     * Esta clase enum define resultado conectar servicio de dispositivo.
     *
     * @property OK Función realizada con éxito.
     * @property ERROR_SERVICE_BIND Fallo en la vinculación del servicio SDK.
     * @property ERROR_SERVICE_BIND Fallo en la vinculación del servicio SDK.
     */
    enum class ErrorCode(val value: Int) {
        OK(0),
        ERROR_SERVICE_BIND(1),
        ERROR_SERVICE_UNBIND(2)
    }
