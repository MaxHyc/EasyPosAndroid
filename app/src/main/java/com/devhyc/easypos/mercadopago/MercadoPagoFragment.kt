package com.devhyc.easypos.mercadopago

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
        //No dejar abrir el Drawer
        Globales.Herramientas.VistaDeDrawerLauout(requireActivity(),false)
        //Ocultar boton de ir atras
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.subtitle = ""
        //
        mpViewModel = ViewModelProvider(this)[MercadoPagoViewModel::class.java]
        _binding = FragmentMercadoPagoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //INTERACCION DE INTERFAZ DE USUARIO
        binding.btnCancelarMp.setOnClickListener {
            mpViewModel.cancelarOrdenActual()
        }

        //REFRESCAR VISTAS
        mpViewModel.ImagenQr.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.imgQrMp.visibility = View.VISIBLE
            Glide.with(requireView())
                .load(it)
                .into(binding.imgQrMp)
        })
        mpViewModel.BotonCancelar.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it)
                binding.btnCancelarMp.visibility = View.VISIBLE
            else
                binding.btnCancelarMp.visibility = View.GONE
        })
        mpViewModel.mensajeDelServer.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            AlertView.showError("¡Atención!",it,requireContext())
        })
        mpViewModel.EstadoDescripcion.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvMensajeMp.text = it
        })
        mpViewModel.PagoFinalizado.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val bundle = Bundle().apply {
                putParcelable("resultadoPago",it as? Parcelable)
            }
            parentFragmentManager.setFragmentResult("resultadoKey",bundle)
            findNavController().popBackStack()
        })
        mpViewModel.MensajeEscaneo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvMsjEscaneo.text = it
        })
        //
        mpViewModel.CrearOrden()
        //CONTROLAR EL EVENTO DE BACK
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return root
    }
}