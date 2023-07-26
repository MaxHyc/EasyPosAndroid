package com.devhyc.easypos.ui.cobro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.devhyc.easypos.databinding.FragmentCobroBinding
import com.devhyc.easypos.ui.mediopago.MedioPagoFragment
import com.devhyc.easypos.utilidades.AlertView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CobroFragment : Fragment() {

    //View Binding
    private var _binding: FragmentCobroBinding? = null
    private val binding get() = _binding!!
    private lateinit var cobroViewModel: CobroFragmentViewModel
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //View Binding
        cobroViewModel = ViewModelProvider(this)[CobroFragmentViewModel::class.java]
        _binding = FragmentCobroBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Boton Agregar Pago
        binding.flAddPago.setOnClickListener {
            AbrirVentanaMedioPago()
        }
        return root
    }

    fun AbrirVentanaMedioPago()
    {
        try {
            /*val linear = View.inflate(requireContext(), R.layout.fragment_medio_pago, null) as ConstraintLayout
            AlertDialog.Builder(requireContext())
                .setTitle("Seleccione medio de pago")
                .setView(linear)
                .setIcon(R.drawable.dollar)
                .setPositiveButton("Agregar", DialogInterface.OnClickListener { dialogInterface, i ->
                    run {
                        AgregarMedioPago()
                    }
                })
                .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .setCancelable(false)
                .show()*/
            val linear = MedioPagoFragment()
            linear.show(activity!!.supportFragmentManager,"VentanaMedioPago")
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al abrir ventana de Medio de Pago",e.message,requireContext())
        }
    }

    fun AgregarMedioPago()
    {
        try {

        }
        catch (e:Exception)
        {
            AlertView.showError("Error al agregar medio de pago",e.message,requireContext())
        }
    }
}