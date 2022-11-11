package com.devhyc.easypos.ui.listamediopago

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.databinding.FragmentListaDeArticulosBinding
import com.devhyc.easypos.databinding.FragmentListaMediosPBinding
import com.devhyc.easypos.databinding.FragmentMedioPagoBinding
import com.devhyc.easypos.ui.documento.DocFragmentDirections
import com.devhyc.easypos.ui.mediopago.MedioPagoFragmentViewModel
import com.devhyc.easypos.ui.mediopago.adapter.ItemMedioPago
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListaMediosPFragment : Fragment() {

    private var _binding: FragmentListaMediosPBinding? = null
    private val binding get() = _binding!!
    private lateinit var ListaMedioPViewModel: ListaMediosPFragmentViewModel
    //
    private lateinit var adapterMediosDePagos: ItemMedioPago

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        ListaMedioPViewModel = ViewModelProvider(this)[ListaMediosPFragmentViewModel::class.java]
        _binding = FragmentListaMediosPBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //
        ListaMedioPViewModel.ListarMediosDePago()
        ListaMedioPViewModel.LMedioPago.observe(viewLifecycleOwner, Observer {
            //Cuando termina de cargar
            adapterMediosDePagos = ItemMedioPago(ArrayList<DTMedioPago>(it))
            adapterMediosDePagos.setOnItemClickListener(object: ItemMedioPago.onItemClickListener{
                override fun onItemClick(position: Int) {
                    //AL TOCAR UN MEDIO DE PAGO
                    //val action = ListaMediosPFragmentDirections.actionListaMedioToCobro()
                    //view?.findNavController()?.navigate(action)
                    adapterMediosDePagos.notifyDataSetChanged()
                }
            })
            binding.rvTodosMp.layoutManager = LinearLayoutManager(activity)
            binding.rvTodosMp.adapter = adapterMediosDePagos
        })
        //
        return root
    }
}