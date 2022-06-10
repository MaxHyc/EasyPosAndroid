package com.devhyc.easypos.ui.documentos

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentDocumentosBinding
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DocumentosFragment : Fragment() {

    private var _binding: FragmentDocumentosBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View?
    {
        _binding = FragmentDocumentosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //DataPickers
        binding.fDesde.setOnClickListener {
            ShowDialogPickerDesde()
        }
        binding.fHasta.setOnClickListener {
            ShowDialogPickerHasta()
        }
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)
        binding.fDesde.setText("$day/$month/$year")
        binding.fHasta.setText("$day/$month/$year")
        //Esto es para que se muestren los botones en el AppBar
        setHasOptionsMenu(true)
        return root
    }

    fun ShowDialogPickerDesde()
    {
        try {
            val datePicker = DatePickerFragment {day,month,year -> onDateDesdeSelected(day,month,year) }
            datePicker.show(parentFragmentManager,"datePicker")
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atención!",e.message,binding.root.context)
        }
    }

    fun ShowDialogPickerHasta()
    {
        try {
            val datePicker = DatePickerFragment {day,month,year -> onDateHastaSelected(day,month,year) }
            datePicker.show(parentFragmentManager,"datePicker")
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atención!",e.message,binding.root.context)
        }
    }

    @SuppressLint("SetTextI18n")
    fun onDateHastaSelected(day:Int, month:Int, year:Int)
    {
        binding.fHasta.setText("$day/$month/$year")
    }

    @SuppressLint("SetTextI18n")
    fun onDateDesdeSelected(day:Int, month:Int, year:Int)
    {
        binding.fDesde.setText("$day/$month/$year")
    }

    //Buscar con SearchView
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
                Toast.makeText(requireActivity(),"Esta buscando $query",Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(requireActivity(),"Buscando: $newText",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        )
        super.onCreateOptionsMenu(menu, inflater)
    }
}