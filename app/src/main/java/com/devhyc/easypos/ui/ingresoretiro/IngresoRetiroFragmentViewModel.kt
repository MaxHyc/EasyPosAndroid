package com.devhyc.easypos.ui.ingresoretiro

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.data.model.DTIngresoCaja
import com.devhyc.easypos.data.model.DTTotalesDeclarados
import com.devhyc.easypos.domain.GetImpresionCierreCajaUseCase
import com.devhyc.easypos.domain.GetImpresionInicioCajaUseCase
import com.devhyc.easypos.domain.PostCerrarCaja
import com.devhyc.easypos.domain.PostIniciarCaja
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngresoRetiroFragmentViewModel @Inject constructor(val postIniciarCaja: PostIniciarCaja, var postCerrarCaja: PostCerrarCaja, val getImpresionInicioCajaUseCase: GetImpresionInicioCajaUseCase, val getImpresionCierreCajaUseCase: GetImpresionCierreCajaUseCase): ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
}