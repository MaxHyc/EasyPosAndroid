package com.devhyc.easypos.ui.documentovista

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDoc
import com.devhyc.easypos.data.model.DTDocDetalle
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.databinding.FragmentDocumentoVistaBinding
import com.devhyc.easypos.ui.documento.adapter.ItemDocAdapter
import com.devhyc.easypos.ui.mediospagoslite.MediosPagosLiteFragmentDirections
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocumentoVistaFragment : Fragment() {

    private var oDocumento:DTDoc = DTDoc()
    private lateinit var DocumentoVistaViewModel: DocumentoVistaFragmentViewModel

    private var _binding: FragmentDocumentoVistaBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterItems: ItemDocAdapter

    private lateinit var monedaSignoSelect:String

    private lateinit var listaMediosPagos:ArrayList<DTMedioPago>
    //
    private lateinit var terminal:String
    private lateinit var tipo:String
    private var nro:Long=0

    override fun onDestroy() {
        super.onDestroy()
        Globales.DocumentoEnProceso = null
        Globales.TotalesDocumento = null
    }

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
        //LISTAR MEDIOS DE PAGO
        DocumentoVistaViewModel.LMedioPago.observe(viewLifecycleOwner, Observer {
            listaMediosPagos = it.toCollection(ArrayList())
            //CARGAR DOCUMENTO
            DocumentoVistaViewModel.ObtenerDocumentoEmitido(terminal,tipo,nro.toString())
        })
        //
        //OBTENER ARGUMENTOS PASADOS POR PARAMETRO
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            terminal = bundle.getString("terminal","")
            tipo= bundle.getString("tipodoc","")
            nro= bundle.getLong("nrodoc",0)
            (activity as? AppCompatActivity)?.supportActionBar?.title = tipo.toString()
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Nro $nro"
            //CargarDocumento
            DocumentoVistaViewModel.ListarMediosDePago()
            //DocumentoVistaViewModel.ObtenerDocumentoEmitido(terminal,tipo,nro.toString())
        }
        DocumentoVistaViewModel.Impresion.observe(viewLifecycleOwner, Observer {
            Globales.ControladoraFiservPrint.Print(it,requireContext())
        })
        DocumentoVistaViewModel.MensajeServer.observe(viewLifecycleOwner, Observer {
            AlertView.showServerError("¡Atención!",it,requireContext())
        })
        DocumentoVistaViewModel.DocumentoObtenido.observe(viewLifecycleOwner, Observer {
            Globales.DocumentoEnProceso = it
            oDocumento = it
            CargarDatosDelDocumento()
            binding.svDocumentoVista.visibility = View.VISIBLE
            setHasOptionsMenu(true)
        })
        DocumentoVistaViewModel.DocumentoCalculos.observe(viewLifecycleOwner, Observer {
            if (it != null)
            {
                Globales.TotalesDocumento = it
                binding.tvIvaVista.text = "IVA: ${it.totalImpuestos}"
                binding.tvDtoVista.text = "DTO: ${it.totalDtos}"
                binding.tvSubtotalVista.text = "SUBTOTAL: ${it.subtotal}"
                binding.tvRedondeoVista.text = "REDONDEO: ${it.redondeo}"
                binding.tvTotalVista.text = "TOTAL: $monedaSignoSelect ${it.total}"
            }
            else
            {
                binding.linearTotalesVista.visibility = View.GONE
            }
        })
        DocumentoVistaViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if(it)
                binding.progressBar6.visibility = View.VISIBLE
            else
                binding.progressBar6.visibility = View.GONE
        })
        return root
    }

    fun CargarDatosDelDocumento()
    {
        try {
            //DATOS DEL CABEZAL
            binding.tvFechasVista.text = "Fecha: " + Globales.Herramientas.TransformarFecha(oDocumento.cabezal!!.fecha.toString(),Globales.FechaJson,Globales.Fecha_dd_MM_yyyy_HH_mm_ss)
            if (oDocumento.cabezal!!.observaciones != "")
            {
                binding.tvObservaciones.text = "Observaciones: ${oDocumento.cabezal!!.observaciones}"
                binding.tvObservaciones.visibility = View.VISIBLE
            }
            if (oDocumento.complemento!!.lugarEntrega.isNotEmpty() && oDocumento.complemento!!.fechaEntrega!!.isNotEmpty())
            {
                binding.tvLugarDeEntregaVista.text = "Lugar de entrega: ${oDocumento.complemento!!.lugarEntrega} \n\r Fecha de entrega: " + Globales.Herramientas.TransformarFecha(oDocumento.complemento!!.fechaEntrega.toString(),Globales.FechaJson,Globales.Fecha_dd_MM_yyyy_HH_mm_ss)
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
                    binding.tvTipoCambioVista.text = "Tipo de cambio: ${oDocumento.valorizado!!.tipoCambio}\nMoneda: ${monedaSignoSelect}"
                    binding.tvTipoCambioVista.visibility = View.VISIBLE
                }
                if (oDocumento.valorizado!!.listaPrecioCodigo != "")
                {
                    binding.tvListaPrecioVista.text = "Lista de precio: ${oDocumento.valorizado!!.listaPrecioCodigo}"
                    binding.tvListaPrecioVista.visibility = View.VISIBLE
                }
                //PAGOS
                if (oDocumento.valorizado!!.pagos.isNotEmpty())
                {
                    //SI TIENE PAGOS ASOCIADOS
                    var pagosdoc:String=""
                    oDocumento.valorizado!!.pagos.forEach { pago ->
                        pagosdoc += listaMediosPagos.find { pago.medioPagoCodigo.toString() == it.Id }!!.Nombre
                    }
                    binding.tvPagosVista.text = pagosdoc
                    binding.tvPagosVista.visibility = View.VISIBLE
                }
                else
                {
                    //SI NO TIENE PAGOS ASOCIADOS
                    binding.linearPagosVista.visibility = View.GONE
                }
                binding.tvFormaPagoVista.text = "Forma de pago días: ${oDocumento.valorizado!!.formaPagoDias}"
                binding.tvFormaPagoVista.visibility = View.VISIBLE
                //CALCULAR DOCUMENTO
                DocumentoVistaViewModel.CalcularDoc(oDocumento)
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
                    //binding.tvTituloCabezal.visibility = View.GONE
                    binding.tvDatosClienteVista.text = "Cliente: ${oDocumento.receptor!!.receptorRazon} (${oDocumento.receptor!!.clienteCodigo})"
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
            if (oDocumento.referencias!!.isNotEmpty())
            {
                //TERMINAL TIPO NRO TOTAL
                binding.linearReferencias.visibility = View.VISIBLE
                var ref:String = ""
                oDocumento.referencias!!.forEach {
                    ref += "Terminal: ${it.terminalCodigo} | TipoDoc: ${it.tipoDocCodigo} | Nro: ${it.nroDocumento} | Total: ${it.total} \r\n"
                }
                binding.tvRefInfo.text =ref
            }
            else
            {
                binding.linearReferencias.visibility = View.GONE
            }
            //SI ES DEVOLUCION QUE NO MUESTRE EL BOTON DE DEVOLVER
            /*  val devolverTickItem = menu.findItem(R.id.tvDevolvertick)
            if (oDocumento.cabezal!!.tipoDocCodigo == Globales.Terminal.Documentos.DevContado)
            {
                devolverTickItem.isVisible = false
            }*/
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al cargar los datos del documento",e.message,requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_reimprimir,menu)
        menu.findItem(R.id.tvDevolvertick).isVisible = oDocumento.cabezal!!.tipoDocCodigo != Globales.Terminal.Documentos.DevContado
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.tvReImprimir ->
            {
                DocumentoVistaViewModel.ObtenerImpresion(oDocumento.cabezal!!.terminal,oDocumento.cabezal!!.tipoDocCodigo,oDocumento.cabezal!!.nroDoc)
            }
            R.id.tvDevolvertick ->
            {
                val action = DocumentoVistaFragmentDirections.actionDocumentoVistaFragmentToMediosPagosLiteFragment(true)
                view?.findNavController()?.navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}