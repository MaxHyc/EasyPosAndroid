package com.devhyc.easypos.ui.documentovista

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDoc
import com.devhyc.easypos.data.model.DTDocDetalle
import com.devhyc.easypos.databinding.FragmentDocumentoVistaBinding
import com.devhyc.easypos.ui.documento.adapter.ItemDocAdapter
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocumentoVistaFragment : Fragment() {

    private var oDocumento: DTDoc = DTDoc()
    private lateinit var DocumentoVistaViewModel: DocumentoVistaFragmentViewModel

    private var _binding: FragmentDocumentoVistaBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterItems: ItemDocAdapter

    private lateinit var monedaSignoSelect:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DocumentoVistaViewModel = ViewModelProvider(this)[DocumentoVistaFragmentViewModel::class.java]
        _binding = FragmentDocumentoVistaBinding.inflate(this.layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = binding.root
        setHasOptionsMenu(true)
        //OBTENER ARGUMENTOS PASADOS POR PARAMETRO
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            var terminal:String = bundle.getString("terminal","")
            var tipo:String = bundle.getString("tipoDoc","")
            var nro:Long = bundle.getLong("nroDoc",0)
            (activity as? AppCompatActivity)?.supportActionBar?.title = "Documento: " + tipo.toString()
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Nro $nro"
            //CargarDocumento
            DocumentoVistaViewModel.ObtenerDocumentoEmitido(terminal,tipo,nro.toString())
        }
        //EVENTOS
        DocumentoVistaViewModel.isLoading.observe(viewLifecycleOwner, Observer {

        })
        DocumentoVistaViewModel.MensajeServer.observe(viewLifecycleOwner, Observer {
            AlertView.showServerError("¡Atención!",it,requireContext())
        })
        DocumentoVistaViewModel.DocumentoObtenido.observe(viewLifecycleOwner, Observer {
            oDocumento = it
            CargarDatosDelDocumento()
        })
        DocumentoVistaViewModel.DocumentoCalculos.observe(viewLifecycleOwner, Observer {
            if (it != null)
            {
                binding.tvIvaVista.text = "IVA: ${it.totalImpuestos}"
                binding.tvDtoVista.text = "DTO: ${it.totalDtos}"
                binding.tvSubtotalVista.text = "SUBTOTAL: ${it.subtotal}"
                binding.tvRedondeoVista.text = "REDONDEO: ${it.redondeo}"
                binding.tvTotalVista.text = "TOTAL: ${it.total}"
            }
            else
            {
                binding.linearTotalesVista.visibility = View.GONE
            }
        })
        return root
    }

    fun CargarDatosDelDocumento()
    {
        try {
            //DATOS DEL CABEZAL
            binding.tvFechasVista.text = "Fecha: " + Globales.Herramientas.convertirFechaHora(oDocumento.cabezal!!.fecha.toString())
            if (oDocumento.cabezal!!.observaciones != "")
            {
                binding.tvObservaciones.text = "Observaciones: ${oDocumento.cabezal!!.observaciones}"
                binding.tvObservaciones.visibility = View.VISIBLE
            }
            if (oDocumento.complemento!!.lugarEntrega.isNotEmpty() && oDocumento.complemento!!.fechaEntrega!!.isNotEmpty())
            {
                binding.tvLugarDeEntregaVista.text = "Lugar de entrega: ${oDocumento.complemento!!.lugarEntrega} \n\r Fecha de entrega: " + Globales.Herramientas.convertirFechaHora(oDocumento.complemento!!.fechaEntrega.toString())
                binding.tvLugarDeEntregaVista.visibility = View.VISIBLE
            }
            //VALIDO VAROLIZADO
            if (oDocumento.valorizado != null)
            {
                //SI ES VALORIZADO
                //OBTENGO SIGNO DE MONEDA
                when(oDocumento.valorizado!!.monedaCodigo)
                {
                    "1" -> {
                        monedaSignoSelect = "$"
                    }
                    "2" -> {
                       monedaSignoSelect = "U$" + "S"
                    }
                    "" -> {
                       monedaSignoSelect = ""
                    }
                }
                if (oDocumento.valorizado!!.tipoCambio != 0.0 && oDocumento.valorizado!!.monedaCodigo.isNotEmpty())
                {
                    binding.tvTipoCambioVista.text = "Tipo de cambio: ${oDocumento.valorizado!!.tipoCambio} \n\r Moneda: ${monedaSignoSelect}"
                    binding.tvDepositoVista.visibility = View.VISIBLE
                }
                if (oDocumento.valorizado!!.listaPrecioCodigo != "")
                {
                    binding.tvListaPrecioVista.text = "Lista de precio: ${oDocumento.valorizado!!.listaPrecioCodigo}"
                    binding.tvListaPrecioVista.visibility = View.VISIBLE
                }
                //PAGOS
                if (oDocumento.valorizado!!.pagos.isNotEmpty())
                {
                    var pagosdoc:String=""
                    oDocumento.valorizado!!.pagos.forEach {
                        pagosdoc += it.medioPagoCodigo.toString()
                    }
                    binding.tvPagosVista.text = pagosdoc
                    binding.tvPagosVista.visibility = View.VISIBLE
                }
                binding.tvFormaPagoVista.text = "Forma de pago días: ${oDocumento.valorizado!!.formaPagoDias}"
                binding.tvFormaPagoVista.visibility = View.VISIBLE
            }
            else
            {
                binding.cardTotalesVista.visibility = View.GONE
            }
            //VALIDO RECEPTOR
            if (oDocumento.receptor != null)
            {
                //SI TIENE RECEPTOR
                if(oDocumento.receptor!!.receptorRut != "")
                {
                    binding.tvDatosClienteVista.text = "Cliente: ${oDocumento.receptor!!.receptorRazon} \n\r  ${oDocumento.receptor!!.clienteCodigo} \n\r"
                    binding.tvDatosClienteVista.visibility = View.VISIBLE
                }
            }
            //VALIDO COMPLEMENTO
            if(oDocumento.complemento != null)
            {
                if (oDocumento.complemento!!.funcionarioId != 0)
                {
                    binding.tvFuncionarioVista.text = "Funcionario: ${oDocumento.complemento!!.funcionarioId}"
                    binding.tvFuncionarioVista.visibility = View.VISIBLE
                }
                if (oDocumento.complemento!!.codigoDeposito != "")
                {
                    binding.tvDepositoVista.text = "Deposito: ${oDocumento.complemento!!.codigoDeposito}"
                    binding.tvDepositoVista.visibility = View.VISIBLE
                }
            }
            //VALIDO DETALLE
            if (oDocumento.detalle != null)
            {
                //SI TIENE DETALLE
                adapterItems = ItemDocAdapter(ArrayList<DTDocDetalle>(oDocumento.detalle!!))
                adapterItems.setOnItemClickListener(object: ItemDocAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {

                    }
                })
                binding.rvArticulosDocVista.layoutManager = LinearLayoutManager(activity)
                binding.rvArticulosDocVista.adapter = adapterItems
            }
            else
            {
                binding.cardArticulosVista.visibility = View.GONE
            }
            //REFERENCIAS
            if (oDocumento.referencias != null)
            {

            }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al cargar los datos del documento",e.message,requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_reimprimir,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.tvReImprimir ->
            {
                Snackbar.make(requireView(),"Acción no disponible acualmente", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(resources.getColor(R.color.rosado))
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}