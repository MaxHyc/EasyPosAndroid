package com.devhyc.easypos.ui.cabezal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTCliente
import com.devhyc.easypos.data.model.DTDocReceptor
import com.devhyc.easypos.databinding.FragmentCabezalBinding
import com.devhyc.easypos.ui.cabezal.adapter.customSpinnerPaises
import com.devhyc.easypos.ui.clientes.listado.ListaClientesFragment
import com.devhyc.easypos.ui.documento.DocumentoPrincipalFragment_GeneratedInjector
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.easypos.utilidades.Globales.DocumentoEnProceso
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

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
        binding.btnBuscarClienteExistente.setOnClickListener {
            MostarDialogoClientes()
        }
        binding.btnLimpiarClienteSelect.setOnClickListener {
            CargarCliente(null)
        }
        return root
    }

    fun GuardarCabezal()
    {
        try {
            if (DocumentoEnProceso != null)
            {
                if (binding.etRazon.text.isNotBlank() && binding.etNroDocCli.text.isNotBlank())
                {
                    DocumentoEnProceso.receptor = DTDocReceptor()
                    when(true)
                    {
                        binding.radioRut.isChecked -> DocumentoEnProceso.receptor!!.receptorTipoDoc = 0
                        binding.radioCI.isChecked -> DocumentoEnProceso.receptor!!.receptorTipoDoc = 1
                        binding.radioPP.isChecked -> DocumentoEnProceso.receptor!!.receptorTipoDoc = 2
                        binding.radioOtro.isChecked -> DocumentoEnProceso.receptor!!.receptorTipoDoc = 3
                        else -> DocumentoEnProceso.receptor!!.receptorTipoDoc = 0
                    }
                    if (binding.tvIdCliente.text.isNotEmpty())
                        DocumentoEnProceso.receptor!!.clienteId = binding.tvIdCliente.text.toString().toLong()
                    if (binding.tvCodCliente.text.isNotEmpty())
                        DocumentoEnProceso.receptor!!.clienteCodigo = binding.tvCodCliente.text.toString()
                    DocumentoEnProceso.receptor!!.receptorMail = binding.etMail.text.toString()
                    DocumentoEnProceso.receptor!!.receptorCiudad = binding.etCiudad.text.toString()
                    DocumentoEnProceso.receptor!!.receptorPais = adapterPaises.getItem(posicionPais).name
                    DocumentoEnProceso.receptor!!.receptorRazon = binding.etRazon.text.toString()
                    DocumentoEnProceso.receptor!!.receptorRut = binding.etNroDocCli.text.toString()
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
                    when(DocumentoEnProceso.receptor!!.receptorTipoDoc)
                    {
                        0 -> binding.radioRut.isChecked = true
                        1 -> binding.radioCI.isChecked = true
                        2 -> binding.radioPP.isChecked = true
                        3 -> binding.radioOtro.isChecked = true
                    }
                    //
                    binding.tvIdCliente.setText(DocumentoEnProceso.receptor!!.clienteId.toString())
                    binding.tvCodCliente.setText(DocumentoEnProceso.receptor!!.clienteCodigo)
                    binding.tvNombreCliente.setText(DocumentoEnProceso.receptor!!.clienteNombre)
                    binding.etRazon.setText(DocumentoEnProceso.receptor!!.receptorRazon)
                    binding.etNroDocCli.setText(DocumentoEnProceso.receptor!!.receptorRut)
                    binding.etCiudad.setText(DocumentoEnProceso.receptor!!.receptorCiudad)
                    binding.etMail.setText(DocumentoEnProceso.receptor!!.receptorMail)
                    binding.etTel.setText(DocumentoEnProceso.receptor!!.receptorTel)
                    binding.etObsservaciones.setText(DocumentoEnProceso.cabezal!!.observaciones)
                    binding.btnLimpiarClienteSelect.visibility = View.VISIBLE
                    binding.tvNombreCliente.visibility = View.VISIBLE
                }
                else
                {
                    binding.btnLimpiarClienteSelect.visibility = View.GONE
                    binding.tvNombreCliente.visibility = View.GONE
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al mostrar datos del cabezal",e.message,requireContext())
        }
    }

    fun MostarDialogoClientes()
    {
        try {
            val dialogoCliente = ListaClientesFragment {cliente -> onClienteSelected(cliente)}
            dialogoCliente.show(parentFragmentManager,"ListaCli")
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),e.message, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun onClienteSelected(cliente: DTCliente)
    {
        CargarCliente(cliente)
    }

    fun CargarCliente(cliente: DTCliente?)
    {
        try {
            if (cliente != null)
            {
                when(cliente.tipoDocumento)
                {
                    0 -> binding.radioRut.isChecked = true
                    1 -> binding.radioCI.isChecked = true
                    2 -> binding.radioPP.isChecked = true
                    3 -> binding.radioOtro.isChecked = true
                }
                binding.tvIdCliente.text = cliente.id.toString()
                binding.tvCodCliente.text = cliente.codigo
                binding.tvNombreCliente.visibility = View.VISIBLE
                binding.tvNombreCliente.setText(cliente.nombre)
                binding.etRazon.setText(cliente.razonSocial)
                binding.etNroDocCli.setText(cliente.documento)
                binding.etDireccion.setText(cliente.direccion)
                binding.etCiudad.setText(cliente.ciudad)
                binding.etMail.setText(cliente.EnviarFacturaMail)
                binding.chkEnviarFacturaPorMail.isChecked = cliente.EnviarFactura
                binding.etTel.setText(cliente.telefono)
                binding.btnLimpiarClienteSelect.visibility = View.VISIBLE
                binding.llidcli.visibility = View.VISIBLE
            }
            else
            {
                DocumentoEnProceso.receptor = null
                binding.btnLimpiarClienteSelect.visibility = View.GONE
                binding.etCiudad.setText("")
                binding.etDireccion.setText("")
                binding.etMail.setText("")
                binding.etRazon.setText("")
                binding.etTel.setText("")
                binding.etNroDocCli.setText("")
                binding.etObsservaciones.setText("")
                binding.tvNombreCliente.visibility = View.GONE
                binding.llidcli.visibility =View.GONE
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al cargar datos del cliente",e.message,requireContext())
        }
    }
}