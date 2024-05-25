package com.devhyc.easypos.ui.tarjetamanual

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTBanco
import com.devhyc.easypos.data.model.DTDocPago
import com.devhyc.easypos.data.model.DTFinanciera
import com.devhyc.easypos.databinding.FragmentMediosDePagoBinding
import com.devhyc.easypos.databinding.FragmentTarjetaManualBinding
import com.devhyc.easypos.ui.mediospagos.MediosDePagoViewModel
import com.devhyc.easypos.ui.mediospagos.adapter.customSpinnerBancos
import com.devhyc.easypos.ui.mediospagos.adapter.customSpinnerFinancieras
import com.devhyc.easypos.ui.mediospagoslite.MediosPagosLiteFragmentDirections
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.DatePickerFragment
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TarjetaManualFragment : Fragment() {

    private var _binding: FragmentTarjetaManualBinding? = null
    private val binding get() = _binding!!
    //ViewModel
    private lateinit var tarjetaAuxViewModel: TarjetaManualFragmentViewModel
    private lateinit var adapterFinancieras: customSpinnerFinancieras
    private lateinit var adapterBancos: customSpinnerBancos
    //
    private lateinit var pago:DTDocPago
    //
    private var tipoMpago:String=""
    //
    private lateinit var fechaVencimiento:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tarjetaAuxViewModel = ViewModelProvider(this)[TarjetaManualFragmentViewModel::class.java]
        _binding = FragmentTarjetaManualBinding.inflate(inflater, container, false)
        val root: View = binding.root
        fechaVencimiento = Globales.Herramientas.ObtenerFechaActual().FechayyyygMMgdd
        binding.etFechaVtoCheque.setText(fechaVencimiento)
        //OBTENER ARGUMENTOS PASADOS POR PARAMETRO
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            tipoMpago = bundle.getString("tipomediopago","")
        }
        when(tipoMpago)
        {
            Globales.TMedioPago.TARJETA.codigo.toString() -> {
                (activity as? AppCompatActivity)?.supportActionBar?.title= "Tarjeta"
                (activity as? AppCompatActivity)?.supportActionBar?.subtitle= ""
                binding.tvTextoNro.text = "N° (Ultimos 4 dígitos)"
                binding.textoCuotas.text = "Cuotas"
                binding.spFinancierasTarjetaAux.visibility = View.VISIBLE
                binding.spBancoCheque.visibility = View.GONE
                binding.etFechaVtoCheque.visibility = View.GONE
                binding.etCuotasAux.visibility = View.VISIBLE
                tarjetaAuxViewModel.ListarFinancieras()
            }
            Globales.TMedioPago.CHEQUE.codigo.toString() -> {
                (activity as? AppCompatActivity)?.supportActionBar?.title= "Cheque"
                (activity as? AppCompatActivity)?.supportActionBar?.subtitle= ""
                binding.tvTextoNro.text = "N° Cuenta"
                binding.textoCuotas.text = "Fecha VTO"
                binding.spFinancierasTarjetaAux.visibility = View.GONE
                binding.etFechaVtoCheque.visibility = View.VISIBLE
                binding.etCuotasAux.visibility = View.GONE
                binding.spBancoCheque.visibility = View.VISIBLE
                tarjetaAuxViewModel.ListarBancos()
            }
        }
        binding.btnAceptarTarjetaAux.setOnClickListener {
            when(tipoMpago)
            {
                Globales.TMedioPago.TARJETA.codigo.toString() -> {
                    pago= DTDocPago()
                    pago.cuotas = binding.etCuotasAux.text.toString().toInt()
                    pago.autorizacion = binding.etAutorizacionAux.text.toString()
                    pago.numero = binding.etNroTarjetaAux.text.toString()
                    DevolverPago(pago)
                }
                Globales.TMedioPago.CHEQUE.codigo.toString() -> {
                    pago= DTDocPago()
                    pago.autorizacion = binding.etAutorizacionAux.text.toString()
                    pago.numero = binding.etNroTarjetaAux.text.toString()
                    if (fechaVencimiento.isNotEmpty())
                    {
                        pago.fechaVto = fechaVencimiento
                    }
                    DevolverPago(pago)
                }
            }
        }
        binding.btnCancelarTarjetaAux.setOnClickListener {
            DevolverPago(null)
        }
        //
        tarjetaAuxViewModel.isLoading.observe(viewLifecycleOwner, Observer {

        })
        tarjetaAuxViewModel.mensajeError.observe(viewLifecycleOwner, Observer {
            AlertView.showError("Ocurrió un error",it,requireContext())
        })
        tarjetaAuxViewModel.ColFinancieras.observe(viewLifecycleOwner, Observer {
            adapterFinancieras = customSpinnerFinancieras(requireContext(), ArrayList<DTFinanciera>(it))
            binding.spFinancierasTarjetaAux.adapter = adapterFinancieras
            binding.btnAceptarTarjetaAux.visibility = View.VISIBLE
            binding.btnCancelarTarjetaAux.visibility = View.VISIBLE
        })
        tarjetaAuxViewModel.ColBancos.observe(viewLifecycleOwner, Observer {
            adapterBancos = customSpinnerBancos(requireContext(), ArrayList<DTBanco>(it))
            binding.spBancoCheque.adapter = adapterBancos
            binding.btnAceptarTarjetaAux.visibility = View.VISIBLE
            binding.btnCancelarTarjetaAux.visibility = View.VISIBLE
        })
        binding.etFechaVtoCheque.setOnClickListener {
            ShowDialogPickerFechaVto()
        }
        //
        return root
    }

    fun ShowDialogPickerFechaVto()
    {
        try {
            val datePicker = DatePickerFragment {day,month,year -> onDateSelected(day,month,year)}
            datePicker.show(parentFragmentManager,"datePicker")
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atención!",e.message,binding.root.context)
        }
    }

    @SuppressLint("SetTextI18n")
    fun onDateSelected(day:Int, month:Int, year:Int)
    {
        var mesCorrecto = month + 1
        var m = mesCorrecto.toString()
        var mesfinal = m
        var diafinal = day.toString()
        if (m.length == 1)
        {
            mesfinal = "0$m"
        }
        if (day.toString().length ==1)
        {
            diafinal = "0$diafinal"
        }
        fechaVencimiento = "$year-$mesfinal-$diafinal"
        binding.etFechaVtoCheque.setText("$diafinal/$mesfinal/$year")
    }


    fun DevolverPago(pago:DTDocPago?)
    {
        try {
            val bundle = Bundle().apply {
                putParcelable("resultadoTarjetaAux",pago as? Parcelable)
            }
            parentFragmentManager.setFragmentResult("resultadoKey",bundle)
            findNavController().popBackStack()
        }
        catch (e:Exception)
        {
            AlertView.showError("Ocurrió un error al retornar los datos de la tarjeta",e.message,requireContext())
        }
    }
}