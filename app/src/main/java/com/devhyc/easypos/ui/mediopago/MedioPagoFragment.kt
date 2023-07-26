package com.devhyc.easypos.ui.mediopago

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.data.model.DTMedioPagoAceptado
import com.devhyc.easypos.databinding.FragmentMedioPagoBinding
import com.devhyc.easypos.ui.mediopago.adapter.ItemMedioPago
import com.devhyc.easypos.ui.pagoTarjeta.adapter.ItemPagoAceptadoAdapter
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.jamesmobile.ui.documento.adapter.ItemDocTouchHelper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedioPagoFragment : DialogFragment() {

    private var _binding: FragmentMedioPagoBinding? = null
    private val binding get() = _binding!!
    private lateinit var MedioPViewModel: MedioPagoFragmentViewModel
    //
    private lateinit var adapterMediosDePagos: ItemMedioPago
    private lateinit var adapterPagosAceptados: ItemPagoAceptadoAdapter
    //
    private lateinit var _pagoSeleccionado:DTMedioPago
    private lateinit var ListPagosAceptados:ArrayList<DTMedioPagoAceptado>
    //
    private var TotalDeVenta:Double = 1200.0
    private var PagoDeVenta:Double = 0.0
    private var Cambio:Double = 0.0

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
        ListPagosAceptados = ArrayList()

        //Abrir teclado forzosamente
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        //

        adapterPagosAceptados = ItemPagoAceptadoAdapter(ListPagosAceptados)
        adapterPagosAceptados.setOnItemClickListener(object: ItemPagoAceptadoAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {

            }
        })
        binding.rvPagosAcumulados.layoutManager = LinearLayoutManager(activity)
        binding.rvPagosAcumulados.adapter = adapterPagosAceptados

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
            //
            binding.flAceptarMedio.isEnabled = true
        })
        //Boton aceptar medio de pago
        binding.flAceptarMedio.setOnClickListener {
            if (_pagoSeleccionado != null)
            {
                //SI ES EFECTIVO
                if(_pagoSeleccionado.Tipo == "1")
                {
                    //EFECTIVO
                    AgregarMedio()
                }
                //SI ES TARJETA
                else if (_pagoSeleccionado.Tipo == "3")
                {
                    val action = MedioPagoFragmentDirections.actionMedioPagoFragmentToPagoTarjetaFragment()
                    view?.findNavController()?.navigate(action)
                   /* val cardType: Int =
                        AidlConstants.CardType.MAGNETIC.value or AidlConstants.CardType.NFC.value or
                                AidlConstants.CardType.IC.value
                    checkCard(cardType)*/
                }
            }
        }
        //Boton Finalizar la venta
        binding.flFinalizarVenta.setOnClickListener {
            //Toast.makeText(requireContext(),"Realizando venta",Toast.LENGTH_SHORT).show()

            Snackbar.make(requireView(),"Realizando venta",
                Snackbar.LENGTH_SHORT).setAction("Ok",{}).show()

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
        //Evento Swipe
        val itemDocTouchHelper = object: ItemDocTouchHelper(requireContext())
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction)
                {
                    ItemTouchHelper.LEFT ->{

                        var pago=adapterPagosAceptados.items[viewHolder.adapterPosition]

                        //SI ES EFECTIVO
                        if (pago.Tipo == "1")
                        {
                            ListPagosAceptados.remove(adapterPagosAceptados.items[viewHolder.adapterPosition])
                            Snackbar.make(requireView(),"Pago eliminado", Snackbar.LENGTH_SHORT)
                                .setTextColor(resources.getColor(R.color.white))
                                .setBackgroundTint(resources.getColor(R.color.red))
                                .setAction("Cerrar",{})
                                .show()
                            adapterPagosAceptados.notifyDataSetChanged()
                            EliminarMedio()
                        }
                        //SI ES TARJETA
                        else if (pago.Tipo == "3")
                        {
                            Snackbar.make(requireView(),"No puede eliminar un pago con tarjeta", Snackbar.LENGTH_SHORT)
                                .setTextColor(resources.getColor(R.color.white))
                                .setBackgroundTint(resources.getColor(R.color.red))
                                .setAction("Cerrar",{})
                                .show()
                            adapterPagosAceptados.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(itemDocTouchHelper)
        touchHelper.attachToRecyclerView(binding.rvPagosAcumulados)
        //
        setFragmentResultListener("tarjeta") { key, bundle ->
            val result = bundle.getBoolean("tarjeta")
            //FINALIZA LA VENTA POR TARJETA
            if (result) {
                ListPagosAceptados.add(Globales.PagoTarjetaAprobado)
                adapterPagosAceptados.notifyDataSetChanged()
                //Ocultar lista
                binding.rvMediosDePago.visibility = View.GONE
                binding.rvPagosAcumulados.visibility = View.VISIBLE
                //Ubicar boton Finalizar
                binding.etMontoParaMpagos.isEnabled = false
                binding.flFinalizarVenta.isVisible = true
                binding.flAceptarMedio.visibility = View.GONE
                //TOCO ACEPTAR AUTOMATICAMENTE
                binding.flFinalizarVenta.performClick()
            }
        }
        //
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
                    //Muestro el cambio
                    Cambio =PagoDeVenta - TotalDeVenta
                    if (Cambio > 0)
                    {
                        binding.tvMedioPagoCambio.text = Cambio.toString()
                        binding.cardCambio.isVisible = true
                    }
                    if (PagoDeVenta >= TotalDeVenta)
                    {
                        binding.etMontoParaMpagos.isEnabled = false
                    }
                    //
                    ListPagosAceptados.add(DTMedioPagoAceptado(_pagoSeleccionado.Nombre,_pagoSeleccionado.Tipo,PagoDeVenta,Cambio))
                    adapterPagosAceptados.notifyDataSetChanged()
                    //
                    binding.rvMediosDePago.isVisible = false
                    binding.flAceptarMedio.isVisible = false
                    binding.rvPagosAcumulados.isVisible = true
                    binding.flFinalizarVenta.isVisible = true
                    //
                   /* binding.flAceptarMedio.isVisible = false
                    binding.flFinalizarVenta.isVisible = true
                    binding.flCancelarMedio.isVisible = true
                    binding.cardCambio.isVisible = true
                    binding.cardPagos.isVisible = true
                    binding.rvMediosDePago.isVisible = false
                    //binding.btnMultiPagos.isVisible = false
                    //
                    binding.tvMedioPagoSeleccionado.text = _pagoSeleccionado.Nombre
                    //
                    Cambio =PagoDeVenta - TotalDeVenta
                    binding.tvMedioPagoCambio.text= Cambio.toString()
                    if (PagoDeVenta >= TotalDeVenta)
                    {
                        binding.etMontoParaMpagos.isEnabled = false
                    }*/
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

    fun EliminarMedio()
    {
        try {
            _pagoSeleccionado = adapterMediosDePagos.mediosDepago[0]
            binding.flFinalizarVenta.isVisible = false
            binding.cardCambio.isVisible = false
            //
            binding.etMontoParaMpagos.isEnabled = true
            binding.flAceptarMedio.isVisible = true
            //
            if (adapterPagosAceptados.items.isEmpty())
            {
                binding.rvMediosDePago.visibility = View.VISIBLE
                binding.rvPagosAcumulados.visibility = View.GONE
            }
            else
            {
                binding.rvMediosDePago.visibility = View.GONE
                binding.rvPagosAcumulados.visibility = View.VISIBLE
            }

        }
        catch (e:Exception)
        {

        }
    }
}