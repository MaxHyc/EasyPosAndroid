package com.devhyc.easypos.ui.mediopago

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.databinding.FragmentMedioPagoBinding
import com.devhyc.easypos.ui.mediopago.adapter.ItemMedioPago
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MedioPagoFragment : DialogFragment() {

    private var _binding: FragmentMedioPagoBinding? = null
    private val binding get() = _binding!!
    private lateinit var MedioPViewModel: MedioPagoFragmentViewModel
    //
    private lateinit var adapterMediosDePagos: ItemMedioPago
    //
    private lateinit var _pagoSeleccionado:DTMedioPago
    //
    private var TotalDeVenta:Double = 1200.0
    private var PagoDeVenta:Double = 0.0
    private var Cambio:Double = 0.0

    //
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MedioPViewModel = ViewModelProvider(this)[MedioPagoFragmentViewModel::class.java]
        _binding = FragmentMedioPagoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Selecciona pesos por defecto
        //binding.radioPesos.isChecked = true
        //Llamar al listar

        //Abrir teclado forzosamente
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        //

        binding.tvMedioPagoTotalVenta.text = TotalDeVenta.toString()
        binding.etMontoParaMpagos.setText(TotalDeVenta.toString())
        binding.etMontoParaMpagos.requestFocus()
        binding.etMontoParaMpagos.selectAll()
        //

        MedioPViewModel.ListarMediosDePago()
        MedioPViewModel.LMedioPago.observe(viewLifecycleOwner, Observer {
            //Cuando termina de cargar
            adapterMediosDePagos = ItemMedioPago(ArrayList<DTMedioPago>(it))
            adapterMediosDePagos.setOnItemClickListener(object: ItemMedioPago.onItemClickListener{
                override fun onItemClick(position: Int) {
                    //AL TOCAR UN MEDIO DE PAGO
                    for (i in adapterMediosDePagos.mediosDepago) {
                        i.seleccionado = false
                    }
                    //Mostrar la seleccionada
                    adapterMediosDePagos.mediosDepago[position].seleccionado = true
                    adapterMediosDePagos.notifyDataSetChanged()
                    //
                    _pagoSeleccionado = adapterMediosDePagos.mediosDepago[position]
                    //Toast.makeText(requireContext(),_pagoSeleccionado.Nombre,Toast.LENGTH_SHORT).show()
                }
            })
            //Selecciono el primer medio de pago
            adapterMediosDePagos.mediosDepago[0].seleccionado = true
            _pagoSeleccionado = adapterMediosDePagos.mediosDepago[0]
            //
            binding.rvMediosDePago.layoutManager = LinearLayoutManager(activity)
            binding.rvMediosDePago.adapter = adapterMediosDePagos
        })
        //Boton aceptar medio de pago
        binding.flAceptarMedio.setOnClickListener {
            AgregarMedio()
        }
        //Boton Finalizar la venta
        binding.flFinalizarVenta.setOnClickListener {
            //Toast.makeText(requireContext(),"Realizando venta",Toast.LENGTH_SHORT).show()

            Snackbar.make(requireView(),"Realizando venta",
                Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.black)).setAction("Ok",{}).show()

            if (Globales.ImpresionSeleccionada == Globales.eTipoImpresora.SUNMI.codigo)
            {
                Globales.ControladoraSunMi.ImprimirPaginaDePrueba(requireContext())
            }
            view?.findNavController()?.popBackStack()
        }
        //
        binding.etMontoParaMpagos.setOnClickListener {
            binding.etMontoParaMpagos.selectAll()
        }
        binding.etMontoParaMpagos.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() === KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                    AgregarMedio()
                    return true
                }
                return false
            }
        })
        return root
    }

    private fun AgregarMedio()
    {
        try {
            if (binding.etMontoParaMpagos.text.isNotEmpty())
            {
                PagoDeVenta = binding.etMontoParaMpagos.text.toString().toDouble()
                if (PagoDeVenta < TotalDeVenta)
                {
                    Snackbar.make(requireView(),"El total del pago no coincide con el total de la venta",
                        Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.red)).setAction("Ok",{}).show()
                }
                else
                {
                    //
                    binding.flAceptarMedio.isVisible = false
                    binding.flFinalizarVenta.isVisible = true
                    binding.cardCambio.isVisible = true
                    binding.cardPagos.isVisible = true
                    binding.rvMediosDePago.isVisible = false
                    binding.btnMultiPagos.isVisible = false
                    //
                    binding.tvMedioPagoSeleccionado.text = _pagoSeleccionado.Nombre
                    //
                    Cambio =PagoDeVenta - TotalDeVenta
                    binding.tvMedioPagoCambio.text= Cambio.toString()
                    if (PagoDeVenta >= TotalDeVenta)
                    {
                        binding.etMontoParaMpagos.isEnabled = false
                    }
                }
                //
            }
            else
            {
                Snackbar.make(requireView(),"Debe ingresar un monto para el pago",
                    Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.red)).setAction("Aceptar",{}).show()
            }
        }
        catch (e:Exception)
        {

        }
    }
}