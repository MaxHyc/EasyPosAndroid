package com.devhyc.easypos.ui.mediospagoslite

import android.app.Application
import android.os.Bundle
import android.view.*
import android.widget.Gallery
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.databinding.FragmentMediosPagosLiteBinding
import com.devhyc.easypos.fiserv.device.DeviceApi
import com.devhyc.easypos.fiserv.device.DeviceService
import com.devhyc.easypos.fiserv.presenter.TransactionPresenter
import com.devhyc.easypos.fiserv.service.TransactionServiceImpl
import com.devhyc.easypos.ui.mediospagoslite.adapter.ItemTipoMedioPago
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
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.Dispatcher
import java.util.*
import kotlin.collections.ArrayList
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
    var dialogDevolucion: AlertDialog? =null
    var esDevolucion:Boolean = false
    //

    override fun onResume() {
        //PUSE ESTO ACA PORQUE NECESITO QUE LO EJECUTE SIEMPRE QUE VUELVA A LA APP,
        //EN EL EVENTO DE FISER PASA QUE A VECES NO SE EJECUTA, NO SE PORQUE
        super.onResume()
        if (Globales.IDTransaccionActual.isNotEmpty())
        {
            Thread.sleep(500)
            mediopagoViewModels.ConsultarTransaccionITD(Globales.IDTransaccionActual,Globales.ProveedorActual,esDevolucion,)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediopagoViewModels = ViewModelProvider(this)[MediosPagosLiteViewModel::class.java]
        //
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            esDevolucion = bundle.getBoolean("esDevolucion",false)
        }
        if (esDevolucion)
        {
            //DEVOLUCION
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = "Devolución"
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.subtitle = "Seleccione el medio de pago con el que desea devolver"
        }
        else
        {
            //VENTA
            //ELIMINO EL BOTON DE IR ATRAS
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        //
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
        try {
            //CUANDO SE CIERRA LA VENTANA, PONGO LOS PAGOS VACIOS
            Globales.DocumentoEnProceso.valorizado!!.pagos = ArrayList()
            //RESTAURA COMPORTAMIENTO DEL DRAWERLAYOUT
            Globales.Herramientas.VistaDeDrawerLauout(requireActivity(),true)
            Globales.IDTransaccionActual = ""
            //Globales.transactionLauncherPresenter.onExit()
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       /* mediopagoViewModels.isLoading.observe(viewLifecycleOwner, Observer {
           *//* requireActivity().runOnUiThread {
                binding.cardCargando.isVisible = it
                binding.rvMediosDePago.isVisible = !it
            }*//*
            binding.cardCargando.isVisible = it
            binding.rvMediosDePago.isVisible = !it
        })*/
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
                    Globales.IDTransaccionActual = ""
                    mediopagoViewModels.AgregarMedio(adapterMediosDePagos.mediosDepago[position],binding.etMontoTotal.text.toString().toDouble(),esDevolucion)
                }
            })
            binding.rvMediosDePago.layoutManager = GridLayoutManager(activity,2)
            binding.rvMediosDePago.adapter = adapterMediosDePagos
            if (!esDevolucion)
                (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Seleccione entre estos ${adapterMediosDePagos.mediosDepago.count()} métodos de pago"
        })

        mediopagoViewModels.TransaccionFinalizada.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            try {
                Snackbar.make(requireView(), it.errorMensaje, Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(resources.getColor(R.color.green))
                    .show()
                //IMPRIMIR DOCUMENTO en hilo secundario
                when (Globales.ImpresionSeleccionada)
                {
                    Globales.eTipoImpresora.FISERV.codigo -> {
                        Globales.ControladoraFiservPrint.Print(it.Impresion.impresionTicket,requireActivity())
                    }
                }
                Globales.IDTransaccionActual = ""
                findNavController().popBackStack()
            }
            catch (e:Exception)
            {
                AlertView.showError("Error al finalizar la transaccion",e.message,requireContext())
            }
        })
        //
        mediopagoViewModels.compraMercadoPago.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            AbrirMercadoPago()
        })
        mediopagoViewModels.compraTarjetaAuxOCheque.observe(viewLifecycleOwner,SingleLiveEvent.EventObserver {
            AbrirTarjetaAux(it)
        })
        //VIEWMODELS
        mediopagoViewModels.mostrarEstado.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            binding.tvMensajeAccion.text = it
        })
        mediopagoViewModels.mostrarErrorLocal.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            DialogoPersonalizado("Ocurrió el siguiente error",it,true)
        })
        mediopagoViewModels.mostrarErrorServer.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            DialogoPersonalizado("¡Atención ser produjo un error!",it,true)
            //binding.tvMensajeServer.text = it
        })
        mediopagoViewModels.transaccionTarjeta.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            DialogoDevolucion(it.AplicaAnulacion,it.TransaccionId,it.Proveedor,it.MedioPagoId,it.TicketPos,it.Acquirer)
        })
        mediopagoViewModels.isLoading.observe(viewLifecycleOwner, Observer {
            binding.cardCargando.isVisible = it
            binding.rvMediosDePago.isVisible = !it
        })
        //SALIR DEL MEDIO DE PAGO
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DialogoSalir()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        //
        setHasOptionsMenu(true)
        //
        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.tvVerTransacciones ->
            {
                Globales.IDTransaccionActual = ""
                Globales.MediosPagoDocumento = ArrayList()
                adapterMediosDePagos.mediosDepago.forEach {
                    Globales.MediosPagoDocumento.add(it)
                }
                if (Globales.MediosPagoDocumento != null)
                {
                    val action = MediosPagosLiteFragmentDirections.actionMediosPagosLiteFragmentToTransaccionesITDFragment(true,esDevolucion)
                    view?.findNavController()?.navigate(action)
                    //ESCUCHO QUE DEVUELVE EL FRAGMENT DE TRANSACCIONES
                    parentFragmentManager.setFragmentResultListener("resultadoTransaccionKey",this@MediosPagosLiteFragment)
                    { _, bundle ->
                        val resultadoPago = bundle.getParcelable<DTDocPago>("resultadoPagoTransaccion")
                        // RECIBO EL DTDOCPAGO DE LA VENTANA DE TRANSACCIONES
                        if (resultadoPago != null) {
                            if (resultadoPago.importe == binding.etMontoTotal.text.toString().toDouble())
                            {
                                mediopagoViewModels.pagos.add(resultadoPago)
                                if (esDevolucion)
                                    mediopagoViewModels.DevolverVenta()
                                else
                                    mediopagoViewModels.FinalizarVenta()
                            }
                            else
                            {
                                AlertView.showAlert("¡Atención!","El total no conicide con el monto del pago: \r\n Monto del ticket: ${binding.etMontoTotal.text.toString()} \r\n Pago aprobado: ${resultadoPago.importe}",requireContext())
                            }
                        }
                        else
                        {
                            AlertView.showAlert("¡Atención!","No se ha podido procesar el pago de Fiserv, intentelo nuevamente",requireContext())
                        }
                    }
                }
                else
                {
                    AlertView.showAlert("¡Atención!","No hay seleccionado un medio de pago para FISERV",requireContext())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_mediosdepago,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun AbrirMercadoPago()
    {
        try {
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
                    if (esDevolucion)
                        mediopagoViewModels.DevolverVenta()
                    else
                        mediopagoViewModels.FinalizarVenta()
                }
                else
                {
                    AlertView.showAlert("¡Atención!","No se ha podido procesar el pago de Mercadopago, intentelo nuevamente",requireContext())
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Atención!","${e.message}",requireContext())
        }
    }

    fun AbrirTarjetaAux(tipoMedioPago:String)
    {
        try {
            //ADD PAGO CON TarjetaAuxiliar
            //ABRO LA VENTANA DE TARJETA AUXILIAR
            val action = MediosPagosLiteFragmentDirections.actionMediosPagosLiteFragmentToTarjetaManualFragment(tipoMedioPago)
            view?.findNavController()?.navigate(action)
            //ESCUCHO QUE DEVUELVE EL FRAGMENT DE TARJETA AUXILIAR
            parentFragmentManager.setFragmentResultListener("resultadoKey",this@MediosPagosLiteFragment)
            { _, bundle ->
                val resultadoPago = bundle.getParcelable<DTDocPago>("resultadoTarjetaAux")
                // RECIBO EL DTDOCPAGO DE LA VENTANA DE TARJETA AUX
                if (resultadoPago != null) {
                    resultadoPago.importe= binding.etMontoTotal.text.toString().toDouble()
                    resultadoPago.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                    resultadoPago.medioPagoCodigo = mediopagoViewModels.pagoseleccionado
                    resultadoPago.monedaCodigo = Globales.DocumentoEnProceso.valorizado!!.monedaCodigo
                    resultadoPago.fecha = Globales.DocumentoEnProceso.cabezal!!.fecha
                    if (resultadoPago.fechaVto.isNullOrEmpty())
                    {
                        resultadoPago.fechaVto = Globales.DocumentoEnProceso.cabezal!!.fecha
                    }
                    mediopagoViewModels.pagos.add(resultadoPago)
                    if (esDevolucion)
                        mediopagoViewModels.DevolverVenta()
                    else
                        mediopagoViewModels.FinalizarVenta()
                }
                else
                {
                    AlertView.showAlert("¡Atención!","No se ha podido procesar el pago de Tarjeta Aux, intentelo nuevamente",requireContext())
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Atención!","${e.message}",requireContext())
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

    private fun DialogoDevolucion(habilitaAnulacion:Boolean,nroTransaccion: String,proveedor:String,mediopagoid:Int, ticketPos:Int,acquirerId:Int)
    {
        if (dialogDevolucion == null)
        {
            dialogDevolucion = AlertDialog.Builder(requireContext())
                .setTitle("¿Que tipo de operación desea hacer?")
                .setIcon(R.drawable.ic_baseline_currency_exchange_24)
                .setMessage("Devolución (Cuando la venta a devolver es de otro cierre de lote)\r\n" +
                            "Anulación (Dentro del mismo cierre de lote)")
                .setPositiveButton("Anulación") { _, _ ->
                    mediopagoViewModels.CrearAnulacionITD(nroTransaccion,proveedor,mediopagoid,ticketPos, acquirerId)
                }
                .setNegativeButton("Devolución") { _, _ ->
                    mediopagoViewModels.CrearDevolucionITD(nroTransaccion,mediopagoid)
                }
                .setCancelable(true)
                .create()
            dialogDevolucion?.getButton(AlertDialog.BUTTON_POSITIVE)?.visibility = View.GONE
        }
        dialogDevolucion?.show()
        dialogDevolucion?.getButton(AlertDialog.BUTTON_POSITIVE)?.isVisible = habilitaAnulacion
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}