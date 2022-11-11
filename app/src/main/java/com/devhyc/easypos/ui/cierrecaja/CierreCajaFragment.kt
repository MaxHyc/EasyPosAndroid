package com.devhyc.easypos.ui.cierrecaja

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTTotalesDeclarados
import com.devhyc.easypos.databinding.FragmentCierreCajaBinding
import com.devhyc.easypos.ui.caja.CajaFragmentViewModel
import com.devhyc.easypos.utilidades.AlertView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CierreCajaFragment : Fragment() {

    private var _binding: FragmentCierreCajaBinding? = null
    private val binding get() = _binding!!
    private var tipo:Int = 0
    //
    private lateinit var cierreViewModel: CierreCajaFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cierreViewModel = ViewModelProvider(this)[CierreCajaFragmentViewModel::class.java]
        _binding = FragmentCierreCajaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            tipo = bundle.getInt("Tipo",0)
        }
        //
        binding.flCerrarCaja.setOnClickListener {
            RealizarCierre()
        }
        cierreViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.progressBar4.isVisible = it
        })
        cierreViewModel.caja.observe(viewLifecycleOwner, Observer {
            //SI CERRO LA CAJA ENTRA ACA
        })
        //
        return root
    }

    fun RealizarCierre()
    {
        try {
            val totalpesos: Double = if (binding.etTotalPesos.text.toString() != "") binding.etTotalPesos.text.toString().toDouble()  else 0.0
            val totalDolares: Double = if (binding.etTotalDolares.text.toString() != "") binding.etTotalDolares.text.toString().toDouble()  else 0.0
            val totalTarjetas: Double = if (binding.etTotalTarjetas.text.toString() != "") binding.etTotalTarjetas.text.toString().toDouble()  else 0.0
            val totalTarjetasDolares: Double = if (binding.etTotalTarjetasDolares.text.toString() != "") binding.etTotalTarjetasDolares.text.toString().toDouble()  else 0.0
            val totalCheques: Double = if (binding.etTotalCheques.text.toString() != "") binding.etTotalCheques.text.toString().toDouble()  else 0.0
            val totalChequesDolares: Double = if (binding.etTotalChequesDolares.text.toString() != "") binding.etTotalChequesDolares.text.toString().toDouble()  else 0.0
            val totalCreditos: Double = if (binding.etTotalCreditos.text.toString() != "") binding.etTotalCreditos.text.toString().toDouble()  else 0.0
            val totalTickets: Double = if (binding.etTotalTickets.text.toString() != "") binding.etTotalTickets.text.toString().toDouble()  else 0.0
            val totalesDeclarados = DTTotalesDeclarados(totalpesos,totalDolares, totalTarjetas, totalTarjetasDolares, totalCheques, totalChequesDolares, totalCreditos, totalTickets)
            cierreViewModel.CerrarCaja(totalesDeclarados)
        }
        catch (e:Exception)
        {
            AlertView.showError(getString(R.string.Error),e.message,requireContext())
        }
    }
}