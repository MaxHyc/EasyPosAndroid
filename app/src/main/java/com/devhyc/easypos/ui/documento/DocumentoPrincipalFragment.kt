package com.devhyc.easypos.ui.documento

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.databinding.FragmentDocumentoPrincipalBinding
import com.devhyc.easypos.ui.documento.adapter.ItemDocAdapter
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.easypos.utilidades.Globales.DocumentoEnProceso
import com.devhyc.easypos.utilidades.Globales.ParametrosDocumento
import com.devhyc.jamesmobile.ui.documento.adapter.ItemDocTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DocumentoPrincipalFragment : Fragment() {

    private lateinit var DocPrincipalViewModel: DocumentoPrincipalViewModel
    private lateinit var _binding: FragmentDocumentoPrincipalBinding
    private val binding get() = _binding!!
    lateinit var dialog: AlertDialog
    private lateinit var fechaEmision:String
    private lateinit var fechaEntrega:String
    private lateinit var adapterItems: ItemDocAdapter
    private lateinit var resultCli:Resultado<DTCliente>

    override fun onResume() {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        //CARGO LOS ITEMS
        if (DocumentoEnProceso.detalle != null)
        {
            runBlocking { CalcularDocumentoApi() }
        }
        //CARGO EL RECEPTOR
        if (DocumentoEnProceso.receptor != null)
        {
            binding.tvClienteNombre.visibility = View.VISIBLE
            binding.tvClienteNombre.text = DocumentoEnProceso.receptor!!.receptorRut
        }
        else
        {
            binding.tvClienteNombre.visibility = View.GONE
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle= ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DocPrincipalViewModel = ViewModelProvider(this)[DocumentoPrincipalViewModel::class.java]
        _binding = FragmentDocumentoPrincipalBinding.inflate(this.layoutInflater)
        /*if(Globales.CajaActual != null)
        {
            (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.menu_pdv)
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Caja ${Globales.NroCaja}"
        }
        else
        {
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.atencion)
                .setTitle("¡Atención!")
                .setMessage("Debe iniciar una caja para empezar a facturar.\nVaya a 'Movimientos de Caja' e inicie una caja.")
                .setPositiveButton("Entendido", DialogInterface.OnClickListener {
                        dialogInterface, i ->
                    run {
                        view?.findNavController()?.navigateUp()
                    }
                })
                .setCancelable(false)
                .show()
        }*/
        //OBTENGO LOS PARAMETROS DEL DOCUMENTO
        runBlocking {
            ObtenerParametrosDocumentoApi()
            binding.shimmerDocCargando.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = binding.root
        setHasOptionsMenu(true)
        //SI EXISTE UN DOCUMENTO
        if (DocumentoEnProceso != null)
        {
            MostrarInfoDocEnNavBarr()
        }
        //EVENTOS DE BOTON
        binding.flAddArtDoc.setOnClickListener {
            val action = DocumentoPrincipalFragmentDirections.actionDocumentoPrincipalFragmentToListaDeArticulosFragment()
            view?.findNavController()?.navigate(action)
        }
        //
        adapterItems = ItemDocAdapter(ArrayList<DTDocDetalle>())
        adapterItems.setOnItemClickListener(object: ItemDocAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {

            }
        })
        binding.rvItemsDoc.layoutManager = LinearLayoutManager(activity)
        binding.rvItemsDoc.adapter = adapterItems
        //Evento deslizar del recycler items (EliminarItem)
        val itemFinalTouchHelper = object: ItemDocTouchHelper(requireContext())
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction)
                {
                    ItemTouchHelper.LEFT ->{
                        //Elimino el item seleccionado
                        adapterItems.items.remove(adapterItems.items[viewHolder.adapterPosition])
                        adapterItems.notifyDataSetChanged()
                        if (Globales.ParametrosDocumento.Validaciones.Valorizado)
                        {
                            runBlocking { CalcularDocumentoApi() }
                        }
                    }
                }
            }
        }
        //SALIR DEL DOCUMENTO
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DialogoSalir()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        //
        val touchHelper = ItemTouchHelper(itemFinalTouchHelper)
        touchHelper.attachToRecyclerView(binding.rvItemsDoc)
        //
        return root
    }

    fun MostrarInfoDocEnNavBarr()
    {
        try {
            (activity as? AppCompatActivity)?.supportActionBar?.title = Globales.ParametrosDocumento.Descripcion
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "N° ${DocumentoEnProceso.cabezal!!.nroDoc}"
        }
        catch (e:Exception)
        {
            AlertView.showServerError("Error", "${e.message}", requireContext())
        }
    }

    suspend fun ObtenerParametrosDocumentoApi()
    {
        try {
            val result = DocPrincipalViewModel.getNuevoDocumentoUseCase(Globales.UsuarioLoggueado.usuario, Globales.Terminal.Codigo,"TCON")
            if (result!!.ok)
            {
                ParametrosDocumento = result.elemento!!.parametros
                DocumentoEnProceso = result.elemento!!.documento
                //
                MostrarInfoDocEnNavBarr()
                CargarConfiguracionDelDocumento()
            }
            else
            {
                AlertView.showServerError("Server Error", "${result.mensaje}", requireContext())
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al obtener los parametros del documento!","${e.message}",requireContext())
        }
    }

    fun CargarConfiguracionDelDocumento()
    {
        try {
            if (ParametrosDocumento != null)
            {
                DocumentoEnProceso!!.detalle = ArrayList<DTDocDetalle>()
                DocumentoEnProceso!!.complemento = DTDocComplemento()
                if (ParametrosDocumento.Validaciones.Valorizado)
                {
                    DocumentoEnProceso!!.valorizado = DTDocValorizado(ParametrosDocumento.Configuraciones.MonedaCodigo,ParametrosDocumento.Configuraciones.TipoCambio,0,ParametrosDocumento.Configuraciones.ListaPrecioCodigo,ArrayList())
                }
                else
                {
                    AlertView.showAlert("¡Atención!","El documento está configurado como no valorizado. Cámbielo para poder usarlo",requireContext())
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al cargar la configuracion del documento",e.message,requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_documento,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.btnAvanzarAMedioPago ->
            {

            }
            R.id.btnAddCliente ->
            {
                val action = DocumentoPrincipalFragmentDirections.actionDocumentoPrincipalFragmentToCabezalFragment()
                view?.findNavController()?.navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    suspend fun CalcularDocumentoApi()
    {
        try {
            val result = DocPrincipalViewModel.postCalcularDocumento(DocumentoEnProceso)
            if (result!!.ok)
            {
                Globales.TotalesDocumento = result.elemento!!
                CalcularDocumento()
            }
            else
            {
                AlertView.showServerError("Server error al calcular documento", result.mensaje,requireContext())
            }
        }
        catch (e:Exception)
        {
            //binding.progressBarDoc.visibility = View.GONE
            AlertView.showAlert("¡Error al calcular el documento!","${e.message}",requireContext())
        }
    }

    fun CalcularDocumento()
    {
        try {
            if (Globales.TotalesDocumento != null) {
                when (Globales.MonedaSeleccionada) {
                    0 -> binding.tvTotalMonedaDoc.text = "$"
                    1 -> binding.tvTotalMonedaDoc.text = "U$" + "S "
                }
                binding.tvTotalTotalDoc.text = Globales.TotalesDocumento.total.toString()
                //CARGAR ADAPTER
                if (Globales.TotalesDocumento.items.isNotEmpty())
                {
                    adapterItems.items = Globales.TotalesDocumento.items as ArrayList<DTDocDetalle>
                    DocumentoEnProceso.detalle = adapterItems.items
                    adapterItems.updateList(adapterItems.items)
                }
            } else {
                binding.tvTotalTotalDoc.text = "0"
            }
            //binding.tvCantidadItems.text = "Cantidad: ${adapterItems.itemCount}"
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al cargar calcular el documento",e.message,requireContext())
        }
    }

    private fun DialogoSalir()
    {
        dialog= AlertDialog.Builder(requireActivity())
            .setIcon(R.drawable.atencion)
            .setTitle("¡Atención!")
            .setMessage("¿Desea cancelar el documento actual?")
            .setPositiveButton("Si", DialogInterface.OnClickListener {
                    dialogInterface, i ->
                ReiniciarVariables()
                Globales.editando_documento = false
                Globales.DocumentoEnProceso = null
                view?.findNavController()?.navigateUp()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialogInterface, i -> dialog.dismiss()
            })
            .setCancelable(true)
            .setOnCancelListener { "Cancelar" }
            .show()
    }

    private fun ReiniciarVariables()
    {
        try
        {
            //Limpio variables
            Globales.DocumentoEnProceso = null
            //Borrar items del adapter
            adapterItems.items.clear()
            adapterItems.notifyDataSetChanged()
            //Cargar los parametros del documento
            runBlocking {
                ObtenerParametrosDocumentoApi()
                binding.shimmerDocCargando.visibility = View.GONE
            }
            //CargarCliente(null)
            //
            binding.tvTotalTotalDoc.text = "0"
            //
            fechaEntrega = ""
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle= ""
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al reiniciar variables",e.message,requireContext())
        }
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        fechaEntrega = ""
    }

    override fun onResume() {
        try
        {
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            (activity as? AppCompatActivity)?.supportActionBar?.title= Globales.ParametrosDocumento.Descripcion
            //CARGO LOS ITEMS
            if (Globales.DocumentoEnProceso.detalle != null)
            {
                    runBlocking { CalcularDocumentoApi() }
            }
            //CARGO EL RECEPTOR
            if (Globales.DocumentoEnProceso.receptor != null)
            {
                (activity as? AppCompatActivity)?.supportActionBar?.subtitle = Globales.DocumentoEnProceso.receptor!!.clienteNombre
                binding.tv.visibility = View.VISIBLE
                binding.flEliminarClienteDoc.visibility = View.VISIBLE
            }
            else
            {
                (activity as? AppCompatActivity)?.supportActionBar?.subtitle= ""
            }
        }
        catch (e:Exception) {
            findNavController().popBackStack()
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle= ""
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //VIEW BINDING
        DocPrincipalViewModel = ViewModelProvider(this)[DocumentoPrincipalViewModel::class.java]
        _binding = FragmentDocumentoPrincipalBinding.inflate(this.layoutInflater)
        //CARGO CONFIGURACIÓN DEL DOCUMENTO
        runBlocking {
            ObtenerParametrosDocumentoApi()
            binding.shimmerDocCargando.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = binding.root
        setHasOptionsMenu(true)
        //CARGO EL ADAPTER
        binding.rvItemsDoc.layoutManager = LinearLayoutManager(activity)
        binding.rvItemsDoc.adapter = adapterItems
        //CARGAR CONFIGURACION DE CALENDARIOS
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)+1
        val year = c.get(Calendar.YEAR)
        //
        //BOTONES ITEMS
        binding.flAddArtDoc.setOnClickListener {
            val action = DocumentoPrincipalFragmentDirections.actionDocumentoPrincipalFragmentToListaDeArticulosFragment()
            view?.findNavController()?.navigate(action)
        }
        //Evento deslizar del recycler items (EliminarItem)
        val itemFinalTouchHelper = object: ItemPrevTouchHelper(requireContext())
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction)
                {
                    ItemTouchHelper.LEFT ->{
                        //Elimino el item seleccionado
                        adapterItems.items.remove(adapterItems.items[viewHolder.adapterPosition])
                        adapterItems.notifyDataSetChanged()
                        binding.tvCantidadItems.text = "Cantidad: ${adapterItems.itemCount}"
                        if (Globales.ParametrosDocumento.Validaciones.Valorizado)
                        {
                            runBlocking { CalcularDocumentoApi() }
                        }
                    }
                }
            }
        }
        binding.etCabezalBuscarClienteDoc.setOnKeyListener(object: View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event!!.getAction() === KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                    //BUSCAR CLIENTE
                    runBlocking {
                        when (Globales.ParametrosDocumento.Configuraciones.ClienteAsociaTipo)
                        {
                            0 -> {
                                BuscarClienteApiConsult(binding.etCabezalBuscarClienteDoc.text.toString(),0)
                            }
                            1 -> {
                                BuscarProveedorApiConsult(binding.etCabezalBuscarClienteDoc.text.toString(),0)
                            }
                        }
                    }
                    return true
                }
                return false
            }
        })
        //SALIR DEL DOCUMENTO
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DialogoSalir()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        //
        val touchHelper = ItemTouchHelper(itemFinalTouchHelper)
        touchHelper.attachToRecyclerView(binding.rvItemsDoc)
        //
        if (month.toString().length == 1)
            fechaEmision = "$year-0$month-$day"
        else
            fechaEmision = "$year-$month-$day"
        return root
    }

    fun ShowDialogPickerFechaDoc()
    {
        try {
            val datePicker = DatePickerFragment {day,month,year -> onDateSelected(day,month,year) }
            datePicker.show(parentFragmentManager,"datePicker")
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atención!",e.message,binding.root.context)
        }
    }

    fun ShowDialogPickerFechaEntrega()
    {
        try {
            val datePicker = DatePickerFragment {day,month,year -> onDateSelectedEntrega(day,month,year) }
            datePicker.show(parentFragmentManager,"datePicker")
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atención!",e.message,binding.root.context)
        }
    }

    @SuppressLint("SetTextI18n")
    fun onDateSelectedEntrega(day:Int, month:Int, year:Int)
    {
        var mesCorrecto = month + 1
        var m = mesCorrecto.toString()
        var mesfinal = m
        if (m.length == 1)
        {
            mesfinal = "0$m"
        }
        fechaEntrega = "$year-$mesfinal-$day"
        binding.etFechaEntregaDoc.setText("$day/$mesfinal/$year")
    }


    @SuppressLint("SetTextI18n")
    fun onDateSelected(day:Int, month:Int, year:Int)
    {
        var mesCorrecto = month + 1
        var m = mesCorrecto.toString()
        var mesfinal = m
        if (m.length == 1)
        {
            mesfinal = "0$m"
        }
        fechaEmision = "$year-$mesfinal-$day"
        binding.etCabezalDocFecha.setText("$day/$mesfinal/$year")
    }

    fun CambiarColorBoton(seleccionado:Int)
    {
        when (seleccionado)
        {
            0 -> {
                //CAMBIAR COLOR A CABEZAL SELECCIONADO
                binding.btnCabezalDoc.setTextColor(context!!.getColor(R.color.white))
                binding.btnCabezalDoc.setBackgroundColor(context!!.getColor(R.color.green))
                binding.btnItemsDoc.setTextColor(context!!.getColor(R.color.black))
                binding.btnItemsDoc.setBackgroundColor(context!!.getColor(R.color.botones_documento))
            }
            1 -> {
                //CAMBIAR COLOR A ITEMS SELECCIONADO
                binding.btnCabezalDoc.setTextColor(context!!.getColor(R.color.black))
                binding.btnCabezalDoc.setBackgroundColor(context!!.getColor(R.color.botones_documento))
                binding.btnItemsDoc.setTextColor(context!!.getColor(R.color.white))
                binding.btnItemsDoc.setBackgroundColor(context!!.getColor(R.color.green))
            }
        }
    }

    fun MostarDialogoClientes()
    {
        try {
            val dialogoCliente = ListaClientesFragment {cliente -> onClienteSelected(cliente as DTCliente)}
            dialogoCliente.show(parentFragmentManager,"ListaCli")
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun onClienteSelected(cliente:DTCliente)
    {
        CargarCliente(cliente)
    }

    private fun CargarCliente(it: DTCliente?)
    {
        try {
            val vacio = 0
            if (it != null) {
                if (it.id != vacio.toLong()) {
                    //
                    Globales.DocumentoEnProceso.receptor = DTDocReceptor(false,it.id,it.codigo ,it.nombre,it.tipoDocumento,it.razonSocial,it.documento,it.direccion,it.ciudad,"","",it.telefono)
                    if (!it.listaPrecioCodigo.isNullOrEmpty())
                    {
                        Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo = it.listaPrecioCodigo
                    }
                    else
                    {
                        Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo = Globales.ParametrosDocumento.Configuraciones.ListaPrecioCodigo
                    }
                    binding.flEliminarClienteDoc.isVisible = true
                    when(Globales.DocumentoEnProceso.receptor!!.receptorTipoDoc) {
                        0 -> binding.radioRutDoc.isChecked = true
                        1 -> binding.radioCIDoc.isChecked = true
                        2 -> binding.radioPPDoc.isChecked = true
                        3 -> binding.radioOtroDoc.isChecked = true
                    }
                    (activity as? AppCompatActivity)?.supportActionBar?.subtitle= Globales.DocumentoEnProceso.receptor!!.clienteNombre
                    binding.etNombreClienteDoc.setText(Globales.DocumentoEnProceso.receptor!!.clienteNombre)
                    binding.etCabezalDocumentoDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorRut)
                    binding.etCabezalRazonDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorRazon)
                    binding.etCabezalDireccionDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorDireccion)
                    binding.etCabezalCiudadDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorCiudad)
                    binding.etCabezalBuscarClienteDoc.setText(Globales.DocumentoEnProceso.receptor!!.clienteCodigo)
                    binding.etCabezalTelDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorTel)
                    binding.etCabezalPaisDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorPais)
                    //
                    binding.etNombreClienteDoc.visibility = View.VISIBLE
                    binding.flEliminarClienteDoc.visibility = View.VISIBLE
                    binding.flEliminarClienteDoc.visibility = View.VISIBLE

                    //CARGAR LA FORMA DE PAGO DEL CLIENTE TODO
                    //CARGAR MAIL CLIENTE TODO
                }
                else {
                    (activity as? AppCompatActivity)?.supportActionBar?.subtitle= ""
                    Globales.ClienteSeleccionado = null
                    Globales.DocumentoEnProceso.receptor = null
                    binding.flEliminarClienteDoc.isVisible = false
                    binding.radioRutDoc.isChecked = true
                    binding.etNombreClienteDoc.setText("")
                    binding.etCabezalDocumentoDoc.setText("")
                    binding.etCabezalRazonDoc.setText("")
                    binding.etCabezalDireccionDoc.setText("")
                    binding.etCabezalCiudadDoc.setText("")
                    //binding.etListaPrecioDoc.setText("")
                    binding.etCabezalBuscarClienteDoc.setText("")
                    binding.etCabezalObservacionesDoc.setText("")
                    binding.etCabezalMailDoc.setText("")
                    binding.etCabezalTelDoc.setText("")
                    binding.etCabezalPaisDoc.setText("")
                    //
                    binding.etNombreClienteDoc.visibility = View.GONE
                    binding.flEliminarClienteDoc.visibility = View.GONE
                    AlertView.showAlert("¡Atención!","No existe cliente con ese código",requireContext())
                }
            }
            else
            {
                (activity as? AppCompatActivity)?.supportActionBar?.subtitle= ""
                Globales.ClienteSeleccionado = null
                Globales.DocumentoEnProceso.receptor = null
                binding.flEliminarClienteDoc.isVisible = false
                binding.radioRutDoc.isChecked = true
                binding.etNombreClienteDoc.setText("")
                binding.etCabezalDocumentoDoc.setText("")
                binding.etCabezalRazonDoc.setText("")
                binding.etCabezalDireccionDoc.setText("")
                binding.etCabezalCiudadDoc.setText("")
                //binding.etListaPrecioDoc.setText("")
                binding.etCabezalBuscarClienteDoc.setText("")
                binding.etCabezalObservacionesDoc.setText("")
                binding.etCabezalMailDoc.setText("")
                binding.etCabezalTelDoc.setText("")
                binding.etCabezalPaisDoc.setText("")
                //
                binding.etNombreClienteDoc.visibility = View.GONE
                binding.flEliminarClienteDoc.visibility = View.GONE
                //VALIDO LISTA DE PRECIO
                //binding.etListaPrecio.setText(Globales.ParametrosDocumento.listaPrecioCodigo)
                //
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al cargar el cliente!","${e.message}",requireContext())
        }
    }

    suspend fun CalcularDocumentoApi()
    {
        try {
            val result = DocPrincipalViewModel.postCalcularDocumento(Globales.DocumentoEnProceso)
            if (result!!.ok)
            {
                Globales.TotalesDocumento = result.elemento!!
                CalcularDocumento()
            }
            else
            {
                AlertView.showServerError("Server error al calcular documento", result.mensaje,requireContext())
            }
        }
        catch (e:Exception)
        {
            binding.progressBarDoc.visibility = View.GONE
            AlertView.showAlert("¡Error al calcular el documento!","${e.message}",requireContext())
        }
    }

    fun CalcularDocumento()
    {
        try {
            if (Globales.TotalesDocumento != null) {
                binding.tvSubtotalTotalDoc.text = Globales.TotalesDocumento.subtotal.toString()
                binding.tvDtoTotalDoc.text = Globales.TotalesDocumento.totalDtos.toString()
                binding.tvIVATotalDoc.text = Globales.TotalesDocumento.totalImpuestos.toString()
                binding.tvRedondeoTotalDoc.text = Globales.TotalesDocumento.redondeo.toString()
                when (Globales.MonedaSeleccionada) {
                    1 -> binding.tvTotalTotalDoc.text = "$ ${Globales.TotalesDocumento.total.toString()}"
                    2 -> binding.tvTotalTotalDoc.text = "U$" + "S " + Globales.TotalesDocumento.total.toString()
                }
                //CARGAR ADAPTER
                if (Globales.TotalesDocumento.items.isNotEmpty())
                {
                    adapterItems.items = Globales.TotalesDocumento.items as ArrayList<DTDocDetalle>
                    Globales.DocumentoEnProceso.detalle = adapterItems.items
                    adapterItems.updateList(adapterItems.items)
                }
            } else {
                binding.tvSubtotalTotalDoc.text = "0"
                binding.tvDtoTotalDoc.text = "0"
                binding.tvIVATotalDoc.text = "0"
                binding.tvRedondeoTotalDoc.text = "0"
                binding.tvTotalTotalDoc.text = "0"
            }
            binding.tvCantidadItems.text = "Cantidad: ${adapterItems.itemCount}"
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al cargar calcular el documento",e.message,requireContext())
        }
    }

    private fun DialogoSalir()
    {
        dialog= AlertDialog.Builder(requireActivity())
            .setIcon(R.drawable.atencion)
            .setTitle("¡Atención!")
            .setMessage("¿Desea cancelar el documento actual?")
            .setPositiveButton("Si", DialogInterface.OnClickListener {
                    dialogInterface, i ->
                ReiniciarVariables()
                Globales.editando_documento = false
                Globales.DocumentoEnProceso = null
                view?.findNavController()?.navigateUp()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialogInterface, i -> dialog.dismiss()
            })
            .setCancelable(true)
            .setOnCancelListener { "Cancelar" }
            .show()
    }

    private fun ReiniciarVariables()
    {
        try
        {
            //Limpio variables
            Globales.ClienteSeleccionado = null
            Globales.DocumentoEnProceso = null
            //Borrar items del adapter
            adapterItems.items.clear()
            adapterItems.notifyDataSetChanged()
            binding.tvCantidadItems.text = "Cantidad ${adapterItems.itemCount}"
            //Cargar los parametros del documento
            //VOLVER A CARGAR LA CONFIGURACION DEL DOCUMENTO
            //CargarParametrosDocumento()
            runBlocking {
                ObtenerParametrosDocumentoApi()
                binding.shimmerDocCargando.visibility = View.GONE
            }
            CargarCliente(null)
            //
            binding.tvSubtotalTotalDoc.text = "0"
            binding.tvDtoTotalDoc.text  = "0"
            binding.tvIVATotalDoc.text  = "0"
            binding.tvRedondeoTotalDoc.text  = "0"
            binding.tvTotalTotalDoc.text = "0"
            //
            binding.etCabezalObservacionesDoc.setText("")
            binding.etLugarDeEntregaDoc.setText("")
            binding.etFechaEntregaDoc.setText("")
            //
            fechaEntrega = ""
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle= ""
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al reiniciar variables",e.message,requireContext())
        }
    }

    fun CargarConfiguracionDelDocumento()
    {
        try {
            if (Globales.ParametrosDocumento != null)
            {
                //SI NO ES PENDIENTE TENGO QUE INSTANCIAR LO SIGUIENTE
                if (!isDocPendiente)
                {
                    Globales.DocumentoEnProceso!!.detalle = ArrayList<DTDocDetalle>()
                    Globales.DocumentoEnProceso!!.complemento = DTDocComplemento()
                    if (Globales.ParametrosDocumento.Validaciones.Valorizado)
                    {
                        Globales.DocumentoEnProceso!!.valorizado = DTDocValorizado(Globales.ParametrosDocumento.Configuraciones.MonedaCodigo,Globales.ParametrosDocumento.Configuraciones.TipoCambio,0,Globales.ParametrosDocumento.Configuraciones.ListaPrecioCodigo,ArrayList())
                    }
                    else
                    {
                        binding.cardTotalesDocumento.visibility = View.GONE
                        binding.LayListaPrecio.visibility = View.GONE
                    }
                }
                else
                {
                    if(!Globales.ParametrosDocumento.Validaciones.Valorizado)
                    {
                        binding.cardTotalesDocumento.visibility = View.GONE
                        binding.LayListaPrecio.visibility = View.GONE
                    }
                }
                //CARGO CONFIGURACION DEL DOCUMENTO
                binding.etCabezalDocFecha.isEnabled = Globales.ParametrosDocumento.Modificadores.ModificaFecha
                binding.etCabezalDocTC.isEnabled = Globales.ParametrosDocumento.Modificadores.ModificaTipoCambio
                binding.etCabezalDocTC.setText(Globales.ParametrosDocumento.Configuraciones.TipoCambio.toString())
                //MONEDAS
                when(Globales.ParametrosDocumento.Configuraciones.MonedaCodigo)
                {
                    "1" -> {
                        binding.rDocPesos.isChecked = true
                        Globales.MonedaSeleccionada = 1
                    }
                    "2" -> {
                        binding.rDocDolares.isChecked = true
                        Globales.MonedaSeleccionada = 2
                        binding.etCabezalDocTC.visibility = View.VISIBLE
                    }
                    "" -> {
                        binding.etCabezalDocTC.visibility = View.GONE
                        binding.rDocDolares.visibility = View.GONE
                        binding.rDocPesos.visibility = View.GONE
                    }
                }
                //
                if (!Globales.ParametrosDocumento.Modificadores.ModificaMoneda)
                {
                    binding.rDocPesos.isEnabled = false
                    binding.rDocDolares.isEnabled = false
                }
                //LISTA DE PRECIO
                binding.btnListaPrecio.isEnabled =Globales.ParametrosDocumento.Configuraciones.ModificaLista
                binding.btnListaPrecio.setText(Globales.ParametrosDocumento.Configuraciones.ListaPrecioCodigo)
                binding.btnListaPrecio.tag = Globales.ParametrosDocumento.Configuraciones.ListaPrecioCodigo
                //FUNCIONARIO
                if (Globales.ParametrosDocumento.Configuraciones.FuncionarioAsocia)
                {
                    when(Globales.ParametrosDocumento.Configuraciones.FuncionarioPerfil)
                    {
                        -2 ->
                        {
                            //SI EL USUARIO ES EL LOGGUEADO
                            binding.btnFuncionario.setText(Globales.UsuarioLoggueado.nombre)
                            binding.btnFuncionario.tag = Globales.UsuarioLoggueado.funcionarioId
                            binding.btnFuncionario.isEnabled = false
                        }
                        else ->
                        {
                            //SI ES TODOS O UN PERFIL EN ESPECIFICO
                            if(Globales.ParametrosDocumento.Configuraciones.FuncionarioDefID != 0)
                            {
                                //BuscoEseFuncionario y lo agrego
                                runBlocking {
                                    val result = DocPrincipalViewModel.getFuncionarioXIdUseCase(Globales.ParametrosDocumento.Configuraciones.FuncionarioDefID.toLong())
                                    if (result!!.ok)
                                    {
                                        binding.btnFuncionario.setText(result.elemento!!.nombre)
                                        binding.btnFuncionario.tag = result.elemento!!.id
                                        binding.btnFuncionario.isEnabled = false
                                        Globales.DocumentoEnProceso.complemento!!.funcionarioId = result.elemento!!.id.toInt()
                                    }
                                }
                            }
                            else
                            {
                                //SI NO TIENE FUNCIONARIO ESPECIFICO
                                //PREGUNTO SI NO HAY UNO YA SELECCIONADO EN EL DOCUMENTO ACTUAL (PORQUE CUANDO RECARGA LA PAGINA VUELVE A CARGAR LOS PARAMETROS DEL DOCUMENTO)
                                if(Globales.DocumentoEnProceso.complemento!!.funcionarioId == 0)
                                {
                                    Globales.DocumentoEnProceso.complemento!!.funcionarioId = 0
                                    binding.btnFuncionario.tag = 0
                                    binding.btnFuncionario.setText("")
                                    binding.btnFuncionario.isEnabled = true
                                }
                                else
                                {
                                    runBlocking {
                                        val result = DocPrincipalViewModel.getFuncionarioXIdUseCase(Globales.DocumentoEnProceso.complemento!!.funcionarioId.toLong())
                                        if (result!!.ok)
                                        {
                                            binding.btnFuncionario.setText(result.elemento!!.id.toString() + "/" + result.elemento!!.nombre + " " + result.elemento!!.apellido)
                                            binding.btnFuncionario.tag = result.elemento!!.id
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    //SI NO ASOCIA FUNCIONARIO
                    binding.btnFuncionario.visibility = View.GONE
                }
                //DEPOSITO
                if (Globales.ParametrosDocumento.Configuraciones.StockAfecta)
                {
                    //SI AFECTA STOCK
                    binding.btnDeposito.isEnabled = Globales.ParametrosDocumento.Configuraciones.StockModificaDeposito
                    binding.btnDeposito.setText(Globales.ParametrosDocumento.Configuraciones.StockDepositoCodigo)
                    binding.btnDeposito.tag = Globales.ParametrosDocumento.Configuraciones.StockDepositoCodigo
                }
                else
                {
                    //SI NO AFECTA STOCK
                    binding.LayDeposito.visibility = View.GONE
                }
                //Forma de pago
                if (Globales.ParametrosDocumento.Configuraciones.CuentaCorrienteAfecta)
                {
                    //SI AFECTA CUENTA CORRIENTE
                    binding.btnFormaPago.isEnabled = Globales.ParametrosDocumento.Modificadores.ModificaFormaPago
                    binding.btnFormaPago.tag = 0
                    binding.btnFormaPago.setText("Contado")
                    Globales.DocumentoEnProceso.valorizado!!.formaPagoDias = 0
                }
                else
                {
                    //NO AFECTA CC
                    binding.LayFormaPago.visibility = View.GONE
                    binding.btnFormaPago.tag = 0
                }
                //FECHA Y LUGAR DE ENTREGA
                binding.etLugarDeEntregaDoc.isEnabled = Globales.ParametrosDocumento.Modificadores.LugarDeEntrega
                binding.etFechaEntregaDoc.isEnabled = Globales.ParametrosDocumento.Modificadores.FechaEntrega
                //CLIENTE
                if (Globales.ParametrosDocumento.Configuraciones.ClienteAsocia)
                {
                    //ENVIA POR MAIL
                    when(Globales.ParametrosDocumento.Modificadores.EnviaMailCheck)
                    {
                        0 -> binding.chkEnviarPorMail.visibility = View.GONE
                        1 -> binding.chkEnviarPorMail.visibility = View.VISIBLE
                        2 -> { binding.chkEnviarPorMail.visibility = View.VISIBLE
                                binding.chkEnviarPorMail.isChecked = true
                        }
                    }
                    //Si ASOCIA CLIENTE
                    if (Globales.ParametrosDocumento.Configuraciones.ClienteDefID.toString() != "")
                    {
                        when(Globales.ParametrosDocumento.Configuraciones.ClienteAsociaTipo)
                        {
                            0 -> {
                                //SI ES CLIENTE
                                runBlocking {
                                    BuscarClienteApiConsult(Globales.ParametrosDocumento.Configuraciones.ClienteDefID.toString(),1)
                                }
                            }
                            1 -> {
                                //SI ES PROVEEDOR
                                runBlocking {
                                    BuscarClienteApiConsult(Globales.ParametrosDocumento.Configuraciones.ClienteDefID.toString(),1)
                                }
                            }
                        }
                    }
                    else
                    {
                        CargarCliente(null)
                    }
                }
                else
                {
                    //NO ASOCIA CLIENTE
                    binding.cardCabezalDocCliente.visibility = View.GONE
                }
                //
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al cargar la configuracion del documento",e.message,requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_documento,menu)
        //VALIDA BOTONES A MOSTRAR EN LA BARRA SUPERIOR
        if (!Globales.ParametrosDocumento.Configuraciones.MedioPagoAplica)
        {
            //Si no Aplica medio de pago
            menu.findItem(R.id.btnMediosDePago).isVisible = false
        }
        //
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.btnEmitirDocumento ->
            {
                item.isVisible = false
                    dialog= AlertDialog.Builder(requireActivity())
                        .setIcon(R.drawable.atencion)
                        .setTitle("¡Atención!")
                        .setMessage("¿Desea emitir el documento?")
                        .setPositiveButton("Si", DialogInterface.OnClickListener {
                                dialogInterface, i ->
                            run {
                                dialog.dismiss()
                                CargarDocumentoParaEnviar(true)
                                item.isVisible = true
                            }
                        })
                        .setNegativeButton("No", DialogInterface.OnClickListener {
                                dialogInterface, i ->
                            run {
                                item.isVisible = true
                                dialog.dismiss()
                            }
                        })
                        .setCancelable(true)
                        .setOnCancelListener { item.isVisible = true }
                        .show()
            }
            R.id.btnGuardarDocumentoActual ->
            {
                item.isVisible = false
                dialog= AlertDialog.Builder(requireActivity())
                    .setIcon(R.drawable.atencion)
                    .setTitle("¡Atención!")
                    .setMessage("¿Desea guardar el documento?")
                    .setPositiveButton("Si", DialogInterface.OnClickListener {
                            dialogInterface, i ->
                        run {
                            dialog.dismiss()
                            CargarDocumentoParaEnviar(false)
                            item.isVisible = true
                        }
                    })
                    .setNegativeButton("No", DialogInterface.OnClickListener {
                            dialogInterface, i ->
                        run {
                            item.isVisible = true
                            dialog.dismiss()
                        }
                    })
                    .setCancelable(true)
                    .setOnCancelListener { item.isVisible = true }
                    .show()
            }
            R.id.btnMediosDePago ->
            {
                if (Globales.DocumentoEnProceso.detalle!!.isNotEmpty())
                {
                    val action = DocumentoPrincipalFragmentDirections.actionDocumentoPrincipalFragmentToMediosDePagoFragment()
                    view?.findNavController()?.navigate(action)
                }
            }
            R.id.btnReferencias ->
            {
                Snackbar.make(binding.messLayo,"Sección no disponible actualmente", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(resources.getColor(R.color.rosado))
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    suspend fun BuscarClienteApiConsult(codigo:String,tipoBusqueda:Int)
    {
        try {
            coroutineScope {
                when(tipoBusqueda)
                {
                    0 -> {
                        //POR CODIGO
                        resultCli = DocPrincipalViewModel.getClienteXCodigo(codigo)!!
                    }
                    1 -> {
                        //POR ID
                        resultCli = DocPrincipalViewModel.getClienteXIdUseCase(codigo.toLong())!!
                    }
                }
                if (resultCli.ok)
                {
                    if (resultCli.ok)
                    {
                        CargarCliente(resultCli.elemento)
                    }
                    else
                    {
                        AlertView.showServerError("Server Error", resultCli.mensaje,requireContext())
                    }
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al buscar cliente!","${e.message}",requireContext())
        }
    }

    suspend fun BuscarProveedorApiConsult(codigo:String,tipoBusqueda:Int)
    {
        try {
            coroutineScope {
                when(tipoBusqueda)
                {
                    0 -> {
                        //POR CODIGO
                        resultCli = DocPrincipalViewModel.getProveedorXCodigoUseCase(codigo)!!
                    }
                    1 -> {
                        //POR ID
                        resultCli = DocPrincipalViewModel.getProveedorXIdUseCase(codigo.toLong())!!
                    }
                }
                if (resultCli.ok)
                {
                    if (resultCli.ok)
                    {
                        CargarCliente(resultCli.elemento)
                    }
                    else
                    {
                        AlertView.showServerError("Server Error", resultCli.mensaje,requireContext())
                    }
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al buscar cliente!","${e.message}",requireContext())
        }
    }

    suspend fun ValidarDocumentoApi()
    {
        try {
            val result = DocPrincipalViewModel.postValidarDocumento(Globales.DocumentoEnProceso)
            if (result!!.ok)
            {
                // TODO: LLAMO AL EMITIR DOCUMENTO
            }
            else
            {
                AlertView.showServerError("¡Error al validar el documento!","${result.mensaje}",requireContext())
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al guardar el documento!","${e.message}",requireContext())
        }
    }

    suspend fun ObtenerParametrosDocumentoApi()
    {
        try {
                val result = DocPrincipalViewModel.getNuevoDocumentoUseCase(Globales.UsuarioLoggueado.usuario, Globales.Terminal.Codigo,Globales.CodigoTipoDocSeleccionado)
                if (result!!.ok)
                {
                    Globales.ParametrosDocumento = result.elemento!!.parametros
                    Globales.DocumentoEnProceso = result.elemento!!.documento
                    //
                    (activity as? AppCompatActivity)?.supportActionBar?.title = Globales.ParametrosDocumento.Descripcion + "Nro: ${Globales.DocumentoEnProceso.cabezal!!.nroDoc}"
                    CargarConfiguracionDelDocumento()
                }
                else
                {
                    AlertView.showServerError("Server Error", "${result.mensaje}", requireContext())
                }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al obtener los parametros del documento!","${e.message}",requireContext())
        }
    }

    fun MostarDialogoAddArt(articulo: DTArticulo?)
    {
        try {
            val dialogoAdd = AddArticuloFragment(articulo) { detalle -> onDetalleSelected() }
            dialogoAdd.show(parentFragmentManager,"AddArt")
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
        }
    }

   @SuppressLint("SetTextI18n")
    fun onDetalleSelected()
    {
        this.onResume()
    }

    //LISTA DE PRECIO///////////////////////////////////////////////
    fun MostarDialogoListaPrecio()
    {
        try {
            val dialogoListaPrecio = ListaGenericoFragment(Globales.TBusquedaGenerica.LISTAPRECIO.codigo) { Listaprecio -> onListaPrecioSelected(Listaprecio as DTGenerico)}
            dialogoListaPrecio.show(parentFragmentManager,"ListaPre")
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun onListaPrecioSelected(Listaprecio:DTGenerico)
    {
        binding.btnListaPrecio.setText(Listaprecio.Codigo + "/" + Listaprecio.Nombre)
        binding.btnListaPrecio.tag = Listaprecio.Codigo
        Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo = Listaprecio.Codigo
    }

    //FUNCIONARIO///////////////////////////////////////////////
    fun MostarDialogoListaFuncionario()
    {
        try {
            val dialogoFuncionario = ListaGenericoFragment(Globales.TBusquedaGenerica.FUNCIONARIO.codigo) {funcionario -> onFuncionarioSelected(funcionario as DTGenerico)}
            dialogoFuncionario.show(parentFragmentManager,"ListaFunc")
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun onFuncionarioSelected(funcionario:DTGenerico)
    {
        runBlocking {
            val result = DocPrincipalViewModel.getFuncionarioXIdUseCase(funcionario.Id.toLong())
            if (result!!.ok)
            {
                binding.btnFuncionario.setText(funcionario.Id + "/" + funcionario.Nombre + " " + funcionario.Apellido)
                binding.btnFuncionario.tag = result.elemento!!.id
                Globales.DocumentoEnProceso.complemento!!.funcionarioId =result.elemento!!.id.toInt()
            }
        }
    }

    //DEPOSITO///////////////////////////////////////////////
    fun MostarDialogoListaDeposito()
    {
        try {
            val dialogoDeposito = ListaGenericoFragment(Globales.TBusquedaGenerica.DEPOSITO.codigo) {deposito -> onDepositoSelected(deposito as DTGenerico)}
            dialogoDeposito.show(parentFragmentManager,"ListaDep")
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun onDepositoSelected(deposito:DTGenerico)
    {
        binding.btnDeposito.setText(deposito.Codigo + "/" + deposito.Nombre)
        binding.btnDeposito.tag = deposito.Codigo
        Globales.DocumentoEnProceso.complemento!!.codigoDeposito = deposito.Codigo

    }

    //FORMA DE PAGO///////////////////////////////////////////////
    fun MostarDialogoListaFormaPago()
    {
        try {
            val dialogoFormaPago = ListaGenericoFragment(Globales.TBusquedaGenerica.FORMAPAGO.codigo) {formapago -> onFormaPagoSelected(formapago as DTGenerico)}
            dialogoFormaPago.show(parentFragmentManager,"ListaFormaPago")
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun onFormaPagoSelected(formapago:DTGenerico)
    {
        binding.btnFormaPago.setText(formapago.Nombre)
        binding.btnFormaPago.tag = formapago.Dias
        Globales.DocumentoEnProceso.valorizado!!.formaPagoDias = formapago.Dias!!
    }

    fun AgregarMedioPagoPorDefecto()
    {
        try
        {
            if (Globales.ParametrosDocumento.Configuraciones.MedioPagoAplica)
            {
                if (Globales.ParametrosDocumento.Configuraciones.MedioPagoDefID != 0)
                {
                    if (Globales.DocumentoEnProceso.valorizado!!.pagos.size == 0)
                    {
                        var pago = DTDocPago()
                        pago.importe= Globales.TotalesDocumento.total
                        pago.tipoCambio = binding.etCabezalDocTC.text.toString().toDouble()
                        pago.medioPagoCodigo = Globales.ParametrosDocumento.Configuraciones.MedioPagoDefID
                        pago.monedaCodigo = Globales.DocumentoEnProceso.valorizado!!.monedaCodigo
                        pago.fecha = Globales.DocumentoEnProceso.cabezal!!.fecha
                        pago.fechaVto = Globales.Herramientas.convertirYYYYMMDD(fechaEmision)
                        Globales.DocumentoEnProceso.valorizado!!.pagos.add(pago)
                    }
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al agregar el medio de pago por defecto","${e.message}",requireContext())
        }
    }

    fun CargarDocumentoParaEnviar(esParaEmitir:Boolean)
    {
        try {
            //MODIFICO RECEPTOR
            if (Globales.DocumentoEnProceso.receptor != null)
            {
                Globales.DocumentoEnProceso.receptor!!.receptorRut = binding.etCabezalDocumentoDoc.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorRazon = binding.etCabezalRazonDoc.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorDireccion = binding.etCabezalDireccionDoc.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorCiudad = binding.etCabezalCiudadDoc.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorPais = binding.etCabezalPaisDoc.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorMail = binding.etCabezalMailDoc.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorTel = binding.etCabezalTelDoc.text.toString()
            }
            //CARGO EL DEPOSITO
            Globales.DocumentoEnProceso.complemento!!.codigoDeposito = binding.btnDeposito.tag.toString()
            //CARGO CODIGO DE SUCURSAL
            Globales.DocumentoEnProceso.complemento!!.codigoSucursal = Globales.Terminal.SucursalDoc
            //CARGO EL FUNCIONARIO
            Globales.DocumentoEnProceso.complemento!!.funcionarioId = binding.btnFuncionario.tag.toString().toInt()
            if (Globales.ParametrosDocumento.Validaciones.Valorizado)
            {
                //CARGO LA LISTA DE PRECIO
                Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo = binding.btnListaPrecio.tag.toString()
                //CARGO LA FORMA DE PAGO
                Globales.DocumentoEnProceso.valorizado!!.formaPagoDias = binding.btnFormaPago.tag.toString().toInt()
            }
            //CARGO OBSERVACIONES
            Globales.DocumentoEnProceso.cabezal!!.observaciones = binding.etCabezalObservacionesDoc.text.toString()
            //CARGO LUGAR DE ENTREGA
            Globales.DocumentoEnProceso.complemento!!.lugarEntrega = binding.etLugarDeEntregaDoc.text.toString()
            //CARGO FECHA DE ENTREGA
            if (fechaEntrega.isNotEmpty())
            {
                Globales.DocumentoEnProceso.complemento!!.fechaEntrega = Globales.Herramientas.convertirYYYYMMDD(fechaEntrega)
            }
            //
            AgregarMedioPagoPorDefecto()
            if (esParaEmitir)
            {
                runBlocking {
                    //VOY A EMITIR EL DOCUMENTO
                    ValidarDocumentoApi()
                }
            }
            else
            {
                runBlocking {
                    //GUARDAR DOCUMENTO PENDIENTE
                    GuardarDocumentoPendiente()
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al cargar el documento para enviar","${e.message}",requireContext())
        }
    }

    suspend fun GuardarDocumentoPendiente()
    {
        try {
            //LLAMO AL GUARDAR DOCUMENTO
                val resuGuardado = DocPrincipalViewModel.postGuardarDocumento(Globales.DocumentoEnProceso)
                if (resuGuardado!!.ok)
                {
                    if (!isDocPendiente)
                    {
                        Snackbar.make(binding.messLayo,"${resuGuardado.mensaje}", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(resources.getColor(R.color.green))
                            .show()
                        ReiniciarVariables()
                    }
                    else
                    {
                        Snackbar.make(requireView(),"Documento guardado como pendiente", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setBackgroundTint(resources.getColor(R.color.green))
                            .show()
                        findNavController().popBackStack()
                    }
                }
                else
                {
                    AlertView.showServerError("¡Error al guardar el documento pendiente!","${resuGuardado.mensaje}",requireContext())
                }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al guardar el documento como pendiente","${e.message}",requireContext())
        }
    }

    fun CargarDatosDelDocumentoPendiente()
    {
        try {
            //Globales.DocumentoEnProceso.valorizado!!.pagos
            //CABEZAL
            //TIPO DE CAMBIO
            if (Globales.DocumentoEnProceso.valorizado != null)
            {
                binding.etCabezalDocTC.setText(Globales.DocumentoEnProceso.valorizado!!.tipoCambio.toString())
                //FORMA DE PAGO
                binding.btnFormaPago.tag = Globales.DocumentoEnProceso.valorizado!!.formaPagoDias
                if (Globales.DocumentoEnProceso.valorizado!!.formaPagoDias == 0)
                    binding.btnFormaPago.text = "Contado"
                else
                    binding.btnFormaPago.text = Globales.DocumentoEnProceso.valorizado!!.formaPagoDias.toString()
                //LISTA DE PRECIO
                binding.btnListaPrecio.text = Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo
                binding.btnListaPrecio.tag = Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo
                when(Globales.DocumentoEnProceso!!.valorizado!!.monedaCodigo)
                {
                    "1" -> {
                        binding.rDocPesos.isChecked = true
                        Globales.MonedaSeleccionada = 1
                    }
                    "2" -> {
                        binding.rDocDolares.isChecked = true
                        Globales.MonedaSeleccionada = 2
                        binding.etCabezalDocTC.visibility = View.VISIBLE
                    }
                    "" -> {
                        binding.etCabezalDocTC.visibility = View.GONE
                        binding.rDocDolares.visibility = View.GONE
                        binding.rDocPesos.visibility = View.GONE
                    }
                }
            }
            //NroDoc
            binding.tvNroDoc.text = "Nro Doc: " + Globales.DocumentoEnProceso.cabezal!!.nroDoc
            //FECHA
            binding.etCabezalDocFecha.setText(Globales.Herramientas.convertirFechaHora(Globales.DocumentoEnProceso.cabezal!!.fecha.toString()))
            fechaEmision = Globales.DocumentoEnProceso.cabezal!!.fecha.toString()
            //USUARIO
            Globales.DocumentoEnProceso.cabezal!!.usuario
            //OBSERVACIONES
            binding.etCabezalObservacionesDoc.setText(Globales.DocumentoEnProceso.cabezal!!.observaciones)
            //TIPODOCCODIGO
            //Globales.DocumentoEnProceso.cabezal!!.tipoDocCodigo
            //TERMINAL
            //Globales.DocumentoEnProceso.cabezal!!.terminal
            //

            //CARGAR LA LISTA DE ARTICULOS
            adapterItems = ItemDocAdapter(ArrayList<DTDocDetalle>())
            adapterItems.items = Globales.DocumentoEnProceso.detalle as ArrayList<DTDocDetalle>
            adapterItems.updateList(adapterItems.items)

            //CARGAR CLIENTE
            //Globales.DocumentoEnProceso.receptor!!.clienteId
            //Globales.DocumentoEnProceso.receptor!!.clienteCodigo
            if (Globales.DocumentoEnProceso.receptor != null)
            {
                binding.etNombreClienteDoc.setText(Globales.DocumentoEnProceso.receptor!!.clienteNombre)
                binding.etCabezalDocumentoDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorRut)
                binding.etCabezalRazonDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorRazon)
                binding.etCabezalDireccionDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorDireccion)
                binding.etCabezalCiudadDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorCiudad)
                binding.etCabezalPaisDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorPais)
                binding.etCabezalTelDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorTel)
                binding.etCabezalMailDoc.setText(Globales.DocumentoEnProceso.receptor!!.receptorMail)
                // TODO: binding.chkEnviarPorMail.isChecked

                binding.flEliminarClienteDoc.isVisible = true
                when(Globales.DocumentoEnProceso.receptor!!.receptorTipoDoc) {
                    0 -> binding.radioRutDoc.isChecked = true
                    1 -> binding.radioCIDoc.isChecked = true
                    2 -> binding.radioPPDoc.isChecked = true
                    3 -> binding.radioOtroDoc.isChecked = true
                }
                (activity as? AppCompatActivity)?.supportActionBar?.subtitle= Globales.DocumentoEnProceso.receptor!!.clienteNombre
            }
            //CARGAR COMPLEMENTO
            binding.btnFuncionario.tag = Globales.DocumentoEnProceso.complemento!!.funcionarioId
            binding.btnFuncionario.setText(Globales.DocumentoEnProceso.complemento!!.funcionarioId.toString())
            binding.etLugarDeEntregaDoc.setText(Globales.DocumentoEnProceso.complemento!!.lugarEntrega)
            binding.btnDeposito.setText(Globales.DocumentoEnProceso.complemento!!.codigoDeposito)
            binding.btnDeposito.tag = Globales.DocumentoEnProceso.complemento!!.codigoDeposito
            binding.etFechaEntregaDoc.setText(Globales.Herramientas.convertirFechaHora(Globales.DocumentoEnProceso.complemento!!.fechaEntrega.toString()))
            // TODO:  Globales.DocumentoEnProceso.referencias
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al cargar datos del documento existente!","${e.message}",requireContext())
        }
    }*/
}
