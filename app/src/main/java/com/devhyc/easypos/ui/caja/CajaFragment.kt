package com.devhyc.easypos.ui.caja

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentCajaBinding
import com.devhyc.easypos.ui.login.LoginActivity
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CajaFragment : Fragment() {

    private var _binding: FragmentCajaBinding? = null
    private val binding get() = _binding!!
    private var ncaja:String=""
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
            //AbrirFragmentInicioCaja()
        }
        binding.btnRetiroDinero.setOnClickListener {
            val action = CajaFragmentDirections.actionIngresoR(1)
            view?.findNavController()?.navigate(action)
        }
        binding.btnReporteX.setOnClickListener {
            val action = CajaFragmentDirections.actionReporteCaja(ncaja)
            view?.findNavController()?.navigate(action)
        }
        binding.btnCierreCaja.setOnClickListener {
            val action = CajaFragmentDirections.actionCierreCaja(1)
            view?.findNavController()?.navigate(action)
        }
        binding.btnInicioCaja.setOnClickListener {
            val action = CajaFragmentDirections.actionIngresoR(2)
            //view?.findNavController()?.navigate(action)
            AbrirFragmentInicioCaja()
        }
        //
        cajaViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.progressCaja.isVisible = it
        })
        cajaViewModel.caja.observe(viewLifecycleOwner, Observer {
            binding.tvFechaHora.isVisible = true
            binding.tvFechaHora.text = "Apertura: ${Globales.Herramientas.TransformarFecha(it.FechaHora,Globales.FechaJson,Globales.Fecha_dd_MM_yyyy_HH_mm_ss)}"
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Caja N° ${it.Nro}"
            ncaja = it.Nro.toString()
        })
        cajaViewModel.mensajeDelServer.observe(viewLifecycleOwner, Observer {
            AlertView.showError("¡Atención!",it,requireActivity())
        })
        //
        cajaViewModel.existeCaja.observe(viewLifecycleOwner, Observer {
            if (!it)
            {
                //Caja Cerrada
                (activity as? AppCompatActivity)?.supportActionBar?.title = "Caja no iniciada"
                binding.btnInicioCaja.visibility = View.VISIBLE
                binding.btnIngresoDinero.visibility= View.GONE
                binding.btnRetiroDinero.visibility= View.GONE
                binding.btnReporteX.visibility= View.GONE
                binding.btnCierreCaja.visibility= View.GONE
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

    fun AbrirFragmentInicioCaja()
    {
        try
        {
            val inflater = layoutInflater
            val view = inflater.inflate(R.layout.fragment_ingreso_retiro, null)
            //
            var etMontoIngreso = view.findViewById<EditText>(R.id.etMontoIR)
            //
            lateinit var dialog: AlertDialog
            dialog= AlertDialog.Builder(requireContext())
                .setIcon(R.drawable.atencion)
                .setTitle("Ingrese monto de inicio de caja")
                .setView(view)
                .setPositiveButton("Aceptar") { dialogInterface, i ->
                    run {
                       //
                        cajaViewModel.IniciarCaja(etMontoIngreso.text.toString())
                    }
                }
                .setNegativeButton("Cancelar") { dialogInterface, i ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }
        catch (e:Exception)
        {

        }
    }
}