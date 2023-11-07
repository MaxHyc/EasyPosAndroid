package com.devhyc.easypos.ui.listadodocumentos

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.ui.listadodocumentos.adapter.ListaDeDocAdapter
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocLista
import com.devhyc.easypos.data.model.DTParamDocLista
import com.devhyc.easypos.databinding.FragmentListaDeDocumentosBinding
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.DatePickerFragment
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.*

@AndroidEntryPoint
class ListaDeDocumentosFragment : Fragment() {

    private lateinit var ListadoDocViewModel: ListaDeDocumentosViewModel
    private lateinit var _binding: FragmentListaDeDocumentosBinding
    private val binding get() = _binding!!
    //
    private var fechaDesde:String=""
    private var fechaHasta:String=""
    //
    private var originalArrayDoc: ArrayList<DTDocLista> = ArrayList()
    private var filtradoArrayDoc: ArrayList<DTDocLista> = ArrayList()
    private var adapterDocumentos: ListaDeDocAdapter = ListaDeDocAdapter(ArrayList())

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = ""
    }

    override fun onResume() {
        super.onResume()
        if (adapterDocumentos != null)
        {
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Cantidad: ${adapterDocumentos.itemCount}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ListadoDocViewModel = ViewModelProvider(this)[ListaDeDocumentosViewModel::class.java]
        _binding = FragmentListaDeDocumentosBinding.inflate(this.layoutInflater)
        //
        binding.etListaDocFechaDesde.setOnClickListener { ShowDialogPickerFechaDesde() }
        binding.etListaDocFechaHasta.setOnClickListener { ShowDialogPickerFechaHasta() }

        binding.etListaDocFechaDesde.setText(Globales.Herramientas.ObtenerFechaActual().FechaDD_MM_YYYY)
        binding.etListaDocFechaHasta.setText(Globales.Herramientas.ObtenerFechaActual().FechaDD_MM_YYYY)

        fechaDesde = Globales.Herramientas.ObtenerFechaActual().FechayyyygMMgdd
        fechaHasta = Globales.Herramientas.ObtenerFechaActual().FechayyyygMMgdd

        ListadoDocViewModel.ListarDocumentos(DTParamDocLista(binding.chkPendientes.isChecked,binding.chkAnulados.isChecked,fechaDesde,fechaHasta))
        ListadoDocViewModel.ListadoDocs.observe(this, Observer {
            try {
                if (it.ok)
                {

                    originalArrayDoc = it.elemento!! as ArrayList<DTDocLista>
                    adapterDocumentos = ListaDeDocAdapter(ArrayList<DTDocLista>(it.elemento!!))
                    adapterDocumentos.setOnItemClickListener(object: ListaDeDocAdapter.OnItemClickListener{
                        override fun onItemClick(position: Int) {
                            //LLAMAR PARA VER EL DOCUMENTO
                            if (binding.chkPendientes.isChecked)
                            {
                                //ES UN DOCUMENTO PENDIENTE
                                /*runBlocking {
                                    val res = ListadoDocViewModel.getDocumentoPendienteUseCase(adapterDocumentos.documentos[position].TerminalCodigo,adapterDocumentos.documentos[position].TipoDocCodigo,adapterDocumentos.documentos[position].NroDoc.toString())
                                    if (res!!.ok)
                                    {
                                        Globales.DocumentoEnProceso = res.elemento
                                        Globales.CodigoTipoDocSeleccionado = adapterDocumentos.documentos[position].TipoDocCodigo
                                        val action = ListaDeDocumentosFragmentDirections.actionListaDeDocumentosFragmentToDocumentoPrincipalFragment(true)
                                        view?.findNavController()?.navigate(action)
                                    }
                                    else
                                    {
                                        AlertView.showServerError("¡Atención!",res.mensaje,requireContext())
                                    }
                                }*/
                            }
                            else
                            {
                                //ES UN DOCUMENTO EMITIDO
                                val action = ListaDeDocumentosFragmentDirections.actionListaDeDocumentosFragmentToDocumentoVistaFragment(adapterDocumentos.documentos[position].TerminalCodigo,adapterDocumentos.documentos[position].TipoDocCodigo,adapterDocumentos.documentos[position].NroDoc)
                                view?.findNavController()?.navigate(action)
                            }
                        }
                    })
                    binding.rvListaDocs.layoutManager = LinearLayoutManager(activity)
                    binding.rvListaDocs.adapter = adapterDocumentos
                    binding.tvCantidadDocs.text= "Cantidad: ${adapterDocumentos.itemCount}"
                    (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Cantidad: ${adapterDocumentos.itemCount}"
                    binding.rvListaDocs.isVisible = true
                }
                else
                {
                    AlertView.showServerError("¡Atención!",it.mensaje,requireContext())
                }
            }
            catch (e:Exception)
            {
                Toast.makeText(requireActivity(),"${e.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = binding.root
        setHasOptionsMenu(true)
        ListadoDocViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.shimmerCargandoDocs.isVisible = it
        })
        binding.chkPendientes.setOnCheckedChangeListener { buttonView, isChecked ->
            ListadoDocViewModel.ListarDocumentos(DTParamDocLista(binding.chkPendientes.isChecked,binding.chkAnulados.isChecked,fechaDesde,fechaHasta))
        }
        binding.chkAnulados.setOnCheckedChangeListener { buttonView, isChecked ->
            ListadoDocViewModel.ListarDocumentos(DTParamDocLista(binding.chkPendientes.isChecked,binding.chkAnulados.isChecked,fechaDesde,fechaHasta))
        }
        //
        return root
    }

    fun ShowDialogPickerFechaDesde()
    {
        try {
            val datePicker = DatePickerFragment {day,month,year -> onFechaDesdeSeleccionada(day,month,year) }
            datePicker.show(parentFragmentManager,"datePicker")
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atención!",e.message,binding.root.context)
        }
    }

    @SuppressLint("SetTextI18n")
    fun onFechaDesdeSeleccionada(day:Int, month:Int, year:Int)
    {
        var mesCorrecto = month + 1
        var m = mesCorrecto.toString()
        var mesfinal = m
        var diafinal = day.toString()
        if (m.length == 1)
        {
            mesfinal = "0$m"
        }
        if(day.toString().length == 1)
        {
            diafinal = "0$diafinal"
        }
        fechaDesde = "$year-$mesfinal-$diafinal"
        binding.etListaDocFechaDesde.setText("$diafinal/$mesfinal/$year")
        ListadoDocViewModel.ListarDocumentos(DTParamDocLista(binding.chkPendientes.isChecked,binding.chkAnulados.isChecked,fechaDesde,fechaHasta))
    }

    fun ShowDialogPickerFechaHasta()
    {
        try {
            val datePicker = DatePickerFragment {day,month,year -> onFechaHastaSeleccionada(day,month,year) }
            datePicker.show(parentFragmentManager,"datePicker")
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atención!",e.message,binding.root.context)
        }
    }

    @SuppressLint("SetTextI18n")
    fun onFechaHastaSeleccionada(day:Int, month:Int, year:Int)
    {
        var mesCorrecto = month + 1
        var m = mesCorrecto.toString()
        var mesfinal = m
        var diafinal = day.toString()
        if (m.length == 1)
        {
            mesfinal = "0$m"
        }
        if(day.toString().length == 1)
        {
            diafinal = "0$diafinal"
        }
        fechaHasta = "$year-$mesfinal-$diafinal"
        binding.etListaDocFechaHasta.setText("$diafinal/$mesfinal/$year")
        ListadoDocViewModel.ListarDocumentos(DTParamDocLista(binding.chkPendientes.isChecked,binding.chkAnulados.isChecked,fechaDesde,fechaHasta))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.btnRecargarItems ->
            {
                ListadoDocViewModel.ListarDocumentos(DTParamDocLista(binding.chkPendientes.isChecked,binding.chkAnulados.isChecked,fechaDesde,fechaHasta))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_busqueda,menu)

        val manager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.tvbusqueda)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("",false)
                searchItem.collapseActionView()
                if (query != null) {
                    filtar(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filtar(newText)
                }
                return false
            }
        }
        )
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun filtar(filtroDeTexto: String) {
        try {
            filtradoArrayDoc.clear()
            if(filtroDeTexto.isNotEmpty())
            {
                originalArrayDoc.forEach{
                    //POR NRODOC
                    if(it.NroDoc.toString().lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        filtradoArrayDoc.add(it)
                    }
                    //POR NROCFE
                    if(it.NroCfe.toString().lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        filtradoArrayDoc.add(it)
                    }
                    //POR TIPOCFE
                    if(it.TipoCfeNombre.toString().lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        filtradoArrayDoc.add(it)
                    }
                    //POR TOTAL
                    if(it.Total.toString().lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        filtradoArrayDoc.add(it)
                    }
                    //POR MONEDA SIGNO
                    if(it.MonedaSigno.toString().lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        filtradoArrayDoc.add(it)
                    }
                    //POR CODIGO DE TIPO DE DOCUMENTO
                    if(it.TipoDocCodigo.toString().lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        filtradoArrayDoc.add(it)
                    }
                    //POR NOMBRE CLIENTE
                    if(it.ClienteNombre.toString().lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        filtradoArrayDoc.add(it)
                    }
                }
                adapterDocumentos.documentos = filtradoArrayDoc
                adapterDocumentos.notifyDataSetChanged()
            }
            else
            {
                filtradoArrayDoc.clear()
                filtradoArrayDoc.addAll(originalArrayDoc)
                adapterDocumentos.notifyDataSetChanged()
            }
            binding.tvCantidadDocs.text="Cantidad: ${adapterDocumentos.itemCount}"
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Cantidad: ${adapterDocumentos.itemCount}"
        }
        catch (e:Exception)
        {

        }
    }
}