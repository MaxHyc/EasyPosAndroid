package com.devhyc.easypos.ui.documento

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentDocBinding
import com.devhyc.easypos.ui.mediopago.MedioPagoFragment
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.jamesmobile.ui.documento.adapter.ItemDocAdapter
import com.devhyc.jamesmobile.ui.documento.adapter.ItemDocTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocFragment : Fragment() {

    private var _binding: FragmentDocBinding? = null
    private val binding get() = _binding!!
    //
    private lateinit var adapterItems: ItemDocAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = ""
        super.onPause()
    }

    override fun onResume() {
        //Ocultar Teclado
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDocBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Accion de abrir lista de articulos
        binding.flAddArticulo.setOnClickListener {
            val action = DocFragmentDirections.docToItemDoc()
            view?.findNavController()?.navigate(action)
        }
        //
        binding.flAddRut.setOnClickListener {
            val action = DocFragmentDirections.docToCabezal()
            view?.findNavController()?.navigate(action)
        }
        //
        if(Globales.CajaActual != null)
        {
            (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.menu_pdv)
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Caja ${Globales.NroCaja}"
        }
        else
        {
          MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.atencion)
                .setTitle("¡Atención!")
                .setMessage("Debe iniciar una caja para empezar a facturar.\nVaya a 'Movimientos de Caja' e inicie una caja.")
                .setPositiveButton("Entendido", DialogInterface.OnClickListener {
                        dialogInterface, i ->
                    run {
                        view?.findNavController()?.navigateUp()
                    }
                })
                .setCancelable(false)
                .show()
        }
        //Cargar Items
        CargarItems()
        //Eventos swipe
        val itemDocTouchHelper = object: ItemDocTouchHelper(requireContext())
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction)
                {
                    ItemTouchHelper.LEFT ->{
                        Globales.ItemsDeDocumento.remove(adapterItems.items[viewHolder.adapterPosition])
                        Snackbar.make(requireView(),"Artículo eliminado", Snackbar.LENGTH_SHORT).setAction("Aceptar",{}).show()
                        CargarItems()
                    }
                }
            }
        }
        //
        val touchHelper = ItemTouchHelper(itemDocTouchHelper)
        touchHelper.attachToRecyclerView(binding.rvArticulospdv)
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_documento,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.btnGuardarDocumento ->
            {
                val action = DocFragmentDirections.actionDocFragmentToMedioPagoFragment()
                view?.findNavController()?.navigate(action)
                //Abrir modal
                //val linear = MedioPagoFragment()
                //linear.show(activity!!.supportFragmentManager,"VentanaMedioPago")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun CargarItems()
    {
        if (Globales.ItemsDeDocumento != null)
        {
            try {
                adapterItems = ItemDocAdapter(Globales.ItemsDeDocumento)
                adapterItems.setOnItemClickListener(object: ItemDocAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int) {

                    }
                })
                binding.rvArticulospdv.layoutManager = LinearLayoutManager(activity)
                binding.rvArticulospdv.adapter = adapterItems
                /*if (Globales.Cabezal!=null)
                {
                    docViewModel.CalcularTotalDocumento(DTDoc(Globales.Cabezal,Globales.ItemsDeDocumento.toList()))
                }*/
            }
            catch (e:Exception)
            {
                Toast.makeText(requireActivity(),"${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ReiniciarVariables()
    {
        try
        {
            Globales.ItemsDeDocumento = ArrayList()
            //Globales.TipoDocumentoSeleccionado = null
            //Globales.Cabezal = null
            //Globales.EditandoDocumento = false
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al reiniciar variables",e.message,requireContext())
        }
    }
}