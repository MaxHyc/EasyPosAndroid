package com.devhyc.easypos.ui.configuracion

import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentConfiguracionDeCajaBinding
import com.devhyc.easypos.utilidades.AlertView
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
            when(Globales.ImpresionSeleccionada)
            {
                Globales.eTipoImpresora.BLUETOOTH.codigo -> {
                    binding.radioBluetooth.isChecked = true
                }
                Globales.eTipoImpresora.FISERV.codigo -> {
                    binding.radioFiservPrint.isChecked = true
                }
            }
            binding.etTiempoEntreImpresion.setText(Globales.TiempoEntreImpresion.toString())
        }
        catch (e: Exception)
        {
            AlertView.showError("Error al guardar las opciones",e.message,activity)
        }
    }

    private fun GuardarOpciones()
    {
        try {
            val editor : SharedPreferences.Editor = Globales.sharedPreferences.edit()
            if (binding.radioBluetooth.isChecked)
            {
                editor.putInt("tipo_impresora",Globales.eTipoImpresora.BLUETOOTH.codigo)
                Globales.ImpresionSeleccionada = Globales.eTipoImpresora.BLUETOOTH.codigo
            }
            else
            {
                editor.putInt("tipo_impresora",Globales.eTipoImpresora.FISERV.codigo)
                Globales.ImpresionSeleccionada = Globales.eTipoImpresora.FISERV.codigo
            }
            Globales.TiempoEntreImpresion = binding.etTiempoEntreImpresion.text.toString().toInt()
            editor.putInt("tiempo_entre_impresion",Globales.TiempoEntreImpresion)
            editor.apply()
            //
            Snackbar.make(requireView(),"Configuraciones guardadas",Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show()
            view?.findNavController()?.navigateUp()
        }
        catch (e: Exception)
        {
            AlertView.showError("Error al guardar las opciones",e.message,activity)
        }
    }

}