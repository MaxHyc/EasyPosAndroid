package com.devhyc.easypos.ui.addarticulos

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocDetalle
import com.devhyc.easypos.databinding.FragmentAddArticuloBinding
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint

class AddArticuloFragment(var art:DTArticulo?, var listener: (art:DTDocDetalle?) -> Unit) : DialogFragment() {

    private var _binding: FragmentAddArticuloBinding? = null
    private val binding get() = _binding!!

    private var DesdeListado:Boolean = false

    private lateinit var AddArtViewModel: AddArticuloFragmentViewModel

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etPrecioAdd.post {
            binding.etPrecioAdd.requestFocus()
            Globales.Herramientas.showKeyboard(binding.etPrecioAdd,requireContext())
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AddArtViewModel = ViewModelProvider(this)[AddArticuloFragmentViewModel::class.java]
        _binding = FragmentAddArticuloBinding.inflate(inflater, container, false)
        val root: View = binding.root
        when(Globales.ParametrosDocumento.Configuraciones.MonedaCodigo)
        {
            Globales.TMoneda.PESOS.codigo.toString() -> { binding.tvPrecioDes.setText("Precio ($)") }
            Globales.TMoneda.DOLARES.codigo.toString()  -> { binding.tvPrecioDes.setText("Precio (U$" + "S)") }
        }
        if (!Globales.ParametrosDocumento.Modificadores.ModificaDescuento)
        {
            binding.etDescuentoAdd.isEnabled = false
        }
        //Cargo el articulo
        binding.etCantidadAdd.setText("1")
        if (art!=null)
        {
            //SI VIENE DE LA LISTA DE ARTICULOS
            DesdeListado = true
            binding.tvDescripcionArtAdd.text = art!!.nombre
            binding.etCodigoAdd.setText(art!!.codigo.toString())
            binding.etPrecioAdd.setText(art!!.precioFinal.toString())
            binding.etDescuentoAdd.setText("0")
            binding.tvPrecioDes.text = "Precio (${art!!.monedaSigno})"
            //SI ES SERIALIZADO, MUESTRO EL CAMPO SERIE PARA QUE EL USUARIO LA ESCANEE
            if (art!!.usaSerie)
            {
                binding.etSerieAdd.visibility = View.VISIBLE
                binding.etCantidadAdd.setText("1")
                binding.etSerieAdd.selectAll()
                binding.etSerieAdd.requestFocus()
            }
            else
            {
                binding.etSerieAdd.visibility = View.GONE
                binding.etSerieAdd.setText("")
            }
            if(art!!.esRubro == 1)
            {
                binding.etPrecioAdd.isEnabled = true
            }
        }
        else {
            //SI LO CARGA DESDE CODIGO DE BARRAS
            DesdeListado = false
            this.isCancelable = false
            binding.btnGuardarAdd.visibility = View.GONE
            binding.tvDescripcionArtAdd.text = ""
            binding.etCodigoAdd.isEnabled = true
            //
            binding.etCodigoAdd.setOnKeyListener(object: View.OnKeyListener{
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (event!!.getAction() === KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                        runBlocking {
                            this.launch {
                                //BUSCAR ARTICULO ESCANEADO POR CODIGO
                                if (Globales.ParametrosDocumento.Validaciones.Valorizado)
                                {
                                    if (Globales.ParametrosDocumento.Configuraciones.TipoCodigo == Globales.TTipoBusqueda.CODIGOINTERNO.codigo)
                                        AddArtViewModel.ObtenerArticuloPorCodigo(binding.etCodigoAdd.text.toString(),Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo)
                                    else
                                        AddArtViewModel.ObtenerArticuloPorBarras(binding.etCodigoAdd.text.toString(),Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo)
                                }
                                else
                                {
                                    if (Globales.ParametrosDocumento.Configuraciones.TipoCodigo == Globales.TTipoBusqueda.CODIGOBARRAS.codigo)
                                        AddArtViewModel.ObtenerArticuloPorCodigo(binding.etCodigoAdd.text.toString(),"")
                                    else
                                        AddArtViewModel.ObtenerArticuloPorBarras(binding.etCodigoAdd.text.toString(),"")
                                }
                            }
                        }
                        return true
                    }
                    return false
                }
            })
            binding.etSerieAdd.setOnKeyListener(object: View.OnKeyListener{
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (event!!.getAction() === KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                        runBlocking {
                            //BUSCAR ARTICULO ESCANEADO POR SERIE
                            if (Globales.ParametrosDocumento.Validaciones.Valorizado)
                                AddArtViewModel.ObtenerArticuloPorSerie(binding.etSerieAdd.text.toString(),Globales.DocumentoEnProceso.valorizado!!.listaPrecioCodigo)
                            else
                                AddArtViewModel.ObtenerArticuloPorSerie(binding.etSerieAdd.text.toString(),"")
                        }
                        return true
                    }
                    return false
                }
            })
        }
        //
            binding.btnAddCantidad.setOnClickListener {
                if (binding.etCantidadAdd.text.isNotEmpty())
                {
                    ModificarCantidad(binding.etCantidadAdd.text.toString().toDouble(),true)
                }
            }
            binding.btnSubstractCantidad.setOnClickListener {
                if (binding.etCantidadAdd.text.isNotEmpty())
                {
                    ModificarCantidad(binding.etCantidadAdd.text.toString().toDouble(),false)
                }
            }
            binding.btnGuardarAdd.setOnClickListener {
                GuardarDetalle()
            }
            binding.btnCancelarAdd.setOnClickListener {
                //listener(null)
                dialog!!.dismiss()
            }
        //
        AddArtViewModel.articuloEncontrado.observe(viewLifecycleOwner, Observer {
            if (it != null)
            {
                art = it
                binding.etCantidadAdd.isEnabled = true
                binding.btnAddCantidad.isEnabled = true
                binding.btnSubstractCantidad.isEnabled = true
                binding.etCodigoAdd.setText("")
                binding.tvDescripcionArtAdd.text = it!!.nombre
                binding.etPrecioAdd.setText(it!!.precioFinal.toString())
                binding.etDescuentoAdd.setText("0")
                binding.tvPrecioDes.text = "Precio (${it!!.monedaSigno})"
                binding.btnGuardarAdd.visibility = View.VISIBLE
                if (it!!.usaSerie)
                {
                    binding.etSerieAdd.visibility = View.VISIBLE
                    binding.etSerieAdd.setText("1")
                    binding.etSerieAdd.selectAll()
                    binding.etSerieAdd.requestFocus()
                }
                else
                {
                    binding.etSerieAdd.visibility = View.GONE
                    binding.etSerieAdd.setText("")
                }
                if(it!!.esRubro == 1)
                {
                    binding.etPrecioAdd.isEnabled = true
                }
            }
            else
            {
                MostrarMensaje("Artículo escaneado no existe",R.color.red)
            }
        })
        AddArtViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.progressBuscando.isVisible = it
            if (it)
            {
                binding.btnGuardarAdd.visibility = View.GONE
                binding.btnCancelarAdd.visibility = View.GONE
            }
            else
            {
                binding.btnCancelarAdd.visibility = View.VISIBLE
            }
        })
        AddArtViewModel.mostrarMensaje.observe(viewLifecycleOwner, Observer {
            /*if (Globales.UsarTTS)
                Globales.ttsManager!!.initQueue(it)*/
            MostrarMensaje(it, R.color.red)
        })
        AddArtViewModel.listaSerieEncontradas.observe(viewLifecycleOwner, Observer {
            if (it != null)
            {
                if(it.count() > 1)
                    //MostarDialogoListaSeries(it)
                else
                {
                    art = it[0]
                    MostrarArticuloEscaneadoPorSerie(it[0])
                }
            }
        })
        binding.flHabilitarEdicionSerie.setOnClickListener {
            binding.etSerieAdd.isEnabled = true
            binding.etSerieAdd.selectAll()
            binding.etSerieAdd.requestFocus()
            binding.flHabilitarEdicionSerie.visibility = View.GONE
        }
        AddArtViewModel.enfocarCodigo.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.etCodigoAdd.selectAll()
            else
                binding.etSerieAdd.selectAll()
        })
        //
        binding.etPrecioAdd.selectAll()
        binding.etPrecioAdd.requestFocus()
        //
        return root
    }

    fun GuardarDetalle()
    {
        try {
            if (binding.etDescuentoAdd.text.toString().toDouble() <= 50)
            {
                if (binding.etCantidadAdd.text.toString().toDouble() > 0.0)
                {
                    //VALIDAR SI USA SERIE
                    /*if(art!!.usaSerie)
                    {
                        if(binding.etSerieAdd.text.isEmpty())
                        {
                            AlertView.showError("¡Atención!","Debe ingresar un nro de serie para este artículo",requireContext())
                            return
                        }
                        if (ValidarSiSerieExisteEnListado(art!!.id.toLong(),binding.etSerieAdd.text.toString()))
                        {
                            ReiniciarVariables()
                            return
                        }
                    }*/
                    var d = DTDocDetalle(
                        art!!.id.toLong(),
                        art!!.codigo,
                        art!!.nombre,
                        art!!.descripcion1,
                        "UN",
                        binding.etCantidadAdd.text.toString().toDouble(),
                        binding.etPrecioAdd.text.toString().toDouble(),
                        art!!.impuesto,
                        art!!.impuestoValor.toDouble(),
                        binding.etDescuentoAdd.text.toString().toDouble(),
                        false,
                        "",
                        binding.etPrecioAdd.text.toString().toDouble(),
                        art!!.grupo,
                        binding.etSerieAdd.text.toString()
                    )
                    Globales.DocumentoEnProceso.detalle = Globales.DocumentoEnProceso.detalle!!.plus(d)
                    MostrarMensaje("${art!!.nombre} ingresado con éxito",R.color.green)
                    if (DesdeListado)
                    {
                        listener(d)
                        dialog!!.dismiss()
                    }
                    else
                    {
                        ReiniciarVariables()
                    }
                }
                else
                {
                    AlertView.showError("¡Atención!","La cantidad no puede ser 0 o menor a 0",requireContext())
                }
            }
            else
            {
                AlertView.showError("¡Atención!","El descuento no puede ser mayor a 50%",requireContext())
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atención!",e.message,requireContext())
        }
    }

    fun ModificarCantidad(cantidad:Double,suma:Boolean)
    {
        try {
            if (suma)
            {
                var res = cantidad + 1
                binding.etCantidadAdd.setText(res.toString())
            }
            else
            {
                if (cantidad != 0.0)
                {
                    var res = cantidad - 1
                    binding.etCantidadAdd.setText(res.toString())
                }
            }
        }
        catch (e:Exception)
        {
            AlertView.showError("¡Atención!",e.message,requireContext())
        }
    }

    fun ReiniciarVariables()
    {
        try {
            art = null
            binding.etCodigoAdd.setText("")
            binding.etSerieAdd.setText("")
            binding.tvDescripcionArtAdd.text =""
            binding.etPrecioAdd.setText("")
            binding.etDescuentoAdd.setText("0")
            binding.etSerieAdd.isEnabled = true
            binding.btnGuardarAdd.visibility = View.GONE
            binding.flHabilitarEdicionSerie.visibility = View.GONE
            if (binding.swEscanearPorSerie.isChecked)
                binding.etSerieAdd.requestFocus()
            else
                binding.etCodigoAdd.requestFocus()
        }
        catch (ex:Exception)
        {
            AlertView.showError("¡Atención!",ex.message,requireContext())
        }
    }

    fun MostrarMensaje(titulo:String,color:Int)
    {
        Snackbar.make(requireView(),titulo, Snackbar.LENGTH_SHORT)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .setBackgroundTint(resources.getColor(color))
            .show()
    }

    fun MostrarArticuloEscaneadoPorSerie(a:DTArticulo)
    {
        try {
            binding.etCantidadAdd.setText("1")
            binding.etCantidadAdd.isEnabled = false
            binding.btnAddCantidad.isEnabled = false
            binding.btnSubstractCantidad.isEnabled = false
            binding.tvDescripcionArtAdd.text = a!!.nombre
            binding.etPrecioAdd.setText(a!!.precioFinal.toString())
            binding.etDescuentoAdd.setText("0")
            binding.tvPrecioDes.text = "Precio (${a!!.monedaSigno})"
            binding.btnGuardarAdd.visibility = View.VISIBLE
            binding.etSerieAdd.isEnabled = false
            binding.flHabilitarEdicionSerie.visibility = View.VISIBLE
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al mostrar articulo escaneado por serie",e.message,requireContext())
        }
    }
}