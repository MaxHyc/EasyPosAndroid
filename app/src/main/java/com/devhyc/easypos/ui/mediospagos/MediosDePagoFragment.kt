package com.devhyc.easypos.ui.mediospagos

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.databinding.FragmentMediosDePagoBinding
import com.devhyc.easypos.fiserv.device.DeviceApi
import com.devhyc.easypos.fiserv.device.DeviceService
import com.devhyc.easypos.fiserv.device.IDeviceService
import com.devhyc.easypos.fiserv.presenter.TransactionLauncherPresenter
import com.devhyc.easypos.fiserv.presenter.TransactionPresenter
import com.devhyc.easypos.fiserv.service.TransactionServiceImpl
import com.devhyc.easypos.ui.mediospagos.adapter.*
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.DatePickerFragment
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ingenico.fiservitdapi.transaction.Transaction
import com.ingenico.fiservitdapi.transaction.constants.TransactionTypes
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class MediosDePagoFragment : Fragment() {

    //
    private var _binding: FragmentMediosDePagoBinding? = null
    private val binding get() = _binding!!
    //ViewModel
    private lateinit var mediosViewModels: MediosDePagoViewModel
    //
    private var _medioPagoSelect:Int =0
    private var _monedaSelect:Int = 0
    private var _bancoSelect:Int = 0
    private var _financieraSelect:Int = 0
    //
    private lateinit var adapterBancos: customSpinnerBancos
    private lateinit var adapterFinancieras: customSpinnerFinancieras
    //
    private lateinit var adapterMediosDePagos: customSpinnerMediosDePago
    private lateinit var adapterMonedas: customSpinnerMonedas
    //
    private lateinit var adapterPagosRealizados: ItemMedioPago
    //
    var totalPago:Double = 0.0
    var totalDoc:Double = 0.0
    //
    private lateinit var fechaVencimiento:String
    //
    var dialog: AlertDialog? = null


    override fun onDestroy() {
        super.onDestroy()
        //CUANDO SE CIERRA LA VENTANA, PONGO LOS PAGOS VACIOS
        Globales.DocumentoEnProceso.valorizado!!.pagos = ArrayList()
        Globales.transactionLauncherPresenter.onExit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mediosViewModels = ViewModelProvider(this)[MediosDePagoViewModel::class.java]
        _binding = FragmentMediosDePagoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mediosViewModels.ListarMediosDePago()
        //
        //CARGAR CONFIGURACION DE CALENDARIOS
        fechaVencimiento = Globales.Herramientas.ObtenerFechaActual().FechayyyygMMgdd
        //CONFIGURACION DE BOTONES DE CALENDARIO
        binding.etFechaVtoPago.setOnClickListener {
            ShowDialogPickerFechaVto()
        }
        binding.btnFinalizarVenta.setOnClickListener {
            FinalizarVenta()
        }
        //
        if (Globales.TotalesDocumento != null)
        {
            binding.etMontoPago.setText(Globales.TotalesDocumento.total.toString())
            totalDoc = Globales.TotalesDocumento.total
            binding.lblTotal.text = totalDoc.toString()
        }
        //
        mediosViewModels.ColFinancieras.observe(viewLifecycleOwner, Observer {
            adapterFinancieras = customSpinnerFinancieras(requireContext(), ArrayList<DTFinanciera>(it))
            binding.spTarjeta.adapter = adapterFinancieras
        })
        mediosViewModels.ColBancos.observe(viewLifecycleOwner, Observer {
            adapterBancos = customSpinnerBancos(requireContext(), ArrayList<DTBanco>(it))
            binding.spBanco.adapter = adapterBancos
        })
        //
        mediosViewModels.LMedioPago.observe(viewLifecycleOwner, Observer {
            //Cargar Adaptador MEDIOS DE PAGOS
            adapterMediosDePagos = customSpinnerMediosDePago(requireContext(),ArrayList<DTMedioPago>(it))
            binding.spMediosDePago.adapter = adapterMediosDePagos
            //Evento seleccionar familia
            binding.spMediosDePago.onItemSelectedListener = object:
                AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //Seleccionado
                    binding.etFechaVtoPago.visibility = View.GONE
                    binding.etNumeroTarjeta.visibility = View.GONE
                    binding.spBanco.visibility = View.GONE
                    binding.spTarjeta.visibility = View.GONE
                    binding.etCuotas.visibility = View.GONE
                    binding.etAutorizacion.visibility = View.GONE
                    //
                    val mediop: DTMedioPago = adapterMediosDePagos.getItem(position)
                    _medioPagoSelect = position
                    when(mediop.Tipo)
                    {
                        Globales.TMedioPago.CHEQUE.codigo.toString() -> {
                            mediosViewModels.ListarBancos()
                            binding.etFechaVtoPago.visibility = View.VISIBLE
                            binding.etNumeroTarjeta.visibility = View.VISIBLE
                            binding.spBanco.visibility = View.VISIBLE
                        }
                        Globales.TMedioPago.TARJETA.codigo.toString() -> {
                            mediosViewModels.ListarFinancieras()
                            binding.etNumeroTarjeta.visibility = View.VISIBLE
                            binding.spTarjeta.visibility = View.VISIBLE
                            binding.etCuotas.visibility = View.VISIBLE
                            binding.etAutorizacion.visibility = View.VISIBLE
                            binding.etCuotas.setText("1")
                        }
                    }
                }
            }
            //
            //CARGAR MONEDAS DEL DOCUMENTO
            if (Globales.ParametrosDocumento != null)
            {
                //Cargar tipo de cambio
                binding.etTipoCambio.setText(Globales.ParametrosDocumento.Configuraciones.TipoCambio.toString())
                binding.etTipoCambio.isEnabled = Globales.ParametrosDocumento.Modificadores.ModificaTipoCambio
                //Cargar Adaptador MONEDAS
                adapterMonedas = customSpinnerMonedas(requireContext(),ArrayList<DTMoneda>(Globales.ParametrosDocumento.Configuraciones.Monedas))
                binding.spMonedas.adapter = adapterMonedas
                //Evento seleccionar MONEDA
                binding.spMonedas.onItemSelectedListener = object:
                    AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        //Seleccionado
                        val moneda: DTMoneda = adapterMonedas.getItem(position)
                        _monedaSelect = position
                    }
                }
            }
            //INICIALIZO ADAPTER MEDIOS DE PAGOS
            adapterPagosRealizados = ItemMedioPago(ArrayList<DTDocPago>(),adapterMonedas.monedas,adapterMediosDePagos.mediosDePago)
            adapterPagosRealizados.setOnItemClickListener(object: ItemMedioPago.onItemClickListener {
                override fun onItemClick(position: Int) {

                }
            })
            binding.rvMediosDePago.layoutManager = LinearLayoutManager(activity)
            binding.rvMediosDePago.adapter = adapterPagosRealizados
            //
            //SI TIENE PAGOS YA ASOCIADOS LOS CARGO A LA LISTA
            if (Globales.DocumentoEnProceso.valorizado!!.pagos.isNotEmpty())
            {
                //AGREGAR MEDIO DE PAGO
                adapterPagosRealizados = ItemMedioPago(ArrayList<DTDocPago>(Globales.DocumentoEnProceso.valorizado!!.pagos),adapterMonedas.monedas,adapterMediosDePagos.mediosDePago)
                adapterPagosRealizados.setOnItemClickListener(object: ItemMedioPago.onItemClickListener {
                    override fun onItemClick(position: Int) {

                    }
                })
                binding.rvMediosDePago.layoutManager = LinearLayoutManager(activity)
                binding.rvMediosDePago.adapter = adapterPagosRealizados
                adapterPagosRealizados.notifyDataSetChanged()
                Recalcular()
            }
            //
        })

        //
        binding.flAddMedioPago.setOnClickListener {
            AgregarMedio()
        }
        //
        //Evento deslizar del recycler items (EliminarItem)
        val itemFinalTouchHelper = object: ItemMedioPagoTouchHelper(requireContext())
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction)
                {
                    ItemTouchHelper.LEFT ->{
                        // TODO: Validar que si el medio de pago es tarjeta, no pueda borrar el item.
                        adapterPagosRealizados.mediosDepago.remove(adapterPagosRealizados.mediosDepago[viewHolder.adapterPosition])
                        adapterPagosRealizados.notifyDataSetChanged()
                        Recalcular()
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(itemFinalTouchHelper)
        touchHelper.attachToRecyclerView(binding.rvMediosDePago)

        //SALIR DEL DOCUMENTO
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DialogoSalir()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        mediosViewModels.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.cardTarjeta.visibility = View.VISIBLE
            else
                binding.cardTarjeta.visibility = View.GONE
        })
        // FISERV INTEGRACION
        FiservInstance()
        mediosViewModels.llamarAppFiserv.observe(viewLifecycleOwner, Observer {
            Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionInputData(it))
        })
        mediosViewModels.mensajeDelServer.observe(viewLifecycleOwner, Observer {
            DialogoFiserv("Mensaje del Servidor",it,true,true)
        })
        mediosViewModels.mensajeErrorDelServer.observe(viewLifecycleOwner, Observer {
            AlertView.showServerError("Error devuelto por fiserv", it,requireContext())
        })
        mediosViewModels.TransaccionConsulta.observe(viewLifecycleOwner, Observer {
            //MUESTRA ESTADO DE LA TRANSACCIÓN
            if (!it!!.conError)
            {
                if (it.pago != null)
                {
                    it.pago!!.medioPagoCodigo = adapterMediosDePagos.getItem(_medioPagoSelect).Id.toInt()
                    it.pago!!.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                    adapterPagosRealizados.mediosDepago.add(it.pago)
                    adapterPagosRealizados.notifyDataSetChanged()
                    Recalcular()
                    //LLAMAR AL FINALIZAR LA VENTA
                    /*Snackbar.make(requireView(),"Transaccion finalizada: ${it.mensaje} | ${it.mensajePos}", Snackbar.LENGTH_SHORT)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setBackgroundTint(resources.getColor(R.color.colorGeocom))
                        .show()*/
                    FinalizarVenta()
                }
            }
            else
            {
                AlertView.showServerError(it!!.transaccionId, it!!.mensaje + " | " + it!!.mensajePos,requireContext())
            }
        })
        mediosViewModels.TransaccionCancelada.observe(viewLifecycleOwner, Observer {
            //SI SE CANCELA LA TRANSACCION
            Toast.makeText(requireContext(),it!!.transaccionId,Toast.LENGTH_SHORT).show()
            AlertView.showServerError(it!!.transaccionId, it!!.mensaje + " | " + it!!.mensajePos,requireContext())
        })
        //
        return root
    }

    fun FinalizarVenta()
    {
        runBlocking {
            Toast.makeText(requireContext(),"Finalizando venta",Toast.LENGTH_SHORT).show()
            Globales.DocumentoEnProceso.valorizado!!.pagos = adapterPagosRealizados.mediosDepago
            Globales.DocumentoEnProceso.complemento!!.codigoDeposito = Globales.Terminal.Deposito
            //VOY A EMITIR EL DOCUMENTO
            if (ValidarDocumentoApi())
            {
                EmitirDocumento()
            }
        }
    }

    fun AgregarMedio()
    {
        try {
            //
            if (totalDoc - totalPago == 0.0)
            {

            }
            //
            var pago = DTDocPago()
            pago.importe= binding.etMontoPago.text.toString().toDouble()
            pago.tipoCambio = binding.etTipoCambio.text.toString().toDouble()
            pago.medioPagoCodigo = adapterMediosDePagos.getItem(_medioPagoSelect).Id.toInt()
            pago.monedaCodigo = adapterMonedas.getItem(_monedaSelect).codigo
            when(adapterMediosDePagos.getItem(_medioPagoSelect).Tipo)
            {
                Globales.TMedioPago.CHEQUE.codigo.toString() -> {
                    //ADD PAGO CON CHEQUE
                    pago.bancoCodigo = adapterBancos.getItem(_bancoSelect).Codigo
                    pago.numero = binding.etNumeroTarjeta.text.toString()
                }
                Globales.TMedioPago.TARJETA.codigo.toString() -> {
                    //ADD PAGO CON TARJETA
                    // TODO: CONTROLAR QUE ESTE HABILITADO EL MEDIO DE PAGO FISERV
                    mediosViewModels.CrearTransaccionITD()
                    //ME VOY DE LA FUNCION
                    return
                }
            }
            pago.fecha = Globales.DocumentoEnProceso.cabezal!!.fecha
            if (fechaVencimiento.isNotEmpty())
            {
                //pago.fechaVto = Globales.Herramientas.convertirYYYYMMDD(Globales.Herramientas.convertirYYYYMMDD(fechaVencimiento))
                pago.fechaVto = fechaVencimiento
            }
            adapterPagosRealizados.mediosDepago.add(pago)
            adapterPagosRealizados.notifyDataSetChanged()
            Recalcular()
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al agregar medio!","${e.message}",requireContext())
        }
    }

    fun Aceptar()
    {
        try {
            if (adapterPagosRealizados.mediosDepago.count() > 0)
            {
                Globales.DocumentoEnProceso.valorizado!!.pagos = adapterPagosRealizados.mediosDepago
                Snackbar.make(requireView(),"Medios de pago agregados correctamente", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(resources.getColor(R.color.green))
                    .show()
                findNavController().popBackStack()
            }
            else
            {
                AlertView.showAlert("No ha seleccionado ningún medio de pago","Ingrese un pago para poder finalizar",requireContext())
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al aceptar medio!","${e.message}",requireContext())
        }
    }

    fun Recalcular()
    {
        try {
            totalPago = 0.0
            adapterPagosRealizados.mediosDepago.forEach {
                if (it.monedaCodigo == Globales.DocumentoEnProceso.valorizado!!.monedaCodigo)
                {
                    totalPago += it.importe
                }
                else
                {
                    if (it.monedaCodigo == "1")
                        totalPago += it.importe / it.tipoCambio
                    else
                        totalPago += it.importe * it.tipoCambio
                }
            }
            binding.lblPago.text = totalPago.toString()
            binding.lblSaldo.text = (totalDoc - totalPago).toString()
            if (totalDoc - totalPago <= 0.0)
                binding.cardAddMedio.visibility = View.GONE
            else
                binding.cardAddMedio.visibility = View.VISIBLE
            binding.etMontoPago.setText((totalDoc - totalPago).toString())
            if ((totalPago - totalDoc) > 0)
            {
                //MOSTRAR CAMBIO
                binding.lblCambio.isVisible = true
                binding.tvVistaCambio.isVisible = true
                binding.tvVistaCambioMoneda.isVisible = true
                binding.lblCambio.text = (totalPago - totalDoc).toString()
                //
                binding.lblSaldo.isVisible = false
                binding.tvVistaSaldo.isVisible = false
                binding.tvVistaSaldoMoneda.isVisible = false
            }
            else
            {
                //NO MOSTRAR CAMBIO
                binding.lblCambio.isVisible = false
                binding.tvVistaCambio.isVisible = false
                binding.tvVistaCambioMoneda.isVisible = false
                binding.lblCambio.isVisible = false
                //
                binding.lblSaldo.isVisible = true
                binding.tvVistaSaldo.isVisible = true
                binding.tvVistaSaldoMoneda.isVisible = true
            }
            binding.btnFinalizarVenta.isVisible = totalDoc == totalPago || totalPago > totalDoc
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al recalcular!","${e.message}",requireContext())
        }
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
        binding.etFechaVtoPago.setText("$diafinal/$mesfinal/$year")
    }

    private fun DialogoSalir()
    {
        if (adapterPagosRealizados.mediosDepago.isNotEmpty())
        {
            AlertView.showError("¡Atención!","Ya existe un pago asociado, no puede cancelar. Finalice la venta.",binding.root.context)
        }
        else
        {
            view?.findNavController()?.navigateUp()
        }
    }

    suspend fun ValidarDocumentoApi(): Boolean
    {
        try {
            val result = mediosViewModels.postValidarDocumento(Globales.DocumentoEnProceso)
            return if (result.ok) {
                true
            } else {
                AlertView.showServerError("¡Error al validar el documento!","${result.mensaje}",requireContext())
                //PantallaDeCarga(false)
                false
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al guardar el documento!","${e.message}",requireContext())
            //PantallaDeCarga(false)
            return false
        }
    }

    suspend fun EmitirDocumento()
    {
        try {
            //LLAMO AL EMITIR DOCUMENTO
            var trans = mediosViewModels.postEmitirDocumento(Globales.DocumentoEnProceso)
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
                        trans = mediosViewModels.getConsultarTransaccion(nroTrans)!!
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
                            AlertView.showAlert("¡Error al obtener transacción!",
                                trans.elemento!!.errorMensaje,requireContext())
                        }
                    }
                    else
                    {
                        AlertView.showAlert("¡Error al obtener transacción!",
                            trans.mensaje,requireContext())
                    }
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al emitir el documento!","${e.message}",requireContext())
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

    private fun createTransactionInputData(monto:BigDecimal): TransactionInputData {
        return TransactionInputData(
            transactionType = TransactionTypes.SALE,
            monto,
            null,
            currency = convertToCurrencyType(Globales.currencySelected),
            null
            /*transactionType = TransactionTypes.SALE,
            amount = if (binding.etAmountEntry.text.isNotBlank()) {
                BigDecimal(binding.etAmountEntry.text.toString())
            } else {
                BigDecimal(0)
            },
            currency = convertToCurrencyType(currencySelected),
            invoiceId = if (binding.etInvoiceId.text.isNotBlank()) {
                binding.etInvoiceId.text.toString().toInt()
            } else {
                null
            },
            acquirerId = if (binding.etAcquirerId.text.isNotBlank()) {
                binding.etAcquirerId.text.toString().toInt()
            } else {
                null
            }*/
        )
    }

    fun showErrorMessage(message: String) {
        DialogoFiserv("Mensaje de Error de Fiserv","$message: ${binding.etMontoPago.text.toString().replace(".","").replace(",","")}")
    }

    fun showTransactionResult(amount: String?, result: String?, code: String?) {
        Toast.makeText(requireContext(),"Retomando control EasyPOS. Aguarde unos instantes",Toast.LENGTH_SHORT).show()
        //CUANDO VUELVE AL EASY POS, CONSULTO EL ESTADO DE LA TRANSACCION
        mediosViewModels.ConsultarTransaccionITD(Globales.IDTransaccionActual)
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

    private fun DialogoFiserv(titulo:String,mensaje:String,cerrar:Boolean = false,dejarCerrar:Boolean=false)
    {
        if (dialog == null)
        {
            dialog = AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setIcon(R.drawable.ic_baseline_payment_24)
                .setMessage(mensaje)
                /*.setPositiveButton("Cerrar") { _, _ ->
                    dialog?.dismiss()
                }*/
                .setCancelable(dejarCerrar)
                .create()
        }
        dialog?.show()
        //Cambiar Texto
        if (titulo.isNotBlank())
            dialog?.setTitle(titulo)
        if (titulo.isNotBlank())
            dialog?.setMessage(mensaje)
        if (cerrar)
            dialog?.dismiss()
        if (dejarCerrar)
            dialog?.setCancelable(dejarCerrar)
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
