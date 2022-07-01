package com.devhyc.easypos.ui.cierrecaja

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.devhyc.easypos.databinding.FragmentCierreCajaBinding
import com.google.android.material.snackbar.Snackbar

class CierreCajaFragment : Fragment() {

    private var _binding: FragmentCierreCajaBinding? = null
    private val binding get() = _binding!!
    private var tipo:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCierreCajaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (arguments !=null)
        {
            val bundle:Bundle = arguments as Bundle
            tipo = bundle.getInt("Tipo",0)

        }
        //
        return root
    }
}