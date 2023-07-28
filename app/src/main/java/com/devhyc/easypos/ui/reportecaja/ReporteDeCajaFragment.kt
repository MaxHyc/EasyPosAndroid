package com.devhyc.easypos.ui.reportecaja

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.data.model.DTCajaNroDocumentos
import com.devhyc.easypos.databinding.FragmentReporteDeCajaBinding
import com.devhyc.easypos.ui.reportecaja.adapter.ItemNrosDocumentos
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReporteDeCajaFragment : Fragment() {

    private var _binding: FragmentReporteDeCajaBinding? = null
    private val binding get() = _binding!!
    //
    private var nroCaja:String = ""
    private lateinit var reporteViewModel: ReporteDeCajaFragmentViewModel
    //
    private lateinit var adapterNrosDocumentos: ItemNrosDocumentos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reporteViewModel = ViewModelProvider(this)[ReporteDeCajaFragmentViewModel::class.java]
        _binding = FragmentReporteDeCajaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            nroCaja = bundle.getString("NroCaja","")
            reporteViewModel.EstadoDeCaja(nroCaja.toString())
        }


        reporteViewModel.estado.observe(viewLifecycleOwner, Observer {
            //Obtener caja estado
            (activity as? AppCompatActivity)?.supportActionBar?.title = it.Nombre
            //
            binding.linearHoraCierre.isVisible = it.Cabezal.EsCierre
            //
            binding.tvCajaFechaHoraActual.text = Globales.Herramientas.convertirFechaHora(it.Cabezal.FechaHoraActual)
            binding.tvCajaHoraApertura.text = Globales.Herramientas.convertirFechaHora(it.Cabezal.FechaHora)
            binding.tvCajaHoraCierre.text = Globales.Herramientas.convertirFechaHora(it.Cabezal.FechaHoraCierre)
            binding.tvCajaNro.text = it.Cabezal.NroCaja.toString()
            binding.tvCajaUsuarioLogueado.text = it.Cabezal.UsuarioLogueado
            binding.tvCajaUsuario.text = it.Cabezal.UsuarioCaja
            binding.tvCajaTerminalCodigo.text = it.Cabezal.TerminalCodigo
            binding.tvCajaTotalIvaDia.text = it.Cabezal.TotalIvaDia.toString()
            binding.tvCajaTotalVentaDia.text = it.Cabezal.TotalVentaDia.toString()
            binding.tvCajaTotalVentaDiaMe.text = it.Cabezal.TotalVentaDiaME.toString()
            binding.tvCajaVentaIvaBas.text = it.Cabezal.VentasIvaBas.toString()
            binding.tvCajaVentaIvaMin.text = it.Cabezal.VentasIvaMin.toString()
            binding.tvCajaVentasExenta.text = it.Cabezal.VentasExenta.toString()
            CargarNrosDocumentos(it.Cabezal.NrosDocumentos)
            //
        })


        return root
    }

    private fun CargarNrosDocumentos(listado:List<DTCajaNroDocumentos>)
    {
        try {
            //Cuando termina de cargar
            adapterNrosDocumentos = ItemNrosDocumentos(ArrayList<DTCajaNroDocumentos>(listado))
            adapterNrosDocumentos.setOnItemClickListener(object: ItemNrosDocumentos.onItemClickListener{
                override fun onItemClick(position: Int) {

                }
            })
            binding.rvNroDeDocumentos.layoutManager = LinearLayoutManager(activity)
            binding.rvNroDeDocumentos.adapter = adapterNrosDocumentos
        }
        catch (e:Exception)
        {

        }
    }
}