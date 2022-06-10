package com.devhyc.easypos

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
       })
        loginViewModel.iniciar.observe(this, Observer {
            if (it)
            {
                //loginViewModel.obtenerTerminal()
                //loginViewModel.obtenerCajaAbierta()
                finish()
            }
            else
            {
                AlertView.showAlert("¡Atención!","No se pudo iniciar la sesión",binding.root.context)
            }
        })
        loginViewModel.LoginModel.observe(this, Observer {
            if (it.ok)
            {
                //Guardar usuario loggueado
                    Globales.UsuarioLoggueado = it.elemento
                //
                loginViewModel.obtenerTerminal()
                loginViewModel.obtenerCajaAbierta()
                Toast.makeText(binding.root.context,"Bienvenido ${it.elemento.nombre} ${it.elemento.apellido}",Toast.LENGTH_LONG).show()
            }
            else
            {
                AlertView.showAlert("¡Atención!","${it.mensaje}",binding.root.context)
            }
        })
        loginViewModel.mensaje.observe(this, Observer {
            Toast.makeText(this,it,Toast.LENGTH_LONG).show()
        })
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