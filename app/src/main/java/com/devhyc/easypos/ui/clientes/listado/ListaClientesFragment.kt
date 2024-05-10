package com.devhyc.easypos.ui.clientes.listado

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTCliente
import com.devhyc.easypos.databinding.FragmentListaClientesBinding
import com.devhyc.easypos.databinding.FragmentMenuMantenimientoBinding
import com.devhyc.easypos.ui.clientes.listado.adapter.ItemClienteAdapter
import com.devhyc.easypos.ui.mediospagos.MediosDePagoViewModel
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListaClientesFragment(val listener: (cliente: DTCliente) -> Unit) : DialogFragment() {

    private var _binding: FragmentListaClientesBinding? = null
    private val binding get() = _binding!!
    //ViewModel
    private lateinit var listaClienteViewModels: ListaClientesViewModel

    lateinit var adapterClientes: ItemClienteAdapter
    //
    private var originalArrayList: ArrayList<DTCliente> = ArrayList()
    private var filtradoArrayList: ArrayList<DTCliente> = ArrayList()
    //

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

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
        //
        binding.etBuscarClienteDesc.addTextChangedListener {
            filtrarCliente(it.toString())
        }
        dialog!!.setCancelable(false)
        binding.btnCancelarSeleccionCliente.setOnClickListener {
            dialog!!.dismiss()
        }
        //
        listaClienteViewModels.ListarClientes()
        listaClienteViewModels.ColClientes.observe(viewLifecycleOwner, Observer {
            originalArrayList = it as ArrayList<DTCliente>
            adapterClientes = ItemClienteAdapter(ArrayList(it))
            adapterClientes.setOnItemClickListener(object: ItemClienteAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
//                    Globales.ClienteSeleccionado = adapterClientes.clientes[position]
                    listener(adapterClientes.clientes[position])
                    dialog!!.dismiss()
                }
            })
            binding.rvListadoClientes.layoutManager = LinearLayoutManager(activity)
            binding.rvListadoClientes.adapter = adapterClientes
            adapterClientes.notifyDataSetChanged()
        })
        listaClienteViewModels.isLoading.observe(viewLifecycleOwner, Observer {
            binding.progressClientes.isVisible = it
        })
        return root
    }

    private fun filtrarCliente(texto:String)
    {
        try {
            if (texto.isNotEmpty())
            {
                filtradoArrayList.clear()
                originalArrayList.forEach {
                    if(texto.isNotEmpty())
                    {
                        var s = "${it.nombre},${it.razonSocial},${it.codigo},${it.direccion},${it.ciudad}".lowercase()
                        if (s.contains(texto.lowercase()))
                        {
                            filtradoArrayList.add(it)
                        }
                    }
                    else
                    {
                        filtradoArrayList.add(it)
                    }
                }
                adapterClientes.clientes = filtradoArrayList
                adapterClientes.notifyDataSetChanged()
            }
            else
            {
                filtradoArrayList.clear()
                filtradoArrayList.addAll(originalArrayList)
                adapterClientes.clientes = filtradoArrayList
                adapterClientes.notifyDataSetChanged()
            }
        }
        catch (e:Exception)
        {

        }
    }

}