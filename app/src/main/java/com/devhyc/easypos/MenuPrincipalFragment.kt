package com.devhyc.easypos

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.devhyc.easypos.databinding.FragmentMenuPrincipalBinding
import com.devhyc.easypos.utilidades.Globales
import java.util.*

class MenuPrincipalFragment : Fragment() {

    private var _binding: FragmentMenuPrincipalBinding? = null
    private val binding get() = _binding!!

    lateinit var dialog: AlertDialog

    override fun onResume() {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        try {
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.hide()
           if (Globales.UsuarioLoggueado != null)
            {
                binding.tvSaludo.text = "¡Bienvenido ${Globales.UsuarioLoggueado.nombre}!"
                binding.tvCajaActual.text = "Nro Caja: ${Globales.NroCaja}"
            }
        }
        catch (e:Exception)
        {

        }
        super.onResume()
    }

    override fun onPause() {
        try {
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.show()
        }
        catch (e:Exception)
        {

        }
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMenuPrincipalBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //NAVEGACION DEL MENU
        binding.buttonIrImpresora.setOnClickListener {
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToConexionImpresora()
            view?.findNavController()?.navigate(action)
        }
        binding.buttonIRarticulos.setOnClickListener {
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToListaDeArticulosFragment()
            view?.findNavController()?.navigate(action)
        }
        binding.buttonIrConfiguracion.setOnClickListener {
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToConfiguracionDeCajaFragment()
            view?.findNavController()?.navigate(action)
        }
        binding.buttonIrDocumentos.setOnClickListener {
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToNavDocumentosFragment()
            view?.findNavController()?.navigate(action)
        }
        binding.buttonIrMovimientosCaja.setOnClickListener {
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToNavCajaFragment()
            view?.findNavController()?.navigate(action)
        }
        binding.buttonIrPuntoDeVenta.setOnClickListener {
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToDocFragment()
            view?.findNavController()?.navigate(action)
        }
        //MOSTRAR VERSION
        binding.tvVersion.text = "HyC Hardware ${Calendar.getInstance().get(Calendar.YEAR)} - Version " + requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName
        return root
    }
}