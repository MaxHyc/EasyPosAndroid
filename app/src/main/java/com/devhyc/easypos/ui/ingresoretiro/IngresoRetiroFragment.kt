package com.devhyc.easypos.ui.ingresoretiro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.devhyc.easypos.databinding.FragmentIngresoRetiroBinding

class IngresoRetiroFragment : Fragment() {

    private var _binding: FragmentIngresoRetiroBinding? = null
    private val binding get() = _binding!!
    private var tipo:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        binding.flIngresoOk.setOnClickListener {
            when (tipo)
            {
                0 -> {
                    //val action = IngresoRetiroFragmentDirections.actionIngresoRetiroFragmentToOkFragment("Ingreso realizado con éxito")
                    //view?.findNavController()?.navigate(action)
                }
                1 -> {
                    //val action = IngresoRetiroFragmentDirections.actionIngresoRetiroFragmentToOkFragment("Retiro realizado con éxito")
                    //view?.findNavController()?.navigate(action)
                }
                2 -> {
                    //val action = IngresoRetiroFragmentDirections.actionIngresoRetiroFragmentToOkFragment("Caja iniciada con éxito")
                    //view?.findNavController()?.navigate(action)
                }
            }
        }
        return root
    }
}