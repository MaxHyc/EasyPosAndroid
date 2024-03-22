package com.devhyc.easypos.mercadopago

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentMediosPagosLiteBinding
import com.devhyc.easypos.databinding.FragmentMercadoPagoBinding
import com.devhyc.easypos.databinding.FragmentReporteDeCajaBinding
import com.devhyc.easypos.ui.reportecaja.ReporteDeCajaFragmentViewModel
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.*

@AndroidEntryPoint
class MercadoPagoFragment : Fragment() {

    private var _binding: FragmentMercadoPagoBinding? = null
    private val binding get() = _binding!!
    private lateinit var mpViewModel: MercadoPagoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mpViewModel = ViewModelProvider(this)[MercadoPagoViewModel::class.java]
        _binding = FragmentMercadoPagoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //REFRESCAR VISTAS
        mpViewModel.ImagenQr.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Glide.with(requireView())
                .load(it)
                .into(binding.imgQrMp)
        })
        mpViewModel.mensajeDelServer.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            AlertView.showError("¡Atención!",it,requireContext())
        })
        mpViewModel.EstadoDescripcion.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvMensajeMp.text = it
        })
        //
        mpViewModel.CrearOrden()
        //
        return root
    }


}