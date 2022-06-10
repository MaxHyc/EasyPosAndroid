package com.devhyc.easypos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.devhyc.easypos.databinding.FragmentDocBinding

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
        //
        binding.flAddArticulo.setOnClickListener {
            val action = DocFragmentDirections.docToItemDoc()
            view?.findNavController()?.navigate(action)
        }
        //
        return root
    }
}