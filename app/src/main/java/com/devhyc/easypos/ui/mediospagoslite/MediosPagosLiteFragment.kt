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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ingenico.fiservitdapi.transaction.Transaction
import com.ingenico.fiservitdapi.transaction.constants.TransactionTypes
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal

@AndroidEntryPoint
class MediosPagosLiteFragment : Fragment() {

    private var _binding: FragmentMediosPagosLiteBinding? = null
    private val binding get() = _binding!!
    //ViewModel
    private lateinit var mediopagoViewModels: MediosPagosLiteViewModel
    //
    private var _medioPagoSelect:Int =0
    private lateinit var adapterMediosDePagos: ItemTipoMedioPago
    //private lateinit var adapterPagosRealizados: ItemMedioPago
    private var pagos: ArrayList<DTDocPago> = ArrayList()
    var totalPago:Double = 0.0
    var totalDoc:Double = 0.0
    var dialog: AlertDialog? = null

    override fun onDestroy() {
        super.onDestroy()
        //CUANDO SE CIERRA LA VENTANA, PONGO LOS PAGOS VACIOS
        Globales.DocumentoEnProceso.valorizado!!.pagos = ArrayList()
        Globales.transactionLauncherPresenter.onExit()
        //RESTAURA COMPORTAMIENTO DEL DRAWERLAYOUT
        Globales.Herramientas.VistaDeDrawerLauout(requireActivity(),true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //DESABILITA EL COMPORTAMIENTO DEL DRAWERLAYOUT
        Globales.Herramientas.VistaDeDrawerLauout(requireActivity(),false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mediopagoViewModels = ViewModelProvider(this)[MediosPagosLiteViewModel::class.java]
        _binding = FragmentMediosPagosLiteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mediopagoViewModels.ListarMediosDePago()
        //ELIMINO EL BOTON DE IR ATRAS
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
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
                    PantallaDeCarga(true)
                    //CUANDO SELECCIONAS EL MEDIO DE PAGO
                    _medioPagoSelect = adapterMediosDePagos.mediosDepago[position].Id.toInt()
                    when(adapterMediosDePagos.mediosDepago[position].Tipo)
                    {
                        Globales.TMedioPago.TARJETA.codigo.toString() -> {
                            //ADD PAGO CON TARJETA
                            if (adapterMediosDePagos.mediosDepago[position].Proveedor == "GEOCOM")
                            {
                                mediopagoViewModels.CrearTransaccionITD(binding.etMontoTotal.text.toString().toDouble())
                                return
                            }
                        }
                        else -> {
                            //SI NO ES TARJETA
                            var pago = DTDocPago()
                            pago.importe= binding.etMontoTotal.text.toString().toDouble()
                            pago.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                            pago.medioPagoCodigo = _medioPagoSelect
                            pago.monedaCodigo = Globales.DocumentoEnProceso.valorizado!!.monedaCodigo
                            pago.fecha = Globales.DocumentoEnProceso.cabezal!!.fecha
                            pago.fechaVto = Globales.DocumentoEnProceso.cabezal!!.fecha
                            pagos.add(pago)
                            FinalizarVenta()
                        }
                    }
                }
            })
            binding.rvMediosDePago.layoutManager = LinearLayoutManager(activity)
            binding.rvMediosDePago.adapter = adapterMediosDePagos
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Seleccione entre estos ${adapterMediosDePagos.mediosDepago.count()} métodos de pago"
        })
        // FISERV INTEGRACION
        FiservInstance()
        mediopagoViewModels.llamarAppFiserv.observe(viewLifecycleOwner, Observer {
            Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionInputData(it))
        })
        mediopagoViewModels.mensajeDelServer.observe(viewLifecycleOwner, Observer {
            DialogoFiserv("Mensaje del Servidor",it)
        })
        mediopagoViewModels.mensajeErrorDelServer.observe(viewLifecycleOwner, Observer {
            DialogoFiserv("Error devuelto por FISERV",it,true)
        })
        mediopagoViewModels.TransaccionConsulta.observe(viewLifecycleOwner, Observer {
            //MUESTRA ESTADO DE LA TRANSACCIÓN
            PantallaDeCarga(true)
            if (!it!!.conError)
            {
                if (it.pago != null)
                {
                    it.pago!!.medioPagoCodigo = _medioPagoSelect
                    it.pago!!.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                    pagos.add(it.pago)
                    DialogoFiserv("Finalizando venta","",true,true)
                    if (totalPago == totalDoc)
                        FinalizarVenta()
                }
            }
            else
            {
                DialogoFiserv("Informe de transacción","${it.mensaje} | ${it.mensajePos} \n(Transac. ${it.transaccionId})",true)
            }
            PantallaDeCarga(false)
        })
        //
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


    fun FinalizarVenta()
    {
        runBlocking {
            Toast.makeText(requireContext(),"Finalizando venta", Toast.LENGTH_SHORT).show()
            Globales.DocumentoEnProceso.valorizado!!.pagos = pagos
            Globales.DocumentoEnProceso.complemento!!.codigoDeposito = Globales.Terminal.Deposito
            //VOY A EMITIR EL DOCUMENTO
            if (ValidarDocumentoApi())
            {
                EmitirDocumento()
            }
        }
    }

    fun PantallaDeCarga(valor:Boolean)
    {
        if (valor)
            binding.cardCargando.visibility = View.VISIBLE
        else
            binding.cardCargando.visibility = View.GONE
    }

    private fun DialogoSalir()
    {
        if (pagos.isNotEmpty())
        {
            AlertView.showError("¡Atención!","Ya existe un pago asociado, no puede volver atrás. Elimine el pago o finalice la venta.",binding.root.context)
        }
        else
        {
            view?.findNavController()?.navigateUp()
        }
    }

    suspend fun ValidarDocumentoApi(): Boolean
    {
        try {
            val result = mediopagoViewModels.postValidarDocumento(Globales.DocumentoEnProceso)
            return if (result.ok) {
                true
            } else {
                DialogoFiserv("¡Error al validar el documento!",result.mensaje,true)
                false
            }
        }
        catch (e:Exception)
        {
            DialogoFiserv("¡Error al guardar el documento!",e.message.toString(),true)
            return false
        }
    }

    suspend fun EmitirDocumento()
    {
        try {
            //LLAMO AL EMITIR DOCUMENTO
            var trans = mediopagoViewModels.postEmitirDocumento(Globales.DocumentoEnProceso)
            if (trans != null)
            {
                //CONSULTO EL ESTADO DE LA TRANSACCION POR ESE NRO DE TRANSACCION
                if (trans.elemento!!.nroTransaccion.isNotEmpty())
                {
                    var nroTrans = trans.elemento!!.nroTransaccion
                    var cont = 0
                    var tiempoEspera = trans.elemento!!.tiempoEsperaSeg
                    while (cont < tiempoEspera)
                    {
                        //CONSULTO LA TRANSACCION POR EL NUMERO
                        trans = mediopagoViewModels.getConsultarTransaccion(nroTrans)!!
                        if (trans.elemento!!.finalizada)
                        {
                            cont = tiempoEspera
                        }
                        else
                        {
                            cont += 1
                            Thread.sleep(1000)
                        }
                    }
                    if(trans.ok)
                    {
                        if (trans.elemento!!.errorCodigo == 0)
                        {
                            Snackbar.make(requireView(), trans.elemento!!.errorMensaje, Snackbar.LENGTH_SHORT)
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                .setBackgroundTint(resources.getColor(R.color.green))
                                .show()
                            //IMPRIMIR DOCUMENTO
                            when (Globales.ImpresionSeleccionada)
                            {
                                Globales.eTipoImpresora.FISERV.codigo -> Globales.ControladoraFiservPrint.Print(trans.elemento!!.Impresion.impresionTicket,requireContext())
                            }
                            Globales.isEmitido = true
                            findNavController().popBackStack()
                        }
                        else if(trans.elemento!!.errorCodigo != 0)
                        {
                            DialogoFiserv("¡Error al obtener transacción!",trans.elemento!!.errorMensaje,true)
                        }
                    }
                    else
                    {
                        DialogoFiserv("¡Error al obtener transacción!",trans.mensaje,true)
                    }
                }
            }
        }
        catch (e:Exception)
        {
            DialogoFiserv("¡Error al obtener transacción!",e.message.toString(),true)
        }
    }

    // INTEGRACIÓN FISERV /////////////////////////////////////////////////////////////////////////////////////////////

    fun FiservInstance()
    {
        try {
            Globales.transactionApi = Transaction(requireContext())
            Globales.transactionApi.connectService()
            val transactionService = TransactionServiceImpl( Globales.transactionApi)
            Globales.deviceService = DeviceService(requireContext().applicationContext as Application)
            Globales.deviceApi = DeviceApi()
            Globales.transactionLauncherPresenter = TransactionPresenter(
                this, transactionService, lifecycleScope,  Globales.deviceApi
            )
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atencion!","${e.message}",requireContext())
        }
    }

    private fun createTransactionInputData(monto: BigDecimal): TransactionInputData {
        return TransactionInputData(
            transactionType = TransactionTypes.SALE,
            monto,
            null,
            currency = convertToCurrencyType(Globales.currencySelected),
            null
        )
    }

    fun showErrorMessage(message: String) {
        DialogoFiserv("Mensaje de Error de Fiserv","$message: ${binding.etMontoTotal.text.toString().replace(".","").replace(",","")}",true)
    }

    fun showTransactionResult(amount: String?, result: String?, code: String?) {
        //CUANDO VUELVE AL EASY POS, CONSULTO EL ESTADO DE LA TRANSACCION
        try {
            PantallaDeCarga(true)
            //DialogoFiserv("Retomando control EasyPOS","Aguarde unos instantes")
            Toast.makeText(requireContext(),"Retomando control EasyPOS",Toast.LENGTH_SHORT).show()
            mediopagoViewModels.ConsultarTransaccionITD(Globales.IDTransaccionActual)
        }
        catch (e:Exception)
        {
            DialogoFiserv("Error",e.message.toString(),true)
        }
    }

    private fun convertToCurrencyType(currencyType: String): Int {
        return when (currencyType) {
            "USD" ->
                840
            "UYU" ->
                858
            else ->
                840
        }
    }

    private fun DialogoFiserv(titulo:String,mensaje:String,cerrar:Boolean = false, cierreAutomatico:Boolean=false)
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