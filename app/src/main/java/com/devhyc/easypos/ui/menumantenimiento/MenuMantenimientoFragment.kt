package com.devhyc.easypos.ui.menumantenimiento

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentMenuMantenimientoBinding
import com.devhyc.easypos.databinding.FragmentMenuPrincipalBinding
import com.devhyc.easypos.ui.menuprincipal.MenuPrincipalFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuMantenimientoFragment : Fragment() {

    private var _binding: FragmentMenuMantenimientoBinding? = null
    private val binding get() = _binding!!

    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuMantenimientoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.btnListadoArticulos.setOnClickListener {
            val action = MenuMantenimientoFragmentDirections.actionMenuMantenimientoFragmentToListaDeArticulosFragment()
            view?.findNavController()?.navigate(action)
        }
        binding.btnListadoClientes.setOnClickListener {
            val action = MenuMantenimientoFragmentDirections.actionMenuMantenimientoFragmentToListaClientesFragment()
            view?.findNavController()?.navigate(action)
        }
        binding.btnConfiguracionTerminal.setOnClickListener {
            val action = MenuMantenimientoFragmentDirections.actionMenuMantenimientoFragmentToConfiguracionDeCajaFragment()
            view?.findNavController()?.navigate(action)
        }
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.subtitle = ""
        return root
    }
}