package com.devhyc.easypos

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.devhyc.easypos.databinding.FragmentConfiguracionDeCajaBinding
import com.devhyc.easypos.utilidades.Globales

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
            editor.apply()
            //
            Globales.NroCaja = binding.etNroCaja.text.toString()
            Globales.DireccionServidor = binding.etDireccionServidor.text.toString()
            //
            Toast.makeText(activity,"Opciones guardadas",Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception)
        {
            Toast.makeText(activity,"Error al cargar las opciones: ${e.message}",Toast.LENGTH_SHORT).show()
        }
    }

}