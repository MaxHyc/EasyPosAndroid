package com.devhyc.easypos.ui.cabezal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.Squareup.Country
import com.devhyc.easypos.domain.GetPaisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CabezalFragmentViewModel @Inject constructor(val getTodosLosPaises: GetPaisesUseCase) : ViewModel()  {
    val isLoading = MutableLiveData<Boolean>()
    var listadoPaises = MutableLiveData<List<Country>>()

    fun ObtenerPaises()
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = getTodosLosPaises()
            if (result!!.ok)
            {
               listadoPaises.postValue(result.elemento)
            }
            isLoading.postValue(false)
        }
    }
}