package com.devhyc.easypos.ui.ingresoretiro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentIngresoRetiroBinding
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IngresoRetiroFragment : Fragment() {

    private var _binding: FragmentIngresoRetiroBinding? = null
    private val binding get() = _binding!!
    private var tipo:Int = 0

    //ViewModel
    private lateinit var ingresosViewModel: IngresoRetiroFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ingresosViewModel = ViewModelProvider(this)[IngresoRetiroFragmentViewModel::class.java]
        _binding = FragmentIngresoRetiroBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            tipo = bundle.getInt("TipoMovimiento",0)
            when(tipo)
            {
                Globales.TTipoMovimientoCaja.INGRESO.codigo -> (activity as? AppCompatActivity)?.supportActionBar?.title = "Ingreso de dinero"
                Globales.TTipoMovimientoCaja.RETIRO.codigo -> (activity as? AppCompatActivity)?.supportActionBar?.title = "Retiro de dinero"
                Globales.TTipoMovimientoCaja.INICIO.codigo -> {
                    (activity as? AppCompatActivity)?.supportActionBar?.title = "Inicio de caja"
                    binding.etObsIR.visibility = View.GONE
                }
            }
        }
        //
        binding.flIngresoOk.visibility = View.VISIBLE
        //
        binding.flIngresoOk.setOnClickListener {
            Cargando(true)
            if(binding.etMontoIR.text.toString() == "")
            {
                AlertView.showAlert(getString(R.string.Atencion),"El monto no puede ser vacío",requireContext())
            }
            else {
                //ME QUEDO CON LA MONEDA
                when(tipo)
                {
                    Globales.TTipoMovimientoCaja.INGRESO.codigo ->
                    {
                        //INGRESO DE DINERO
                        ingresosViewModel.RealizarMovimientoDeCaja(
                            if (binding.rmonedaPesos.isChecked) "1" else "2",
                            binding.etMontoIR.text.toString(),
                            binding.etObsIR.text.toString(),
                            Globales.TTipoMovimientoCaja.INGRESO.codigo)
                    }
                    Globales.TTipoMovimientoCaja.RETIRO.codigo ->
                    {
                        //RETIRO DE CAJA
                        ingresosViewModel.RealizarMovimientoDeCaja(
                            if (binding.rmonedaPesos.isChecked) "1" else "2",
                            binding.etMontoIR.text.toString(),
                            binding.etObsIR.text.toString(),
                            Globales.TTipoMovimientoCaja.RETIRO.codigo)
                    }
                    Globales.TTipoMovimientoCaja.INICIO.codigo ->
                    {
                        //INICIO DE CAJA
                        if (binding.etMontoIR.equals(""))
                        {
                            AlertView.showAlert("El campo está vacío","El monto debe ser distinto de vacío",requireActivity())
                        }
                        else
                        {
                            ingresosViewModel.IniciarCaja(binding.etMontoIR.text.toString())
                        }
                    }
                }
            }
            Cargando(false)
        }
        ingresosViewModel.TransaccionFinalizada.observe(viewLifecycleOwner, Observer {
            Snackbar.make(requireView(), it.errorMensaje, Snackbar.LENGTH_SHORT)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(resources.getColor(R.color.green))
                .show()
            //IMPRIMIR DOCUMENTO
            when (Globales.ImpresionSeleccionada)
            {
                Globales.eTipoImpresora.FISERV.codigo -> Globales.ControladoraFiservPrint.Print(it!!.Impresion.impresionTicket,requireContext())
            }
            findNavController().popBackStack()
        })
        ingresosViewModel.impresionInicio.observe(viewLifecycleOwner, Observer {
            when (Globales.ImpresionSeleccionada)
            {
                Globales.eTipoImpresora.FISERV.codigo -> Globales.ControladoraFiservPrint.Print(it,requireContext())
            }
            findNavController().popBackStack()
        })
        ingresosViewModel.mostrarErrorServer.observe(viewLifecycleOwner, Observer {
            AlertView.showError("El server retorno el siguiente error",it,requireContext())
        })
        ingresosViewModel.mostrarErrorLocal.observe(viewLifecycleOwner, Observer {
            AlertView.showError("Ocurrio el siguiente error",it,requireContext())
        })
        ingresosViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.progressBar2.isVisible = it
            Cargando(it)
        })
        binding.etMontoIR.selectAll()
        binding.etMontoIR.requestFocus()
        return root
    }

    fun Cargando(ver:Boolean)
    {
        binding.etMontoIR.isVisible = ver
        binding.etObsIR.isVisible = ver
        binding.radioMonedaMovimiento.isVisible = ver
        binding.flIngresoOk.isVisible = ver
        binding.lotcargandoc.isVisible = ver
    }
}