package com.devhyc.easypos.ui.configuracion

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.devhyc.easypos.databinding.FragmentConfiguracionDeCajaBinding
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class ConfiguracionDeCajaFragment : Fragment() {

    private var _binding: FragmentConfiguracionDeCajaBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentConfiguracionDeCajaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.flGuardarConfig.setOnClickListener {
            GuardarOpciones()
        }
        CargarOpciones()
        return root
    }

    private fun CargarOpciones()
    {
        try {
            binding.etNroCaja.setText(Globales.NroCaja)
            binding.etDireccionServidor.setText(Globales.DireccionServidor)
            when(Globales.ImpresionSeleccionada)
            {
                Globales.eTipoImpresora.BLUETOOTH.codigo -> {
                    binding.radioBluetooth.isChecked = true
                }
                Globales.eTipoImpresora.SUNMI.codigo -> {
                    binding.radioSunmi.isChecked = true
                }
            }
        }
        catch (e: Exception)
        {
            Toast.makeText(activity,"Error al cargar las opciones: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun GuardarOpciones()
    {
        try {
            val editor : SharedPreferences.Editor = Globales.sharedPreferences.edit()
            editor.putString("nrocaja", binding.etNroCaja.text.toString())
            editor.putString("direccionserver",binding.etDireccionServidor.text.toString())
            if (binding.radioBluetooth.isChecked)
            {
                editor.putInt("tipo_impresora",Globales.eTipoImpresora.BLUETOOTH.codigo)
                Globales.ImpresionSeleccionada = Globales.eTipoImpresora.BLUETOOTH.codigo
            }
            else
            {
                editor.putInt("tipo_impresora",Globales.eTipoImpresora.SUNMI.codigo)
                Globales.ImpresionSeleccionada = Globales.eTipoImpresora.SUNMI.codigo
            }
            editor.apply()
            //
            Globales.NroCaja = binding.etNroCaja.text.toString()
            Globales.DireccionServidor = binding.etDireccionServidor.text.toString()
            //
            Snackbar.make(requireView(),"Configuraciones guardadas",Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show()
            view?.findNavController()?.navigateUp()
        }
        catch (e: Exception)
        {
            Toast.makeText(activity,"Error al cargar las opciones: ${e.message}",Toast.LENGTH_SHORT).show()
        }
    }

}