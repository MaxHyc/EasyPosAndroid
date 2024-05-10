package com.devhyc.easypos

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.util.TypedValue
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.*
import com.devhyc.easypos.databinding.ActivityMainBinding
import com.devhyc.easypos.fiserv.device.DeviceApi
import com.devhyc.easypos.fiserv.device.DeviceService
import com.devhyc.easypos.fiserv.device.IDeviceService
import com.devhyc.easypos.fiserv.presenter.TransactionLauncherPresenter
import com.devhyc.easypos.fiserv.presenter.TransactionPresenter
import com.devhyc.easypos.fiserv.service.TransactionServiceImpl
import com.devhyc.easypos.ui.login.LoginActivity
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.ingenico.fiservitdapi.transaction.Transaction

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var PERMISO: Int = 1
    var REQUEST: Int = 200
    lateinit var navView: NavigationView
    lateinit var dialog: AlertDialog
    lateinit var nav_Menu:Menu

    override fun onDestroy() {
        //DESREGISTRAR IMPRESORA FISERV
        DesregistrarServicioPDAFiserv()
        super.onDestroy()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        //
        VerificarPermisos()
        //Permisos de la App
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISO
        )
        PERMISO = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH)
        if (PERMISO == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH), REQUEST)
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        //
        nav_Menu = navView.menu

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.menuPrincipalFragment,
                R.id.documentoPrincipalFragment,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //
        CargarSharedPreferences()
        startActivity(Intent(this, LoginActivity::class.java))
        //FISERV INSTANCE
        Globales.fiserv.InstanciarDeviceService(this)
    }

    override fun onPostResume() {
        super.onPostResume()
        if (Globales.CerrarApp)
        {
            Globales.CerrarApp = false
            finishAffinity()
        }
    }

    fun DialogoCerrarSesion()
    {
        dialog= AlertDialog.Builder(this)
            .setIcon(R.drawable.atencion)
            .setTitle("¡Atención!")
            .setMessage("¿Desea cerrar la sesión actual?")
            .setPositiveButton("Si", DialogInterface.OnClickListener {
                    dialogInterface, i ->
                run {
                    Globales.Deposito = null
                    Globales.UsuarioLoggueado = null
                    GuardarEstadoLogin("","",false)
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialogInterface, i -> dialog.dismiss()
            })
            .setCancelable(true)
            .setOnCancelListener { "Cancelar" }
            .show()
    }

    fun GuardarEstadoLogin(user:String,password:String,valorSesion:Boolean)
    {
        val editor = Globales.sharedPreferences.edit()
        editor.putString("usuarioanterior",user)
        editor.putString("passanterior",password)
        editor.putBoolean("sesionviva",valorSesion)
        editor.commit()
        Globales.SesionViva = false
        Globales.UsuarioAnterior = ""
        Globales.PassAnterior = ""
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @SuppressLint("CommitPrefEdits")
    private fun CargarSharedPreferences()
    {
        try
        {
            Globales.sharedPreferences = this.getSharedPreferences(getString(R.string._sharedPreferences), MODE_PRIVATE)
            Globales.NroCaja = Globales.sharedPreferences.getString(getString(R.string._nrocaja),"1")
            Globales.DireccionServidor = BuildConfig.DIRECCION_URL
            Globales.DireccionPlexo = BuildConfig.DIRECCION_PLEXO
            Globales.ImpresionSeleccionada = Globales.sharedPreferences.getInt(getString(R.string._tipo_impresora),0)
            Globales.DireccionMac = Globales.sharedPreferences.getString(getString(R.string._mac),"")
            /*if (Globales.ImpresionSeleccionada == Globales.eTipoImpresora.FISERV.codigo)
            {
                //Globales.ControladoraFiservPrint.InstanciarImpresora(this)
            }*/
            Globales.SesionViva = Globales.sharedPreferences.getBoolean("sesionviva",false)
            Globales.UsuarioAnterior = Globales.sharedPreferences.getString("usuarioanterior","")
            Globales.PassAnterior = Globales.sharedPreferences.getString("passanterior","")
        }
        catch (e: Exception)
        {
            Toast.makeText(this,"Error al cargar las preferencias compartidas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (!Globales.EnPrincipal)
        {
            super.onBackPressed()
        }
        else
        {
            DialogoCerrarSesion()
        }
    }

    fun VerificarPermisos()
    {
        //Ubicacion
        var permisoCoarse = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
        var permisoFine = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        //Bluetooth
        var permisoBluetooth = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH)
        var permisoBluetooth_Admin = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_ADMIN)
        var permisoBluetooth_Scan = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_SCAN)
        var permisoBluetooth_Connect = ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT)
        var permisoBluetooth_Advertice= ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_ADVERTISE)
        //
        /* if (permisoBackground == PackageManager.PERMISSION_GRANTED)
         //Toast.makeText(this,"Permiso Background Concedido",Toast.LENGTH_SHORT).show()
         else
             requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQUEST)*/
        //
        if (permisoCoarse == PackageManager.PERMISSION_GRANTED)
        //Toast.makeText(this,"Permiso Coarse Concedido",Toast.LENGTH_SHORT).show()
        else
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST)
        //
        if (permisoFine == PackageManager.PERMISSION_GRANTED)
        //Toast.makeText(this,"Permiso Fine Concedido",Toast.LENGTH_SHORT).show()
        else
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST)
        //
        if (permisoBluetooth == PackageManager.PERMISSION_GRANTED)
        //Toast.makeText(this,"Permiso Bluetooth Concedido",Toast.LENGTH_SHORT).show()
        else
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH), REQUEST)
        //
        if (permisoBluetooth_Admin == PackageManager.PERMISSION_GRANTED)
        //Toast.makeText(this,"Permiso BAdmin Concedido",Toast.LENGTH_SHORT).show()
        else
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_ADMIN), REQUEST)
        //
        if (permisoBluetooth_Scan == PackageManager.PERMISSION_GRANTED)
        //Toast.makeText(this,"Permiso Bscan Concedido",Toast.LENGTH_SHORT).show()
        else
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN), REQUEST)
        //
        if (permisoBluetooth_Connect == PackageManager.PERMISSION_GRANTED)
        //Toast.makeText(this,"Permiso Bconnect Concedido",Toast.LENGTH_SHORT).show()
        else
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST)
        //
        if (permisoBluetooth_Advertice == PackageManager.PERMISSION_GRANTED)
        //Toast.makeText(this,"Permiso bAdvertice Concedido",Toast.LENGTH_SHORT).show()
        else
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE), REQUEST)
    }

    fun DesregistrarServicioPDAFiserv()
    {
        if(Globales.ImpresionSeleccionada == Globales.eTipoImpresora.FISERV.codigo)
        {
            if (Globales.fiserv.deviceService != null)
            {
                Globales.fiserv.RegistrarDeviceService(this,false)
            }
        }
    }
}