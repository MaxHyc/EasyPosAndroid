package com.devhyc.easypos.ui.mediospagoslite

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.databinding.FragmentMediosPagosLiteBinding
import com.devhyc.easypos.fiserv.device.DeviceApi
import com.devhyc.easypos.fiserv.device.DeviceService
import com.devhyc.easypos.fiserv.presenter.TransactionPresenter
import com.devhyc.easypos.fiserv.service.TransactionServiceImpl
import com.devhyc.easypos.ui.mediospagos.adapter.*
import com.devhyc.easypos.ui.mediospagoslite.adapter.ItemTipoMedioPago
import com.devhyc.easypos.ui.menuprincipal.MenuPrincipalFragmentDirections
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.easypos.utilidades.SingleLiveEvent
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ingenico.fiservitdapi.transaction.Transaction
import com.ingenico.fiservitdapi.transaction.constants.TransactionTypes
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import kotlinx.coroutines.*
import kotlin.concurrent.thread

@AndroidEntryPoint
class MediosPagosLiteFragment : Fragment() {

    private var _binding: FragmentMediosPagosLiteBinding? = null
    private val binding get() = _binding!!
    //ViewModel
    private lateinit var mediopagoViewModels: MediosPagosLiteViewModel
    private lateinit var adapterMediosDePagos: ItemTipoMedioPago
    var totalDoc:Double = 0.0
    var dialog: AlertDialog? = null
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        mediopagoViewModels = ViewModelProvider(this)[MediosPagosLiteViewModel::class.java]
        //ELIMINO EL BOTON DE IR ATRAS
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        //DESABILITA EL COMPORTAMIENTO DEL DRAWERLAYOUT
        Globales.Herramientas.VistaDeDrawerLauout(requireActivity(),false)
        // FISERV INTEGRACION
        FiservInstance()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //CUANDO SE CIERRA LA VENTANA, PONGO LOS PAGOS VACIOS
        Globales.DocumentoEnProceso.valorizado!!.pagos = ArrayList()
        Globales.transactionLauncherPresenter.onExit()
        //RESTAURA COMPORTAMIENTO DEL DRAWERLAYOUT
        Globales.Herramientas.VistaDeDrawerLauout(requireActivity(),true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediosPagosLiteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mediopagoViewModels.ListarMediosDePago()
        //CONFIGURACION DE BOTONES DE CALENDARIO
        if (Globales.TotalesDocumento != null)
        {
            binding.etMontoTotal.setText(Globales.TotalesDocumento.total.toString())
            totalDoc = Globales.TotalesDocumento.total
        }
        //
        mediopagoViewModels.LMedioPago.observe(viewLifecycleOwner, Observer {
            //Cargar Adaptador MEDIOS DE PAGO
            adapterMediosDePagos = ItemTipoMedioPago(ArrayList<DTMedioPago>(it))
            adapterMediosDePagos.setOnItemClickListener(object: ItemTipoMedioPago.onItemClickListener {
                override fun onItemClick(position: Int) {
                        AgregarMedioDePago(position)
                }
            })
            binding.rvMediosDePago.layoutManager = LinearLayoutManager(activity)
            binding.rvMediosDePago.adapter = adapterMediosDePagos
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Seleccione entre estos ${adapterMediosDePagos.mediosDepago.count()} métodos de pago"
        })
        mediopagoViewModels.TransaccionFinalizada.observe(viewLifecycleOwner, Observer {
            Snackbar.make(requireView(), it.errorMensaje, Snackbar.LENGTH_SHORT)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(resources.getColor(R.color.green))
                .show()
            //IMPRIMIR DOCUMENTO
            when (Globales.ImpresionSeleccionada)
            {
                Globales.eTipoImpresora.FISERV.codigo -> Globales.ControladoraFiservPrint.Print(it.Impresion.impresionTicket,requireContext())
            }
            findNavController().popBackStack()
        })
        mediopagoViewModels.isLoading.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            PantallaDeCarga(it)
        })
        //VIEWMODELS
        mediopagoViewModels.mostrarEstado.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            binding.tvMensajeAccion.text = it
        })
        mediopagoViewModels.mostrarErrorLocal.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            DialogoPersonalizado("Ocurrió el siguiente error",it,true)
        })
        mediopagoViewModels.mostrarErrorServer.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            DialogoPersonalizado("Error devuelto por FISERV",it,true)
        })
        mediopagoViewModels.mostrarInforme.observe(viewLifecycleOwner,
            SingleLiveEvent.EventObserver {
                AlertView.showAlert("Informe de transacción", it, requireContext())
            })
        //SALIR DEL MEDIO DE PAGO
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DialogoSalir()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        //
        return root
    }

    fun AgregarMedioDePago(position:Int)
    {
        try {
            PantallaDeCarga(true)
                    //CUANDO SELECCIONAS EL MEDIO DE PAGO
                    mediopagoViewModels.pagoseleccionado = adapterMediosDePagos.mediosDepago[position].Id.toInt()
                    when(adapterMediosDePagos.mediosDepago[position].Tipo)
                    {
                        Globales.TMedioPago.TARJETA.codigo.toString() -> {
                            //ADD PAGO CON TARJETA
                            if (adapterMediosDePagos.mediosDepago[position].Proveedor == "GEOCOM")
                            {
                                mediopagoViewModels.CrearTransaccionITD(binding.etMontoTotal.text.toString().toDouble())
                                //return
                            }
                        }
                        Globales.TMedioPago.MERCADOP.codigo.toString() -> {
                            //ADD PAGO CON MERCADOPAGO
                            //ABRO LA VENTANA DE MERCADOPAGO
                            val action = MediosPagosLiteFragmentDirections.actionMediosPagosLiteFragmentToMercadoPagoFragment()
                            view?.findNavController()?.navigate(action)
                            //ESCUCHO QUE DEVUELVE EL FRAGMENT DE MERCADOPAGO
                            parentFragmentManager.setFragmentResultListener("resultadoKey",this@MediosPagosLiteFragment)
                            { _, bundle ->
                                val resultadoPago = bundle.getParcelable<DTDocPago>("resultadoPago")
                                // RECIBO EL DTDOCPAGO DE LA VENTANA DE MERCADOPAGO
                                if (resultadoPago != null) {
                                    resultadoPago.importe= binding.etMontoTotal.text.toString().toDouble()
                                    resultadoPago.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                                    resultadoPago.medioPagoCodigo = mediopagoViewModels.pagoseleccionado
                                    resultadoPago.monedaCodigo = Globales.DocumentoEnProceso.valorizado!!.monedaCodigo
                                    resultadoPago.fecha = Globales.DocumentoEnProceso.cabezal!!.fecha
                                    resultadoPago.fechaVto = Globales.DocumentoEnProceso.cabezal!!.fecha
                                    mediopagoViewModels.pagos.add(resultadoPago)
                                    mediopagoViewModels.FinalizarVenta()
                                }
                                else
                                {
                                    AlertView.showAlert("¡Atención!","No se ha podido procesar el pago de Mercadopago, intentelo nuevamente",requireContext())
                                }
                            }
                            //
                        }
                        else -> {
                            //ADD PAGO CON OTRO MEDIO
                            var pago = DTDocPago()
                            pago.importe= binding.etMontoTotal.text.toString().toDouble()
                            pago.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                            pago.medioPagoCodigo = mediopagoViewModels.pagoseleccionado
                            pago.monedaCodigo = Globales.DocumentoEnProceso.valorizado!!.monedaCodigo
                            pago.fecha = Globales.DocumentoEnProceso.cabezal!!.fecha
                            pago.fechaVto = Globales.DocumentoEnProceso.cabezal!!.fecha
                            mediopagoViewModels.pagos.add(pago)
                            mediopagoViewModels.FinalizarVenta()
                        }
                    }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al agregar el medio de pago",e.message,requireContext())
        }
        finally {
            PantallaDeCarga(false)
        }
    }

    fun PantallaDeCarga(valor:Boolean)
    {
            if (valor)
            {
                binding.rvMediosDePago.visibility = View.GONE
                binding.cardCargando.visibility = View.VISIBLE
                binding.progressBar7.visibility = View.VISIBLE
            }
            else
            {
                binding.rvMediosDePago.visibility = View.VISIBLE
                binding.cardCargando.visibility = View.GONE
                binding.progressBar7.visibility = View.GONE
            }
    }

    private fun DialogoSalir()
    {
        if ( mediopagoViewModels.pagos.isNotEmpty())
        {
            AlertView.showError("¡Atención!","Ya existe un pago asociado, no puede volver atrás. Elimine el pago o finalice la venta.",binding.root.context)
        }
        else
        {
            view?.findNavController()?.navigateUp()
        }
    }

    // INTEGRACIÓN FISERV /////////////////////////////////////////////////////////////////////////////////////////////

    fun FiservInstance()
    {
        try {
            if (Globales.transactionApi == null)
            {
                Globales.transactionApi = Transaction(requireContext())
                Globales.transactionApi.connectService()
                val transactionService = TransactionServiceImpl(Globales.transactionApi)
                Globales.deviceService = DeviceService(requireContext().applicationContext as Application)
                Globales.deviceApi = DeviceApi()
                Globales.transactionLauncherPresenter = TransactionPresenter(
                    this, transactionService, lifecycleScope,  Globales.deviceApi
                )
            }
            else
            {
                if(!Globales.transactionApi.connected)
                {
                    Globales.transactionApi = Transaction(requireContext())
                    Globales.transactionApi.connectService()
                    val transactionService = TransactionServiceImpl(Globales.transactionApi)
                    Globales.deviceService = DeviceService(requireContext().applicationContext as Application)
                    Globales.deviceApi = DeviceApi()
                    Globales.transactionLauncherPresenter = TransactionPresenter(
                        this, transactionService, lifecycleScope,  Globales.deviceApi
                    )
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atencion!","${e.message}",requireContext())
        }
    }

    fun showErrorMessage(message: String) {
        DialogoPersonalizado("Mensaje de Error de Fiserv","$message: ${binding.etMontoTotal.text.toString().replace(".","").replace(",","")}",true)
    }

    var contador=0
    fun showTransactionResult(amount: String?, result: String?, code: String?) {
        //CUANDO VUELVE AL EASY POS, CONSULTO EL ESTADO DE LA TRANSACCION
        try {
            //VALIDO QUE NO SE EJECUTE DOS VECES
            if (contador==0)
                contador+=1
            else
            {
                contador=0
                return
            }
            Snackbar.make(requireView(),"Retomando control EasyPOS",Snackbar.LENGTH_SHORT)
                .setBackgroundTint(context!!.getColor(R.color.fiservcolor))
                .setTextColor(context!!.getColor(R.color.white))
                .show()
            mediopagoViewModels.ConsultarTransaccionITD(Globales.IDTransaccionActual)
        }
        catch (e:Exception)
        {
            DialogoPersonalizado("Error",e.message.toString(),true)
        }
    }

    private fun DialogoPersonalizado(titulo:String,mensaje:String,cerrar:Boolean = false, cierreAutomatico:Boolean=false)
    {
        if (dialog == null)
        {
            dialog = AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setIcon(R.drawable.ic_baseline_payment_24)
                .setMessage(mensaje)
                .setPositiveButton("Cerrar") { _, _ ->
                    dialog?.dismiss()
                }
                .setCancelable(false)
                .create()
            dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.visibility = View.GONE
        }
        dialog?.show()
        //Cambiar Texto
        if (titulo.isNotBlank())
            dialog?.setTitle(titulo)
        if (titulo.isNotBlank())
            dialog?.setMessage(mensaje)
        if (cierreAutomatico)
            dialog?.dismiss()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isVisible = cerrar
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}