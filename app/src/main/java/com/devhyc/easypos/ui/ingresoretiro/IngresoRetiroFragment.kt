package com.devhyc.easypos.ui.ingresoretiro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentIngresoRetiroBinding
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IngresoRetiroFragment : Fragment() {

    private var _binding: FragmentIngresoRetiroBinding? = null
    private val binding get() = _binding!!
    private var tipo:Int = 0

    //ViewModel
    private lateinit var ingresosViewModel: IngresoRetiroFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ingresosViewModel = ViewModelProvider(this)[IngresoRetiroFragmentViewModel::class.java]
        _binding = FragmentIngresoRetiroBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            tipo = bundle.getInt("TipoMovimiento",0)
            when(tipo)
            {
                0 -> (activity as? AppCompatActivity)?.supportActionBar?.title = "Ingreso de dinero"
                1 -> (activity as? AppCompatActivity)?.supportActionBar?.title = "Retiro de dinero"
                2 -> (activity as? AppCompatActivity)?.supportActionBar?.title = "Inicio de caja"
            }
        }
        //
        binding.flIngresoOk.visibility = View.VISIBLE
        //
        binding.flIngresoOk.setOnClickListener {
            if(binding.etMontoIR.text.toString() == "")
            {
                AlertView.showAlert(getString(R.string.Atencion),"El monto no puede ser vacío",requireContext())
            }
            else {
                when(tipo)
                {
                    0 ->
                    {
                        //INGRESO DE DINERO
                        //ingresosViewModel.RealizarMovimientoDeCaja(binding.etMontoIR.text.toString(),true)
                    }
                    1 ->
                    {
                        //RETIRO DE CAJA
                        //ingresosViewModel.RealizarMovimientoDeCaja(binding.etMontoIR.text.toString(),false)
                    }
                    2 ->
                    {
                        //INICIO DE CAJA
                        if (binding.etMontoIR.equals(""))
                        {
                            AlertView.showAlert("El campo está vacío","El monto debe ser distinto de vacío",requireActivity())
                        }
                        else
                        {
                            //ingresosViewModel.IniciarCaja(binding.etMontoIR.text.toString())
                        }
                    }
                }
            }
        }
        //INICIO DE CAJA
       /* ingresosViewModel.inicioCaja.observe(viewLifecycleOwner, Observer {
            Snackbar.make(requireView(),"Imprimiendo inicio de caja", Snackbar.LENGTH_LONG).setAnimationMode(
                BaseTransientBottomBar.ANIMATION_MODE_SLIDE
            ).show()
            //Globales.ControladoraDeImpresion.Print("",requireContext())
            view?.findNavController()?.navigateUp()
        })
        //INGRESO DE CAJA
        ingresosViewModel.ingresoCaja.observe(viewLifecycleOwner, Observer {
            Snackbar.make(requireView(),"Imprimiendo ingreso de caja", Snackbar.LENGTH_LONG).setAnimationMode(
                BaseTransientBottomBar.ANIMATION_MODE_SLIDE
            ).show()
            //Globales.ControladoraDeImpresion.Print("",requireContext())
            view?.findNavController()?.navigateUp()
        })
        //RETIRO DE CAJA
        ingresosViewModel.retiroCaja.observe(viewLifecycleOwner, Observer {
            Snackbar.make(requireView(),"Imprimiendo retiro de caja", Snackbar.LENGTH_LONG).setAnimationMode(
                BaseTransientBottomBar.ANIMATION_MODE_SLIDE
            ).show()
            //Globales.ControladoraDeImpresion.Print("it.activo.codigoCpcl.toString()",requireContext())
            view?.findNavController()?.navigateUp()
        })*/
        //
        ingresosViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.progressBar2.isVisible = it
        })
        return root
    }
}