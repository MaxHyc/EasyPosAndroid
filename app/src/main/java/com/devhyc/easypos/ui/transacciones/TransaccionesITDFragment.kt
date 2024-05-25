package com.devhyc.easypos.ui.transacciones

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocPago
import com.devhyc.easypos.databinding.FragmentTransaccionesBinding
import com.devhyc.easypos.ui.transacciones.adapter.ItemDevolucionAdapter
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.SingleLiveEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransaccionesITDFragment : Fragment() {

    private lateinit var TransaccionesITDViewModel: TransaccionesITDFragmentViewModel
    private lateinit var _binding: FragmentTransaccionesBinding
    private val binding get() = _binding!!
    var dialog: AlertDialog? = null
    lateinit var adapterTransacciones: ItemDevolucionAdapter
    var itemSelect:Int=0
    var transaccionesNoAsociadas:Boolean = false
    var esDevolucion:Boolean = false
    //var pagoSelect:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransaccionesITDViewModel = ViewModelProvider(this)[TransaccionesITDFragmentViewModel::class.java]
        _binding = FragmentTransaccionesBinding.inflate(this.layoutInflater)
        //
        //OBTENER ARGUMENTOS PASADOS POR PARAMETRO
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            transaccionesNoAsociadas = bundle.getBoolean("transaccionesNoAsociadas",false)
            esDevolucion = bundle.getBoolean("esDevolucion",false)
            CargarTransacciones()
        }
        TransaccionesITDViewModel.ListadoDocumentos.observe(this, Observer {
            adapterTransacciones = ItemDevolucionAdapter(ArrayList(it),transaccionesNoAsociadas)
            adapterTransacciones.setOnItemClickListener(object: ItemDevolucionAdapter.OnItemClickListener {
                override fun onConsultarButtonClick(position: Int) {
                    itemSelect=position
                    TransaccionesITDViewModel.ConsultarEstadoTransaccion(adapterTransacciones.items[position].TransaccionId,adapterTransacciones.items[position].Proveedor,transaccionesNoAsociadas)
                }
                override fun onSeleccionarPagoButtonClick(position: Int) {
                    if (transaccionesNoAsociadas)
                    {
                        //BUSCO ESA TRANSACCION
                        TransaccionesITDViewModel.ConsultarTransaccionITD(adapterTransacciones.items[position].TransaccionId,adapterTransacciones.items[position].Proveedor)
                    }
                    else
                    {
                        Snackbar.make(requireView(),"Utilice esto para asociar un cobro con tarjeta ya emitido a un nuevo ticket",Snackbar.LENGTH_SHORT).show()
                    }
                }
            })
            binding.rvTransacciones.layoutManager = LinearLayoutManager(activity)
            binding.rvTransacciones.adapter = adapterTransacciones
            adapterTransacciones.notifyItemChanged(itemSelect)
            binding.rvTransacciones.scrollToPosition(itemSelect)
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.subtitle = "Cantidad: ${adapterTransacciones.items.count()}"
            Snackbar.make(requireView(),"${adapterTransacciones.items.count()} transacciones listadas",Snackbar.LENGTH_SHORT).show()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = binding.root
        TransaccionesITDViewModel.MedioPagoCargado.observe(viewLifecycleOwner, Observer {
            val bundle = Bundle().apply {
                putParcelable("resultadoPagoTransaccion",it as? Parcelable)
            }
            parentFragmentManager.setFragmentResult("resultadoTransaccionKey",bundle)
            findNavController().popBackStack()
        })
        TransaccionesITDViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.shimmerSeleccionTrans.isVisible = it
            binding.progressBar8.isVisible = it
            binding.rvTransacciones.isVisible = !it
        })
        TransaccionesITDViewModel.MensajeServer.observe(viewLifecycleOwner, Observer {
            AlertView.showError("¡Atención!",it,binding.root.context)
        })
        /*TransaccionesITDViewModel.ActualizarLista.observe(viewLifecycleOwner, Observer {
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.subtitle = "Cantidad: ${adapterTransacciones.items.count()}"
            Snackbar.make(requireView(),it,Snackbar.LENGTH_SHORT).show()
        })*/
        //VIEWMODELS
        TransaccionesITDViewModel.mostrarErrorLocal.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            DialogoPersonalizado("Ocurrió el siguiente error",it,true)
        })
        TransaccionesITDViewModel.mostrarErrorServer.observe(viewLifecycleOwner, SingleLiveEvent.EventObserver {
            DialogoPersonalizado("Error devuelto por FISERV",it,true)
        })
        return root
    }

    private fun DialogoPersonalizado(titulo:String,mensaje:String,cerrar:Boolean = false, cierreAutomatico:Boolean=false)
    {
        if (dialog == null)
        {
            dialog = AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setIcon(R.drawable.ic_baseline_payment_24)
                .setMessage(mensaje)
                .setPositiveButton("Cerrar") { _, _ ->
                    dialog?.dismiss()
                }
                .setCancelable(false)
                .create()
            dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.visibility = View.GONE
        }
        dialog?.show()
        //Cambiar Texto
        if (titulo.isNotBlank())
            dialog?.setTitle(titulo)
        if (titulo.isNotBlank())
            dialog?.setMessage(mensaje)
        if (cierreAutomatico)
            dialog?.dismiss()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isVisible = cerrar
    }

    fun CargarTransacciones()
    {
        try {
            if (transaccionesNoAsociadas)
            {
                (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = "Transacciones aprobadas"
                (requireActivity() as? AppCompatActivity)?.supportActionBar?.subtitle = ""
                //CARGO LAS TRANSACCIONES
                TransaccionesITDViewModel.ListarDocumentosSinAsociarITD()
            }
            else
            {
                (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = "Transacciones realizadas"
                (requireActivity() as? AppCompatActivity)?.supportActionBar?.subtitle = ""
                //CARGO LAS TRANSACCIONES
                TransaccionesITDViewModel.ListarDocumentosITD()
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al buscar las transacciones", e.message, requireContext())
        }
    }
}