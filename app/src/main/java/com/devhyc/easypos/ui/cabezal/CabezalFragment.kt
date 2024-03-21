package com.devhyc.easypos.ui.cabezal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTBanco
import com.devhyc.easypos.data.model.DTDocReceptor
import com.devhyc.easypos.data.model.Squareup.Country
import com.devhyc.easypos.databinding.FragmentCabezalBinding
import com.devhyc.easypos.ui.cabezal.adapter.customSpinnerPaises
import com.devhyc.easypos.ui.mediospagos.adapter.customSpinnerBancos
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.easypos.utilidades.Globales.DocumentoEnProceso
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration

@AndroidEntryPoint
class CabezalFragment : Fragment() {

    private var _binding: FragmentCabezalBinding? = null
    private val binding get() = _binding!!
    //
    private lateinit var cabezalViewModel: CabezalFragmentViewModel
    //
    private lateinit var adapterPaises: customSpinnerPaises
    //
    private var posicionPais:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        GuardarCabezal()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCabezalBinding.inflate(this.layoutInflater)
        cabezalViewModel = ViewModelProvider(this)[CabezalFragmentViewModel::class.java]
        val root: View = binding.root
        cabezalViewModel.ObtenerPaises()
        //
        cabezalViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.progressCarga.visibility = View.VISIBLE
            else
                binding.progressCarga.visibility = View.GONE
        })
        //
        cabezalViewModel.listadoPaises.observe(viewLifecycleOwner, Observer {
            adapterPaises = customSpinnerPaises(requireContext(), it)
            binding.spPaises.adapter = adapterPaises
            //Buscar el pais por defecto
            var pospais:Int=0
            adapterPaises.paises.forEach { pais ->
                if (pais.name == Globales.CiudadPorDefecto)
                {
                    binding.spPaises.setSelection(pospais)
                }
                pospais += 1
            }
            MostrarDatos()
            binding.svCabezalDoc.visibility = View.VISIBLE
        })
        binding.spPaises.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                posicionPais = position
                if (DocumentoEnProceso.receptor != null)
                {
                    //Me fijo si cambio el pais
                    if (DocumentoEnProceso.receptor!!.receptorPais != adapterPaises.getItem(position).name)
                    {
                        //Si cambió pongo la capital nueva
                        binding.etCiudad.setText(adapterPaises.getItem(position).capital)
                    }
                    else
                    {
                        //Si no cambió, me fijo si la ciudad es la misma
                        if (DocumentoEnProceso.receptor!!.receptorCiudad != "")
                        {
                            binding.etCiudad.setText(DocumentoEnProceso.receptor!!.receptorCiudad)
                        }
                        else
                        {
                            binding.etCiudad.setText(adapterPaises.getItem(position).capital)
                        }
                    }
                }
                else
                {
                    binding.etCiudad.setText(adapterPaises.getItem(position).capital)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        cabezalViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.progressPaises.isVisible = it
        })
        return root
    }

    fun GuardarCabezal()
    {
        try {
            if (DocumentoEnProceso != null)
            {
                if (binding.etRazon.text.isNotBlank() && binding.etRut.text.isNotBlank())
                {
                    DocumentoEnProceso.receptor = DTDocReceptor()
                    DocumentoEnProceso.receptor!!.receptorMail = binding.etMail.text.toString()
                    DocumentoEnProceso.receptor!!.receptorCiudad = binding.etCiudad.text.toString()
                    DocumentoEnProceso.receptor!!.receptorPais = adapterPaises.getItem(posicionPais).name
                    DocumentoEnProceso.receptor!!.receptorRazon = binding.etRazon.text.toString()
                    DocumentoEnProceso.receptor!!.receptorRut = binding.etRut.text.toString()
                    DocumentoEnProceso.receptor!!.receptorDireccion = binding.etDireccion.text.toString()
                    DocumentoEnProceso.receptor!!.receptorTel = binding.etTel.text.toString()
                    DocumentoEnProceso.cabezal!!.observaciones = binding.etObsservaciones.text.toString()
                    Snackbar.make(requireView(),"Cabezal guardado", Snackbar.LENGTH_SHORT)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setBackgroundTint(resources.getColor(R.color.green))
                        .setDuration(Snackbar.LENGTH_SHORT)
                        .setActionTextColor(resources.getColor(R.color.white))
                        .setAction("Omitir",View.OnClickListener {

                        })
                        .show()
                }
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
            if (DocumentoEnProceso != null)
            {
                if(DocumentoEnProceso.receptor != null)
                {
                    //
                    binding.etDireccion.setText(DocumentoEnProceso.receptor!!.receptorDireccion)
                    var pospais:Int=0
                    adapterPaises.paises.forEach {
                        if (it.name == DocumentoEnProceso.receptor!!.receptorPais)
                        {
                            binding.spPaises.setSelection(pospais)
                        }
                        pospais += 1
                    }
                    //
                    binding.etRazon.setText(DocumentoEnProceso.receptor!!.receptorRazon)
                    binding.etRut.setText(DocumentoEnProceso.receptor!!.receptorRut)
                    binding.etCiudad.setText(DocumentoEnProceso.receptor!!.receptorCiudad)
                    binding.etMail.setText(DocumentoEnProceso.receptor!!.receptorMail)
                    binding.etTel.setText(DocumentoEnProceso.receptor!!.receptorTel)
                    binding.etObsservaciones.setText(DocumentoEnProceso.cabezal!!.observaciones)
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("Error al mostrar datos del cabezal",e.message,requireContext())
        }
    }
}