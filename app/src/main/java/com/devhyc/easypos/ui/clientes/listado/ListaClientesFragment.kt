package com.devhyc.easypos.ui.clientes.listado

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentListaClientesBinding
import com.devhyc.easypos.databinding.FragmentMenuMantenimientoBinding
import com.devhyc.easypos.ui.clientes.listado.adapter.ItemClienteAdapter
import com.devhyc.easypos.ui.mediospagos.MediosDePagoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListaClientesFragment : Fragment() {

    private var _binding: FragmentListaClientesBinding? = null
    private val binding get() = _binding!!
    //ViewModel
    private lateinit var listaClienteViewModels: ListaClientesViewModel

    lateinit var dialog: AlertDialog

    lateinit var adapterClientes: ItemClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listaClienteViewModels = ViewModelProvider(this)[ListaClientesViewModel::class.java]
        _binding = FragmentListaClientesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        listaClienteViewModels.ListarClientes()
        binding.flAddNuevoCli.setOnClickListener {
            //Agregar nuevo cliente
        }
        listaClienteViewModels.ColClientes.observe(viewLifecycleOwner, Observer {
            adapterClientes = ItemClienteAdapter(ArrayList(it))
            adapterClientes.setOnItemClickListener(object: ItemClienteAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    //EVENTO CLICK
                }
            })
            binding.rvClientes.layoutManager = LinearLayoutManager(activity)
            binding.rvClientes.adapter = adapterClientes
            adapterClientes.notifyDataSetChanged()
        })
        listaClienteViewModels.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.shimmerSeleccionCli.visibility = View.VISIBLE
            else
                binding.shimmerSeleccionCli.visibility = View.GONE
        })
        return root
    }
}