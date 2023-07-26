package com.devhyc.easypos.ui.cabezal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocReceptor
import com.devhyc.easypos.databinding.FragmentCabezalBinding
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class CabezalFragment : Fragment() {

    private var _binding: FragmentCabezalBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentCabezalBinding.inflate(this.layoutInflater)
    }

    override fun onPause() {
        super.onPause()
        GuardarCabezal()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = binding.root
        /*binding.flGuardarCabezal.setOnClickListener {
            GuardarCabezal()
        }*/
        MostrarDatos()
        return root
    }

    fun GuardarCabezal()
    {
        try {
            if (Globales.DocumentoEnProceso != null)
            {
                Globales.DocumentoEnProceso.receptor = DTDocReceptor()
                Globales.DocumentoEnProceso.receptor!!.receptorMail = binding.etMail.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorCiudad = binding.etCiudad.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorPais = binding.etPais.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorRazon = binding.etRazon.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorRut = binding.etRut.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorDireccion = binding.etDireccion.text.toString()
                Globales.DocumentoEnProceso.receptor!!.receptorTel = binding.etTel.text.toString()
                Snackbar.make(requireView(),"Cabezal guardado", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(resources.getColor(R.color.green))
                    .show()
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("Error al guardar el cabezal",e.message,requireContext())
        }
    }

    fun MostrarDatos()
    {
        try {
            if (Globales.DocumentoEnProceso != null)
            {
                if(Globales.DocumentoEnProceso.receptor != null)
                {
                    binding.etMail.setText(Globales.DocumentoEnProceso.receptor!!.receptorMail)
                    binding.etCiudad.setText(Globales.DocumentoEnProceso.receptor!!.receptorCiudad)
                    binding.etPais.setText(Globales.DocumentoEnProceso.receptor!!.receptorPais)
                    binding.etRazon.setText(Globales.DocumentoEnProceso.receptor!!.receptorRazon)
                    binding.etRut.setText(Globales.DocumentoEnProceso.receptor!!.receptorRut)
                    binding.etDireccion.setText(Globales.DocumentoEnProceso.receptor!!.receptorDireccion)
                    binding.etTel.setText(Globales.DocumentoEnProceso.receptor!!.receptorTel)
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("Error al guardar el cabezal",e.message,requireContext())
        }
    }
}