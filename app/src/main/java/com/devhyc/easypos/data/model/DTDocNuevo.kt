package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

class DTDocNuevo (@SerializedName("documento") var documento:DTDoc,
                  @SerializedName("parametros") var parametros:DTDocParametros
                  ) {
}