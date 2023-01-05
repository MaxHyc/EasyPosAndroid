package com.devhyc.easypos.ui.login

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.devhyc.easypos.databinding.ActivityLoginBinding
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginActivityViewModel by viewModels()

    private lateinit var dialogo:ProgressDialog

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Boton salir
        binding.btnSalir.setOnClickListener {
            Globales.CerrarApp=true
            finish()
        }
        //Boton iniciar sesion
        binding.btnIniciarSesion.setOnClickListener {
            IniciarSesion()
        }
       loginViewModel.isLoading.observe(this, Observer {
           binding.progressCargandoLogin.isVisible = it
           if (it)
           {
               dialogo = ProgressDialog(this)
               dialogo.setTitle("Iniciando sesión")
               dialogo.setMessage("Comprobando credenciales, aguarde un instante")
               dialogo.setCancelable(true)
               dialogo.show()
           }
       })
        loginViewModel.iniciar.observe(this, Observer {
            if (it)
            {
                Thread.sleep(500)
                this.finish()
            }
        })
        loginViewModel.LoginModel.observe(this, Observer {
            if (it != null)
            {
                //Guardar usuario loggueado
                Globales.UsuarioLoggueado = it
                loginViewModel.obtenerTerminal()
                loginViewModel.obtenerCajaAbierta()
                Toast.makeText(binding.root.context,"Bienvenido ${it.nombre} ${it.apellido}",Toast.LENGTH_LONG).show()
            }
        })
        loginViewModel.mensaje.observe(this, Observer {
            try
            {
                AlertView.showAlert("¡Atención!",it,this)
                if (dialogo != null)
                {
                    dialogo.dismiss()
                }
            }
            catch (e:Exception)
            {
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
        })
        binding.tvVersionDeLaApp.text = "Version " + this.packageManager.getPackageInfo(this.packageName, 0).versionName
    }

    fun IniciarSesion()
    {
        try {
            if (binding.etUsuario.text.toString().equals(""))
            {
                AlertView.showAlert("¡Atención!","El nombre de usuario no puede ser vacío",binding.root.context)
            }
            else
            {
                //Buscar usuario
                loginViewModel.iniciarSesion(binding.etUsuario.text.toString(),binding.etPass.text.toString())
            }
        }
        catch (e:Exception)
        {
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}