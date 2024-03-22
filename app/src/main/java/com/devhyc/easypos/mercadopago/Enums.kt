package com.devhyc.easypos.mercadopago

enum class EstadoMercadoPago {
    Pendiente,
    Creando,
    Esperando,
    Pagando,
    Procesando,
    Finalizado
}

enum class EstadoResultadoMercadoPago {
    SinRespuesta,
    Aprobado,
    Rechazado,
    Cancelado,
    Expirada,
    ConError
}
