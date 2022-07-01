package com.devhyc.easypos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.ui.*
import com.devhyc.easypos.databinding.ActivityMainBinding
import com.devhyc.easypos.ui.login.LoginActivity
import com.devhyc.easypos.utilidades.Globales

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var PERMISO: Int = 1
    var REQUEST: Int = 200
    lateinit var navView: NavigationView
    lateinit var dialog: AlertDialog

    lateinit var nav_Menu:Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        //
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
        PERMISO = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.BLUETOOTH)
        if (PERMISO == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH), REQUEST)
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        //
        nav_Menu = navView.menu

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.menuPrincipalFragment,R.id.docFragment, R.id.listaDeArticulosFragment,R.id.nav_cajaFragment,R.id.nav_documentosFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //
        CargarSharedPreferences()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onPostResume() {
        super.onPostResume()
        if (Globales.CerrarApp)
        {
            finishAndRemoveTask()
        }
        //Cargar datos del cabezal
        if (navView!=null)
        {
            var headerview: View = navView.getHeaderView(0)
            val nombreUsuario =headerview.findViewById<TextView>(R.id.tvUsuarioLogueado)
            val nrocaja = headerview.findViewById<TextView>(R.id.tvnroCaja)
            if (Globales.UsuarioLoggueado!= null)
            {
                nombreUsuario.text= "${Globales.UsuarioLoggueado.nombre} ${Globales.UsuarioLoggueado.apellido}"
            }
            if (Globales.Terminal != null)
            {
                nrocaja.text = Globales.Terminal.Descripcion
            }
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
                    Globales.UsuarioLoggueado = null
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
            Globales.sharedPreferences = this.getSharedPreferences("preferencias", Context.MODE_PRIVATE)
            Globales.NroCaja = Globales.sharedPreferences.getString("nrocaja","1")
            Globales.DireccionServidor =Globales.sharedPreferences.getString("direccionserver","https://192.168.1.18/wseasym_desa/api/")
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
        //super.onBackPressed()
    }
}