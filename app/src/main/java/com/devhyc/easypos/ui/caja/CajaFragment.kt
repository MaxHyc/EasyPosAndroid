package com.devhyc.easypos.ui.caja

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.devhyc.easypos.databinding.FragmentCajaBinding
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CajaFragment : Fragment() {

    private var _binding: FragmentCajaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            val action = CajaFragmentDirections.actionReporteCierre(0)
            view?.findNavController()?.navigate(action)
        }
        binding.btnCierreCaja.setOnClickListener {
            val action = CajaFragmentDirections.actionReporteCierre(1)
            view?.findNavController()?.navigate(action)
        }
        binding.btnInicioCaja.setOnClickListener {
            val action = CajaFragmentDirections.actionIngresoR(2)
            view?.findNavController()?.navigate(action)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        //Ver si la caja está abierta
        if (Globales.CajaActual != null)
        {
            //Existe caja abierta
            binding.btnInicioCaja.visibility = View.GONE
            binding.btnIngresoDinero.visibility= View.VISIBLE
            binding.btnRetiroDinero.visibility= View.VISIBLE
            binding.btnReporteX.visibility= View.VISIBLE
            binding.btnCierreCaja.visibility= View.VISIBLE
        }
        else
        {
            //No hay caja abierta
            binding.btnInicioCaja.visibility = View.VISIBLE
            binding.btnIngresoDinero.visibility= View.GONE
            binding.btnRetiroDinero.visibility= View.GONE
            binding.btnReporteX.visibility= View.GONE
            binding.btnCierreCaja.visibility= View.GONE
        }
    }
}