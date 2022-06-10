package com.devhyc.easypos.ui.firma

import android.graphics.*
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.FragmentFirmaBinding
import com.devhyc.easypos.utilidades.AlertView
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream


class FirmaFragment : Fragment() {

    private var _binding: FragmentFirmaBinding? = null
    private val binding get() = _binding!!
    lateinit var vista: VistaFirma

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentFirmaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        try
        {
            vista = VistaFirma(requireContext())
            binding.panelFirma.addView(vista)
            //requireActivity().setContentView(vista)
            //GuardarFirma
            binding.flGuardarFirma.setOnClickListener {
                ConvertirImagenABase64()
            }
            //BorrarFirma
            binding.flBorrarFirma.setOnClickListener {
                BorrarFirma()
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert(getString(R.string.Error),e.message,requireContext())
        }
        return root
    }

    fun BorrarFirma()
    {
        try {
            vista.SetErase(true)
            vista.invalidate()
        }
        catch (e:Exception)
        {
            AlertView.showAlert(getString(R.string.Error),e.message,requireContext())
        }
    }

    fun ConvertirImagenABase64(): String
    {
        var base:String
        try {
            var bitmap = vista.drawToBitmap()
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos)
            val b = baos.toByteArray()
            base = Base64.encodeToString(b,Base64.DEFAULT)
            Snackbar.make(requireView(),"Imagen guardada",Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            base = ""
            AlertView.showAlert(getString(R.string.Error),e.message,requireContext())
        }
        return base
    }
}
