package com.devhyc.easypos.mercadopago

enum class EstadoMercadoPago(val valor:Int) {
    Pendiente(0),
    Creando(1),
    Esperando(2),
    Pagando(3),
    Procesando(4),
    Finalizado(9)
}

enum class EstadoResultadoMercadoPago(val valor: Int) {
    SinRespuesta(0),
    Aprobado(1),
    Rechazado(2),
    Cancelado(3),
    Expirada(4),
    ConError(9)
}
