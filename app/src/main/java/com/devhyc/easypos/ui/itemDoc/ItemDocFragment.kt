package com.devhyc.easypos.ui.itemDoc

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocItem
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.databinding.FragmentItemDocBinding
import com.devhyc.easypos.ui.articulos.adapter.RubroAdapter
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ItemDocFragment : Fragment() {

    private var _binding: FragmentItemDocBinding? = null
    private val binding get() = _binding!!
    //
    private lateinit var itemDocVm: ItemDocFragmentViewModel
    private lateinit var adapterRub: RubroAdapter
    private var originalArrayListRub: ArrayList<DTRubro> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        itemDocVm = ViewModelProvider(this)[ItemDocFragmentViewModel::class.java]
        _binding = FragmentItemDocBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //BuscarRubros
        itemDocVm.ListarRubros()
        //Loading
        itemDocVm.isLoading.observe(requireActivity(), Observer {
            binding.progressBar.isVisible =it
        })
        //Carga finalizada Rubros
        itemDocVm.cargacompletaRubros.observe(requireActivity(), Observer {
            if (it)
            {
                ListarRubros()
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
        //
        itemDocVm.rubrosModel.observe(requireActivity(), Observer {
            originalArrayListRub = it as ArrayList<DTRubro>
            adapterRub = RubroAdapter(it)
            adapterRub.setOnItemClickListener(object: RubroAdapter.onItemClickListener
            {
                override fun onItemClick(position: Int) {
                    if (binding.etMonto.text.trim().toString() != "" )
                    {
                        AgregarItem(position)
                    }
                    else
                    {
                        Snackbar.make(requireView(),"Debe ingresar un monto y despues seleccionar el rubro",Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.red)).show()
                        binding.etMonto.requestFocus()
                    }
                }
            })
        })
        //Esto es para que se muestren los botones en el AppBar
        //setHasOptionsMenu(true)
        binding.etMonto.requestFocus()
        return root
    }

    @SuppressLint("SetTextI18n")
    private fun ListarRubros()
    {
        binding.rvRubros.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRubros.adapter = adapterRub
        Toast.makeText(requireContext(),"${adapterRub.itemCount} rubros listados.", Toast.LENGTH_SHORT).show()
        binding.tvCantidadRubros.text="Cantidad: ${adapterRub.itemCount}"
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
                if (query.equals(""))
                {
                    //articulosViewModels.ListarArticulos()
                    itemDocVm.ListarRubros()
                }
                else
                {
                    //articulosViewModels.ListarArticulosFiltrado(3,query.toString())
                }
                //Toast.makeText(requireActivity(),"Resultados para: $query",Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        }
        )
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun AgregarItem(position:Int)
    {
        try {
            var cantidad:Double = 1.00
            //var descuento:String = binding.etDescuento.text.toString()
            var precio:String = binding.etMonto.text.toString()
            Globales.ItemsDeDocumento.add(DTDocItem(adapterRub.articulos[position].id,adapterRub.articulos[position].nombre,cantidad,precio.toDouble()))
            Snackbar.make(requireView(),"${adapterRub.articulos[position].nombre} $${binding.etMonto.text}",Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.green)).show()
            binding.etMonto.setText("")
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
        }
    }
}