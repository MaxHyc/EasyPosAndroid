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

    fun PantallaDeCarga(si:Boolean)
    {
        if (si)
        {
            binding.shimmerDocCargando.visibility = View.VISIBLE
        }
        else
        {
            binding.shimmerDocCargando.visibility = View.GONE
        }
    }

    override fun onResume() {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        //CARGO LOS ITEMS
        try {
            if (Globales.isEmitido)
            {
                ReiniciarVariables()
                Globales.isEmitido = false
            }
            if (DocumentoEnProceso.detalle != null)
            {
                runBlocking { CalcularDocumentoApi() }
            }
            //CARGO EL RECEPTOR
            if (DocumentoEnProceso.receptor != null)
            {
                binding.tvClienteNombre.visibility = View.VISIBLE
                binding.tvClienteNombre.text = DocumentoEnProceso.receptor!!.receptorRazon + "\n" + DocumentoEnProceso.receptor!!.receptorRut
            }
            else
            {
                binding.tvClienteNombre.visibility = View.GONE
            }
        }
        catch (e:Exception)
        {

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
        binding.flAddRubro.setOnClickListener {
            val action = DocumentoPrincipalFragmentDirections.actionDocumentoPrincipalFragmentToListaDeArticulosFragment(true)
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
        //OBTENER LA CAJA ABIERTA
        DocPrincipalViewModel.ObtenerCajaAbierta()
        DocPrincipalViewModel.caja.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == null)
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
            }
            else
            {
                (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "N° Caja: ${Globales.NroCaja} (${it.Usuario})"
            }
        })
        return root
    }

    fun MostrarInfoDocEnNavBarr()
    {
        try {
            (activity as? AppCompatActivity)?.supportActionBar?.title = "${ParametrosDocumento.Descripcion} N° ${DocumentoEnProceso.cabezal!!.nroDoc}"
            //(activity as? AppCompatActivity)?.supportActionBar?.subtitle = "N° ${DocumentoEnProceso.cabezal!!.nroDoc}"
        }
        catch (e:Exception)
        {
            AlertView.showServerError("Error", "${e.message}", requireContext())
        }
    }

    suspend fun ObtenerParametrosDocumentoApi()
    {
        try {
            val result = DocPrincipalViewModel.getNuevoDocumentoUseCase(Globales.UsuarioLoggueado.usuario, Globales.Terminal.Codigo,"TICK")
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
                //
                //CARGO CODIGO DE SUCURSAL
                DocumentoEnProceso.complemento!!.codigoSucursal = Globales.Terminal.SucursalDoc
                //CARGO EL FUNCIONARIO
                DocumentoEnProceso.complemento!!.funcionarioId = Globales.UsuarioLoggueado.funcionarioId
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
                AbrirVentanaMedioDePago()
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

    fun AbrirVentanaMedioDePago()
    {
        try {
            if (DocumentoEnProceso.detalle!!.isNotEmpty())
            {
                //VENTANA MEDIO PAGO MULTIPLE
                //val action = DocumentoPrincipalFragmentDirections.actionDocumentoPrincipalFragmentToMediosDePagoFragment()
                //view?.findNavController()?.navigate(action)
                //VENTANA MEDIO DE PAGO SIMPLE
                val action = DocumentoPrincipalFragmentDirections.actionDocumentoPrincipalFragmentToMediosPagosLiteFragment()
                view?.findNavController()?.navigate(action)
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al abrir ventana de medio de pago",e.message,requireContext())
        }
    }
}
