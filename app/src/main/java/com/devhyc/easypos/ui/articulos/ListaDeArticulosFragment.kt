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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.databinding.FragmentListaDeArticulosBinding
import com.devhyc.easypos.ui.articulos.adapter.ArticuloAdapter
import com.devhyc.easypos.ui.articulos.adapter.RubroAdapter
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ListaDeArticulosFragment : Fragment() {

    private var _binding: FragmentListaDeArticulosBinding? = null
    private val binding get() = _binding!!
    //
    private lateinit var articulosViewModels: ListaDeArticulosViewModel
    private var originalArrayListRub: ArrayList<DTRubro> = ArrayList()
    //private lateinit var adapterArt: ArticuloAdapter
    private lateinit var adapterRub: RubroAdapter
    //
    private var tempArrayList: ArrayList<DTRubro> = ArrayList()

    override fun onPause() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = ""
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        articulosViewModels = ViewModelProvider(this).get(ListaDeArticulosViewModel::class.java)
        // Inflate the layout for this fragment
        _binding = FragmentListaDeArticulosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Buscar rubros
        articulosViewModels.ListarRubros()
        //Listado de Rubros
        articulosViewModels.rubrosModel.observe(requireActivity(), Observer {
            originalArrayListRub = it as ArrayList<DTRubro>
            adapterRub = RubroAdapter(it)
            adapterRub.setOnItemClickListener(object: RubroAdapter.onItemClickListener
            {
                override fun onItemClick(position: Int) {

                }
            })
        })
        //Carga finalizada Rubros
        articulosViewModels.cargacompletaRubros.observe(requireActivity(), Observer {
            if (it)
            {
                ListarRubros()
                binding.viewLoading.isVisible = false
                binding.rvArticulos.isVisible = true
            }
            else
            {
                MaterialAlertDialogBuilder(requireContext())
                    //.setIcon(R.drawable.bombilla)
                    .setTitle("¡Atención!")
                    .setMessage("No se pudieron listar los artículos, verifique su conexión a internet")
                    .show()
            }
        })
        //Esto es para que se muestren los botones en el AppBar
        setHasOptionsMenu(true)
        return root;
    }

    @SuppressLint("SetTextI18n")
    private fun ListarRubros()
    {
        binding.rvArticulos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticulos.adapter = adapterRub
        //Toast.makeText(requireContext(),"${adapterRub.itemCount} rubros listados.", Toast.LENGTH_SHORT).show()
        binding.tvCantidadArt.text="Cantidad: ${adapterRub.itemCount}"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Cantidad: ${adapterRub.itemCount}"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val inflater = requireActivity().menuInflater

        inflater.inflate(R.menu.menu_busqueda, menu)

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
                //
                //Toast.makeText(requireActivity(),"Esta buscando $query",Toast.LENGTH_SHORT).show()
                if (query != null) {
                    filtar(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filtar(newText)
                }
                //Toast.makeText(requireActivity(),"Buscando: $newText",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        )
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.btnRecargarItems ->
            {
                CargarTodo()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun CargarTodo()
    {
        //Cargar Articulos
        binding.viewLoading.isVisible = true
        binding.rvArticulos.isVisible = false
        articulosViewModels.ListarRubros()
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun filtar(filtroDeTexto: String) {
        try {
            tempArrayList.clear()
            if(filtroDeTexto.isNotEmpty())
            {
                originalArrayListRub.forEach{
                    //POR CODIGO
                    if(it.codigo.lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        tempArrayList.add(it)
                    }
                    //POR NOMBRE
                    if(it.nombre.toString().lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        tempArrayList.add(it)
                    }
                    //POR IMPUESTO TASA
                    if(it.impuestoTasa.toString().lowercase(Locale.getDefault()).contains(filtroDeTexto))
                    {
                        tempArrayList.add(it)
                    }
                }
                adapterRub.articulos = tempArrayList
                adapterRub.notifyDataSetChanged()
            }
            else
            {
                tempArrayList.clear()
                tempArrayList.addAll(originalArrayListRub)
                adapterRub.notifyDataSetChanged()
            }
            binding.tvCantidadArt.text="Cantidad: ${adapterRub.itemCount}"
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Cantidad: ${adapterRub.itemCount}"
        }
        catch (e:Exception)
        {

        }
    }
}