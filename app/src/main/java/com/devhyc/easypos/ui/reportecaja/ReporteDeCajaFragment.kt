package com.devhyc.easypos.ui.reportecaja

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTCajaEstado
import com.devhyc.easypos.data.model.DTCajaNroDocumentos
import com.devhyc.easypos.databinding.FragmentReporteDeCajaBinding
import com.devhyc.easypos.ui.documento.DocumentoPrincipalFragmentDirections
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
    //
    private var estadoCaja: DTCajaEstado? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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
            binding.tvCajaFechaHoraActual.text = Globales.Herramientas.TransformarFecha(it.Cabezal.FechaHoraActual,Globales.FechaJson,Globales.Fecha_dd_MM_yyyy_HH_mm_ss)
            binding.tvCajaHoraApertura.text = Globales.Herramientas.TransformarFecha(it.Cabezal.FechaHora,Globales.FechaJson,Globales.Fecha_dd_MM_yyyy_HH_mm_ss)
            if (it.Cabezal.FechaHoraCierre != null)
            {
                binding.tvCajaHoraCierre.text = Globales.Herramientas.TransformarFecha(it.Cabezal.FechaHoraCierre,Globales.FechaJson,Globales.Fecha_dd_MM_yyyy_HH_mm_ss)
            }
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
            estadoCaja = it
            binding.svReporte.visibility = View.VISIBLE
        })
        reporteViewModel.impresionReporte.observe(viewLifecycleOwner, Observer {
            Globales.ControladoraFiservPrint.Print(it,requireContext())
        })
        reporteViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.progressBar5.visibility = View.VISIBLE
            else
                binding.progressBar5.visibility = View.GONE
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_reporte,menu)
        menu.findItem(R.id.btn_menu_ImprimirReporte).isVisible = true
        menu.findItem(R.id.btn_menu_RealizarCierre).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.btn_menu_ImprimirReporte ->
            {
                if (estadoCaja != null)
                    reporteViewModel.ImpresionReporte(estadoCaja!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}