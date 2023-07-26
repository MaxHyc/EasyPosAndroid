package com.devhyc.easypos.ui.menuprincipal

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.devhyc.easypos.BuildConfig
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentMenuPrincipalBinding
import com.devhyc.easypos.ui.login.LoginActivity
import com.devhyc.easypos.utilidades.Globales
import java.util.*

class MenuPrincipalFragment : Fragment() {

    private var _binding: FragmentMenuPrincipalBinding? = null
    private val binding get() = _binding!!

    lateinit var dialog: AlertDialog

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        Globales.EnPrincipal = true
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        try {
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.hide()
           if (Globales.UsuarioLoggueado != null)
            {
               if (BuildConfig.VERSION_TESTING)
                    binding.tvSaludo.text = "TESTING (${Globales.UsuarioLoggueado.nombre})"
               else
                    binding.tvSaludo.text = "¡Bienvenido ${Globales.UsuarioLoggueado.nombre}!"

                if (Globales.Terminal != null)
                    binding.tvCajaActual.text = Globales.Terminal.Descripcion
                else
                    binding.tvCajaActual.text = "Caja: ${Globales.NroCaja}"
            }
            if (Globales.CajaActual != null)
                binding.tvInfoAbierto.text = "Caja abierta: ${Globales.Herramientas.convertirFechaHora(Globales.CajaActual.FechaHora.toString())} \n Nro: ${Globales.CajaActual.Nro} \n Usuario que inició: ${Globales.CajaActual.Usuario}"
            else
                binding.tvInfoAbierto.text= ""
        }
        catch (e:Exception)
        {

        }
        super.onResume()
    }

    override fun onPause() {
        try {
            Globales.EnPrincipal = false
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
        Globales.EnPrincipal = true
        //Cerrar sesion
        binding.btnCerrarSesion.setOnClickListener { DialogoCerrarSesion() }
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
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToListaDeDocumentosFragment()
            view?.findNavController()?.navigate(action)
        }
        binding.buttonIrMovimientosCaja.setOnClickListener {
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToNavCajaFragment()
            view?.findNavController()?.navigate(action)
        }
        binding.buttonIrPuntoDeVenta.setOnClickListener {
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToDocumentoPrincipalFragment()
            view?.findNavController()?.navigate(action)
        }
        binding.btnIntegradores.setOnClickListener {
            val action = MenuPrincipalFragmentDirections.actionMenuPrincipalFragmentToMasOpcionesFragment()
            view?.findNavController()?.navigate(action)
        }
        //MOSTRAR VERSION
        binding.tvVersion.text = "HyC Hardware ${Calendar.getInstance().get(Calendar.YEAR)} - Version " + requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName
        return root
    }

    fun DialogoCerrarSesion()
    {
        dialog= AlertDialog.Builder(requireActivity())
            .setIcon(R.drawable.atencion)
            .setTitle("¡Atención!")
            .setMessage("¿Desea cerrar la sesión actual?")
            .setPositiveButton("Si", DialogInterface.OnClickListener {
                    dialogInterface, i ->
                run {
                    Globales.UsuarioLoggueado = null
                    Globales.Deposito = null
                    GuardarEstadoLogin("","",false)
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                }
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialogInterface, i -> dialog.dismiss()
            })
            .setCancelable(true)
            .setOnCancelListener { "Cancelar" }
            .show()
    }

    fun GuardarEstadoLogin(user:String,password:String,valorSesion:Boolean)
    {
        val editor = Globales.sharedPreferences.edit()
        editor.putString("usuarioanterior",user)
        editor.putString("passanterior",password)
        editor.putBoolean("sesionviva",valorSesion)
        editor.commit()
        Globales.SesionViva = false
        Globales.UsuarioAnterior = ""
        Globales.PassAnterior = ""
    }
}