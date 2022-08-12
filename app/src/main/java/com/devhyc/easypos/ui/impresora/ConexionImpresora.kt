package com.devhyc.easypos.ui.impresora

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTImpresora
import com.devhyc.easypos.databinding.FragmentConexionImpresoraBinding
import com.devhyc.easypos.impresion.ImpresionSunMi
import com.devhyc.easypos.ui.impresora.adapter.ItemImpresoraAdapter
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.jamesmobile.ui.impresora.ConexionImpresoraViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.zebra.sdk.comm.BluetoothConnection
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConexionImpresora : Fragment() {

    private var _binding: FragmentConexionImpresoraBinding? = null
    private val binding get() = _binding!!
    private lateinit var conexionImpresoraViewModel: ConexionImpresoraViewModel

    //
    private val REQUEST_ENABLE_BT: Int = 1
    var PERMISO: Int = 1
    var REQUEST: Int = 200
    //
    private lateinit var adapterImpresora: ItemImpresoraAdapter
    private var vinculadas: ArrayList<DTImpresora> = ArrayList()
    //

    //BROADCAST
    private var searchFinish: BroadcastReceiver? = null
    private var searchStart: BroadcastReceiver? = null
    private var discoveryResult: BroadcastReceiver? = null
    var adapter: ArrayAdapter<String>? = null

    private lateinit var contexto:Context

    private var mBluetoothAdapter: BluetoothAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        conexionImpresoraViewModel = ViewModelProvider(this)[ConexionImpresoraViewModel::class.java]
        _binding = FragmentConexionImpresoraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        bluetoothSetup()

        contexto = requireContext()

        //Obtener MAc impresora vinculada
        var MacActual = Globales.sharedPreferences.getString("MAC","")
        //

        val pairedDevices: Set<BluetoothDevice>? = mBluetoothAdapter?.bondedDevices
        if (pairedDevices!!.isNotEmpty())
        {
            pairedDevices.forEach { device ->
                try {
                    if (MacActual == device.address)
                    {
                        vinculadas.add(DTImpresora(device.name,device.address,true,true,device.bluetoothClass.majorDeviceClass))
                    }
                    else
                    {
                        vinculadas.add(DTImpresora(device.name,device.address,true,false,device.bluetoothClass.majorDeviceClass))
                    }
                }
                catch (e:Exception)
                {
                    Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
                }
            }
        }
        else
        {
            Toast.makeText(requireContext(),"No hay dispositivos vinculados",Toast.LENGTH_LONG).show()
        }
        //
        adapterImpresora = ItemImpresoraAdapter(ArrayList<DTImpresora>(vinculadas))
        adapterImpresora.setOnItemClickListener(object: ItemImpresoraAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                //Restaurar seleccion
                for (i in adapterImpresora.impresoras)
                {
                    i.seleccionada = false
                }
                //Mostrar la seleccionada
                adapterImpresora.impresoras[position].seleccionada = true
                adapterImpresora.notifyDataSetChanged()
                //
                val editor = Globales.sharedPreferences.edit()
                editor.putString("MAC", adapterImpresora.impresoras[position].mac)
                editor.putBoolean("seconfiguro", false)
                editor.commit()
                //
                //
                //Toast.makeText(requireContext(),"${adapterImpresora.impresoras[position].nombre} guardada",Toast.LENGTH_SHORT).show()
                Snackbar.make(binding.clayout,"${adapterImpresora.impresoras[position].nombre} guardada",Snackbar.LENGTH_SHORT)
                    //.setAction(R.string.Descartar,MyUndoListener())
                    .setAnimationMode(ANIMATION_MODE_SLIDE)
                    .show()
            }
        })

        binding.rvListImpresoras.layoutManager = LinearLayoutManager(activity)
        binding.rvListImpresoras.adapter = adapterImpresora

        //PERMISOS EN TIEMPO DE EJECUCION PARA ANDROID 6 EN ADELANTE
        var permissionFineCheck = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionFineCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST)
        }
        var permissionCoarseCheck = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionCoarseCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),REQUEST)
        }
      /*  var permissionBackCheck = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (permissionBackCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),REQUEST)
        }*/
        //

        binding.flBuscarPrinters.setOnClickListener {
            if (!mBluetoothAdapter!!.isDiscovering) {
                //Clear LIST y ADAPTEr
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.BLUETOOTH_ADMIN
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    adapterImpresora.impresoras.clear()
                    mBluetoothAdapter!!.startDiscovery()
                    Snackbar.make(binding.clayout,"Buscando dispositivos",Snackbar.LENGTH_SHORT)
                        //.setAction(R.string.Descartar,MyUndoListener())
                        .setAnimationMode(ANIMATION_MODE_SLIDE)
                        .show()
                } else {
                    Snackbar.make(requireView(),"No ha concedido permisos a la aplicación",Snackbar.LENGTH_SHORT).show()
                }
            }
            else
            {
                mBluetoothAdapter!!.cancelDiscovery()
                Snackbar.make(binding.clayout,"Búsqueda cancelada",Snackbar.LENGTH_SHORT)
                    .setAnimationMode(ANIMATION_MODE_SLIDE)
                    //.setAction(R.string.Descartar,MyUndoListener())
                    .show()
            }
        }

        binding.flImpresionPrueba.setOnClickListener {
            Globales.ControladoraSunMi.ImprimirPaginaDePrueba(requireContext())
            Snackbar.make(binding.clayout,"Imprimiendo pagina de prueba",Snackbar.LENGTH_SHORT)
                .setAnimationMode(ANIMATION_MODE_SLIDE)
                .show()
        }


        //BUSCAR

        // UI - Event Handler.
        // Search device, then add List.
        discoveryResult = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val key: String
                val remoteDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (remoteDevice != null)
                {
                    var nombreImpresora:String = ""
                    var i: DTImpresora? = null
                    if (remoteDevice.name == null)
                    {
                        nombreImpresora = "Dispositivo sin nombre"
                    }
                    else
                    {
                        nombreImpresora = remoteDevice.name
                    }
                    if (remoteDevice.bondState != BluetoothDevice.BOND_BONDED) {
                        if (MacActual == remoteDevice.address)
                        {
                            i = DTImpresora(nombreImpresora, remoteDevice.address, false, true,remoteDevice.bluetoothClass.majorDeviceClass)
                        }
                        else
                        {
                            i = DTImpresora(nombreImpresora,remoteDevice.address,false,false,remoteDevice.bluetoothClass.majorDeviceClass)
                        }
                    }
                    else
                    {
                        //VINCULADA
                        if (MacActual == remoteDevice.address)
                        {
                            i = DTImpresora(nombreImpresora,remoteDevice.address,true,true,remoteDevice.bluetoothClass.majorDeviceClass)
                        }
                        else
                        {
                            i = DTImpresora(nombreImpresora,remoteDevice.address,true,false,remoteDevice.bluetoothClass.majorDeviceClass)
                        }
                    }
                    try {
                        if (i != null) {
                            adapterImpresora.impresoras.add(i)
                        }
                        adapterImpresora.notifyDataSetChanged()
                    }
                    catch (e:Exception)
                    {
                        Toast.makeText(requireContext(), "${e.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        requireActivity().registerReceiver(discoveryResult, IntentFilter(BluetoothDevice.ACTION_FOUND))
        searchStart = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                binding.flBuscarPrinters.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.red)))
                binding.flBuscarPrinters.setImageDrawable(resources.getDrawable(R.drawable.ic_close))
                binding.progressBar10.visibility = View.VISIBLE
            }
        }
        requireActivity().registerReceiver(searchStart, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        searchFinish = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                binding.flBuscarPrinters.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(
                    R.color.blue
                )))
                binding.flBuscarPrinters.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_search_24))
                binding.progressBar10.visibility = View.GONE
            }
        }
        requireActivity().registerReceiver(searchFinish, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
        return root
    }

    // BT
    private val bluetoothPort: BluetoothConnection? = null
    @SuppressLint("ResourceType", "MissingPermission")
    private fun bluetoothSetup(): Boolean {
        var bOk = true
        try {
            //clearBtDevData()
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (mBluetoothAdapter == null) {
                AlertView.showAlert(
                    "¡Atención!",
                    "¡El dipositivo no soporta bluetooth!",
                    requireActivity()
                )
                bOk = false
                return bOk
            }
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        } catch (e: Exception) {
            bOk = false
            AlertView.showAlert(
                "Error",
                e.message, requireActivity()
            )
        }
        return bOk
    }

  /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(resultCode) {
            REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK)
                {
                    Toast.makeText(requireContext(),"ON",Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(requireContext(),"OFF",Toast.LENGTH_LONG).show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }*/

    override fun onDestroy() {
        requireActivity().unregisterReceiver(searchFinish)
        requireActivity().unregisterReceiver(searchStart)
        requireActivity().unregisterReceiver(discoveryResult)
        super.onDestroy()
    }

    class MyUndoListener : View.OnClickListener {

        override fun onClick(v: View) {
            // Code to undo the user's last action
        }
    }
}