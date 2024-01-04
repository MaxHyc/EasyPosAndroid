package com.devhyc.easypos.ui.articulos

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easymanagementmobile.ui.articulos.adapter.ItemArticuloAdapter
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocDetalle
import com.devhyc.easypos.data.model.DTFamiliaHija
import com.devhyc.easypos.data.model.DTFamiliaPadre
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.databinding.FragmentListaDeArticulosBinding
import com.devhyc.easypos.ui.addarticulos.AddArticuloFragment
import com.devhyc.easypos.ui.articulos.adapter.ItemRubroAdapter
import com.devhyc.easypos.ui.articulos.adapter.customSpinnerAdapter
import com.devhyc.easypos.ui.articulos.adapter.customSpinnerSubFamiliaAdapter
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ListaDeArticulosFragment() : Fragment() {

    private var _binding: FragmentListaDeArticulosBinding? = null
    private val binding get() = _binding!!
    //View Model
    private lateinit var ListaDeArticulosViewModel: ListaDeArticulosViewModel
    //
    private lateinit var adapterSubFamilia: customSpinnerSubFamiliaAdapter
    private lateinit var adapterFamilia: customSpinnerAdapter
    //ARTICULOS
    private lateinit var adapterArticulos: ItemArticuloAdapter
    private var originalArrayList: ArrayList<DTArticulo> = ArrayList()
    private var filtradoArrayList: ArrayList<DTArticulo> = ArrayList()

    //RUBROS
    private lateinit var adapterRubros: ItemRubroAdapter
    private  var originalRubroArrayList: ArrayList<DTRubro> = ArrayList()
    private var filtradoRubroArrayList: ArrayList<DTRubro> = ArrayList()

    //
    private var TextoBusqueda:String=""
    private var CodigoSeleccionado:String=""
    //
    private var EsRubro:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        if (EsRubro)
        {
            ListaDeArticulosViewModel.CargarRubros()
        }
            else
        {
            ListaDeArticulosViewModel.ListaArticulos.value = ArrayList<DTArticulo>()
        }
    }

    override fun onResume() {
        CargarTodo()
        super.onResume()
    }

    fun CargarTodo()
    {
        if (EsRubro)
        {
            //CargarRubros
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Lista de rubros"
            binding.rvArticulos.visibility = View.GONE
            binding.rvRubros.visibility = View.VISIBLE
            binding.viewLoading.isVisible = true
            binding.rvArticulos.isVisible = false
            ListaDeArticulosViewModel.CargarRubros()
        }
        else
        {
            //Cargar Articulos
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Lista de artículos"
            binding.rvArticulos.visibility = View.VISIBLE
            binding.rvRubros.visibility = View.GONE
            binding.viewLoading.isVisible = true
            binding.rvArticulos.isVisible = false
            ListaDeArticulosViewModel.CargarFamilias()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        ListaDeArticulosViewModel = ViewModelProvider(this)[com.devhyc.easypos.ui.articulos.ListaDeArticulosViewModel::class.java]
        _binding = FragmentListaDeArticulosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //
        if (arguments !=null) {
            val bundle: Bundle = arguments as Bundle
            EsRubro = bundle.getBoolean("rubros", false)
        }
        //EVENTOS OBSERVER DE RUBROS
        ListaDeArticulosViewModel.ListaRubros.observe(viewLifecycleOwner, Observer {
            try {
                originalRubroArrayList = it as ArrayList<DTRubro>
                adapterRubros = ItemRubroAdapter(ArrayList<DTRubro>(it))
                adapterRubros.setOnItemClickListener(object: ItemRubroAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int) {
                        //CARGAR DOC DETALLE
                        var item = DTArticulo()
                        item.id = adapterRubros.rubros[position].id.toInt()
                        item.nombre = adapterRubros.rubros[position].nombre
                        item.codigo = adapterRubros.rubros[position].codigo
                        item.impuesto = adapterRubros.rubros[position].impuestoCodigo.toInt()
                        item.impuestoValor = adapterRubros.rubros[position].impuestoTasa.toInt()
                        item.esRubro = 1
                        MostarDialogoAddArt(item)
                    }
                })
                binding.rvRubros.layoutManager = LinearLayoutManager(activity)
                binding.rvRubros.adapter = adapterRubros
                Snackbar.make(binding.messArtLayout,"${adapterRubros.itemCount} rubros listados.", Snackbar.LENGTH_SHORT).setAnimationMode(
                    BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                ).show()
                binding.tvCantidadArticulosListado.text="Cantidad: ${adapterRubros.itemCount}"
                binding.viewLoading.isVisible = false
                binding.rvRubros.isVisible = true
            }
            catch (e:Exception)
            {
                Toast.makeText(requireActivity(),"${e.message}", Toast.LENGTH_SHORT).show()
            }
        })
        //Eventos Observer articulo
        ListaDeArticulosViewModel.ListaArticulos.observe(viewLifecycleOwner, Observer {
            try {
                originalArrayList = it as ArrayList<DTArticulo>
                adapterArticulos = ItemArticuloAdapter(ArrayList<DTArticulo>(it))
                adapterArticulos.setOnItemClickListener(object: ItemArticuloAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int) {
                        MostarDialogoAddArt(adapterArticulos.articulos[position])
                    }
                })
                binding.rvArticulos.layoutManager = LinearLayoutManager(activity)
                binding.rvArticulos.adapter = adapterArticulos
                Snackbar.make(binding.messArtLayout,"${adapterArticulos.itemCount} artículos listados.", Snackbar.LENGTH_SHORT).setAnimationMode(
                    BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                ).show()
                binding.tvCantidadArticulosListado.text="Cantidad: ${adapterArticulos.itemCount}"
                binding.viewLoading.isVisible = false
                binding.rvArticulos.isVisible = true
            }
            catch (e:Exception)
            {
                Toast.makeText(requireActivity(),"${e.message}", Toast.LENGTH_SHORT).show()
            }
        })

        //Eventos Observer
        ListaDeArticulosViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.shimmerSeleccionArt.visibility = View.VISIBLE
            else
                binding.shimmerSeleccionArt.visibility = View.GONE
        })

        //
        binding.editTextTextPersonName8.addTextChangedListener {
            if (EsRubro)
            {
                filtrarRubro(it.toString())
            }
            else
            {
                filtrarArticulo(it.toString())
            }
        }

        //Evento Observer del listado de familia
        ListaDeArticulosViewModel.ListaFamilias.observe(viewLifecycleOwner, Observer {
            try {
                //Cargar Adaptador FAMILIAS
                adapterFamilia = customSpinnerAdapter(requireContext(),ArrayList<DTFamiliaPadre>(it))
                binding.spFamilia.adapter = adapterFamilia
                //Evento seleccionar familia
                binding.spFamilia.onItemSelectedListener = object:
                    AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        //Seleccionado
                        val familia: DTFamiliaPadre = adapterFamilia.getItem(position)
                        CodigoSeleccionado = familia.codigo
                        filtarCodigo(familia.codigo)
                        if (familia != null) {
                            if (familia.subfamilias != null)
                            {
                                adapterSubFamilia = customSpinnerSubFamiliaAdapter(requireContext(),ArrayList<DTFamiliaHija>(familia.subfamilias))
                                binding.spSubFamilia.adapter = adapterSubFamilia
                                //
                            }
                        }
                    }
                }
                //Evento seleccionar subfamilia
                binding.spSubFamilia.onItemSelectedListener= object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val subfamilia: DTFamiliaHija = adapterSubFamilia.getItem(position)
                        //filtar("",codigofam + subfamilia.codigoSubFamilia)
                        CodigoSeleccionado = subfamilia.codigo
                        filtarCodigo(subfamilia.codigo)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }
            catch (e:Exception)
            {
                Toast.makeText(requireActivity(),"${e.message}", Toast.LENGTH_SHORT).show()
            }
        })
        ListaDeArticulosViewModel.mensajeDelServer.observe(viewLifecycleOwner, Observer {
            AlertView.showAlert("¡Atención!",it,requireActivity())
        })
        //Esto es para que se muestren los botones en el AppBar
        setHasOptionsMenu(true)
        return root;
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.menu_busqueda, menu)
        //Cuadro de busqueda superior
        val manager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.tvbusqueda)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    TextoBusqueda = newText
                    if (EsRubro)
                    {
                        filtrarRubro(TextoBusqueda)
                    }
                    else
                    {
                        filtrarArticulo(TextoBusqueda)
                    }
                }
                return false
            }
        }
        )
        super.onCreateOptionsMenu(menu, inflater)
        //
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
          /*  R.id.btnRecargarItems ->
            {
                CargarTodo()
            }*/
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun filtarCodigo(codigo: String) {
        try {
            if (!EsRubro)
            {
                ListaDeArticulosViewModel.CargarArticulos(100,
                    Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo,4,codigo)
            }
            else
            {
                ListaDeArticulosViewModel.CargarRubros()
            }
        }
        catch (e:Exception)
        {

        }
    }

    private fun filtrarArticulo(texto:String)
    {
        try {
            if (texto.isNotEmpty())
            {
                filtradoArrayList.clear()
                originalArrayList.forEach {
                    if(texto.isNotEmpty())
                    {
                        if(it.nombre.lowercase(Locale.getDefault()).contains(texto))
                        {
                            filtradoArrayList.add(it)
                        }
                        if(it.codigo.lowercase(Locale.getDefault()).contains(texto))
                        {
                            filtradoArrayList.add(it)
                        }
                    }
                    else
                    {
                        filtradoArrayList.add(it)
                    }
                }
                adapterArticulos.articulos = filtradoArrayList
                adapterArticulos.notifyDataSetChanged()
            }
            else
            {
                filtradoArrayList.clear()
                filtradoArrayList.addAll(originalArrayList)
                adapterArticulos.articulos = filtradoArrayList
                adapterArticulos.notifyDataSetChanged()
            }
            binding.tvCantidadArticulosListado.setText("Cantidad: " + adapterArticulos.itemCount)
        }
        catch (e:Exception)
        {

        }
    }

    private fun filtrarRubro(texto:String)
    {
        try {
            if (texto.isNotEmpty())
            {
                filtradoRubroArrayList.clear()
                originalRubroArrayList.forEach {
                    if(texto.isNotEmpty())
                    {
                        if(it.nombre.lowercase(Locale.getDefault()).contains(texto))
                        {
                            filtradoRubroArrayList.add(it)
                        }
                        if(it.codigo.lowercase(Locale.getDefault()).contains(texto))
                        {
                            filtradoRubroArrayList.add(it)
                        }
                    }
                    else
                    {
                        filtradoRubroArrayList.add(it)
                    }
                }
                adapterRubros.rubros = filtradoRubroArrayList
                adapterRubros.notifyDataSetChanged()
            }
            else
            {
                filtradoRubroArrayList.clear()
                filtradoRubroArrayList.addAll(originalRubroArrayList)
                adapterRubros.rubros = filtradoRubroArrayList
                adapterRubros.notifyDataSetChanged()
            }
            binding.tvCantidadArticulosListado.setText("Cantidad: " + adapterRubros.itemCount)
        }
        catch (e:Exception)
        {

        }
    }

    fun MostarDialogoAddArt(articulo: DTArticulo)
    {
        try {
            val dialogoAdd = AddArticuloFragment(articulo) { detalle -> onDetalleSelected(detalle!!)}
            dialogoAdd.show(parentFragmentManager,"AddArtListPos")
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun onDetalleSelected(art:DTDocDetalle)
    {
        if (art != null)
        {
            Snackbar.make(binding.messArtLayout,"${art.descripcion} agregado", Snackbar.LENGTH_SHORT)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show()
        }
    }
}