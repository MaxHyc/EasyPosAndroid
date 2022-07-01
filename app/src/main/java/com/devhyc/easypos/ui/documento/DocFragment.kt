package com.devhyc.easypos.ui.documento

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentDocBinding
import com.devhyc.easypos.ui.login.LoginActivity
import com.devhyc.easypos.ui.menuprincipal.MenuPrincipalFragmentDirections
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class DocFragment : Fragment() {

    private var _binding: FragmentDocBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        if(Globales.CajaActual != null)
        {
            (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.menu_pdv)
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Caja ${Globales.NroCaja}"
        }
        else
        {
            //AlertView.showAlert(getString(R.string.Atencion),"Debe iniciar una caja para empezar a facturar",requireContext())
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
                Snackbar.make(requireView(),"Cobrar",Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show()
                //val action = ModServicioFragmentDirections.modServicioToFirma()
                //view?.findNavController()?.navigate(action)
                //Globales.oReclamoSeleccionado = oReclamoPendiente
            }
        }
        return super.onOptionsItemSelected(item)
    }
}