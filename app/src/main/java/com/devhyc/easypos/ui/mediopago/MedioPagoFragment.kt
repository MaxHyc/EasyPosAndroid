package com.devhyc.easypos.ui.mediopago

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentItemDocBinding
import com.devhyc.easypos.databinding.FragmentMedioPagoBinding
import com.devhyc.easypos.ui.itemDoc.ItemDocFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedioPagoFragment : DialogFragment() {

    private var _binding: FragmentMedioPagoBinding? = null
    private val binding get() = _binding!!
    private lateinit var MedioPViewModel: MedioPagoFragmentViewModel
    //

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MedioPViewModel = ViewModelProvider(this)[MedioPagoFragmentViewModel::class.java]
        _binding = FragmentMedioPagoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Selecciona pesos por defecto
        binding.radioPesos.isChecked = true
        //Boton Cancelar
        binding.flCancelarMedio.setOnClickListener {
            dismiss()
        }
        //Boton aceptar medio de pago
        binding.flAceptarMedio.setOnClickListener {
            //Guardar medio de pago
            Toast.makeText(requireContext(),"Se agrego medio de pago",Toast.LENGTH_SHORT).show()
            dismiss()
        }
        //
        binding.etMontoParaMpagos.setOnClickListener {
            binding.etMontoParaMpagos.selectAll()
        }
        return root
    }
}