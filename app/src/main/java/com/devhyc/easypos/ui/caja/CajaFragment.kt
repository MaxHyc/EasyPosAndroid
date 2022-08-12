package com.devhyc.easypos.ui.caja

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.devhyc.easypos.databinding.FragmentCajaBinding
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CajaFragment : Fragment() {

    private var _binding: FragmentCajaBinding? = null
    private val binding get() = _binding!!
    //
    private lateinit var cajaViewModel: CajaFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cajaViewModel = ViewModelProvider(this)[CajaFragmentViewModel::class.java]
        _binding = FragmentCajaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.btnIngresoDinero.setOnClickListener {
            val action = CajaFragmentDirections.actionIngresoR(0)
            view?.findNavController()?.navigate(action)
        }
        binding.btnRetiroDinero.setOnClickListener {
            val action = CajaFragmentDirections.actionIngresoR(1)
            view?.findNavController()?.navigate(action)
        }
        binding.btnReporteX.setOnClickListener {
            val action = CajaFragmentDirections.actionReporteCaja()
            view?.findNavController()?.navigate(action)
        }
        binding.btnCierreCaja.setOnClickListener {
            val action = CajaFragmentDirections.actionCierreCaja(1)
            view?.findNavController()?.navigate(action)
        }
        binding.btnInicioCaja.setOnClickListener {
            val action = CajaFragmentDirections.actionIngresoR(2)
            view?.findNavController()?.navigate(action)
        }
        //
        cajaViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.progressCaja.isVisible = it
        })
        cajaViewModel.caja.observe(viewLifecycleOwner, Observer {
            binding.tvFechaHora.isVisible = true
            binding.tvFechaHora.text = "Apertura: ${Globales.Herramientas.convertirFechaHora(it.FechaHora)}"
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Caja N° ${it.Nro}"
        })
        cajaViewModel.mensajeDelServer.observe(viewLifecycleOwner, Observer {
            AlertView.showAlert("¡Atención!",it,requireActivity())
        })
        //
        cajaViewModel.iniciar.observe(viewLifecycleOwner, Observer {
            if (!it)
            {
                //Caja Cerrada
                binding.btnInicioCaja.visibility = View.VISIBLE
                binding.btnIngresoDinero.visibility= View.GONE
                binding.btnRetiroDinero.visibility= View.GONE
                binding.btnReporteX.visibility= View.GONE
                binding.btnCierreCaja.visibility= View.GONE
                (activity as? AppCompatActivity)?.supportActionBar?.title = "Caja no iniciada"
                binding.animationOpen.isVisible = false
                binding.animationClose.isVisible = true
            }
            else
            {
                //Caja abierta
                binding.btnInicioCaja.visibility = View.GONE
                binding.btnIngresoDinero.visibility= View.VISIBLE
                binding.btnRetiroDinero.visibility= View.VISIBLE
                binding.btnReporteX.visibility= View.VISIBLE
                binding.btnCierreCaja.visibility= View.VISIBLE
                binding.animationOpen.isVisible = true
                binding.animationClose.isVisible = false
            }
        })
        cajaViewModel.ObtenerCajaAbierta()
        return root
    }

    override fun onResume() {
        super.onResume()
        //Ver si la caja está abierta
        if (Globales.CajaActual != null)
        {

        }
        else
        {
            //No hay caja abierta

        }
    }
}