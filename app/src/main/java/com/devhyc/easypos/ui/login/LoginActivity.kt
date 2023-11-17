package com.devhyc.easypos.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.devhyc.easypos.databinding.ActivityLoginBinding
import com.devhyc.easypos.fiserv.FiservITD
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.easypos.utilidades.Globales.fiserv
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginActivityViewModel by viewModels()

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Valida si cerro sesion
        if (Globales.SesionViva)
        {
            binding.animationlogin.isVisible = true
            IniciarSesion(Globales.UsuarioAnterior,Globales.PassAnterior,true)
        }
        //RETORNA LOGUIN AUTOMATICO
        loginViewModel.LoginAutomatico.observe(this, Observer {
            if (it != null)
                Globales.UsuarioLoggueado = it
                loginViewModel.obtenerTerminal()
            Toast.makeText(binding.root.context,"Bienvenido de nuevo ${it.nombre}",Toast.LENGTH_LONG).show()
        })
        //RETORNA EL INICIO AUTOMATICO
        loginViewModel.iniciarAutomatico.observe(this, Observer {
            if (it)
            {
                finish()
            }
        })
        //Boton iniciar sesion
        binding.btnIniciarSesion.setOnClickListener {
            //binding.animationlogin.isVisible = true
            //IniciarSesion(binding.etUsuario.text.toString(),binding.etPass.text.toString(),false)
            fiserv.ConectarServicioITD(this)
            fiserv.ProcesarTransaccionITD(this)
        }
        //LOADINGS
        loginViewModel.isLoadingTerminal.observe(this, Observer {
            if (it)
            {
                binding.etUsuario.visibility = View.GONE
                binding.etPass.visibility = View.GONE
                binding.btnIniciarSesion.visibility = View.GONE
                binding.tvinfoLoginHyc.visibility = View.VISIBLE
                binding.tvinfoLoginHyc.setText("OBTENIENDO PARAMETROS DE TERMINAL")
            }
        })
        loginViewModel.isLoadingControlLogin.observe(this,Observer {
            if (it)
            {
                binding.etUsuario.visibility = View.GONE
                binding.etPass.visibility = View.GONE
                binding.btnIniciarSesion.visibility = View.GONE
                binding.tvinfoLoginHyc.visibility = View.VISIBLE
                binding.tvinfoLoginHyc.setText("CONECTANDO CON SERVIDORES DE HYC")
            }
        })
        loginViewModel.isLoadingAutomatico.observe(this,Observer {
            if (it)
            {
                binding.etUsuario.visibility = View.GONE
                binding.etPass.visibility = View.GONE
                binding.btnIniciarSesion.visibility = View.GONE
                binding.tvinfoLoginHyc.visibility = View.VISIBLE
                binding.tvinfoLoginHyc.setText("COMPROBANDO CREDENCIALES AUTOMATICAMENTE")
            }
        })
        loginViewModel.isLoadingInicioSesion.observe(this,Observer {
            if (it)
            {
                binding.etUsuario.visibility = View.GONE
                binding.etPass.visibility = View.GONE
                binding.btnIniciarSesion.visibility = View.GONE
                binding.tvinfoLoginHyc.visibility = View.VISIBLE
                binding.tvinfoLoginHyc.setText("COMPROBANDO CREDENCIALES LOCALES")
            }
        })
        //
        loginViewModel.iniciar.observe(this, Observer {
            if (it)
            {
                Thread.sleep(500)
                finish()
            }
        })
        loginViewModel.LoginModel.observe(this, Observer {
            if (it != null)
            {
                //Guardar usuario loggueado
                Globales.UsuarioLoggueado = it
                loginViewModel.obtenerTerminal()
                GuardarEstadoLogin(binding.etUsuario.text.toString(),binding.etPass.text.toString(),true)
                Toast.makeText(binding.root.context,"Bienvenido ${it.nombre} ${it.apellido}",Toast.LENGTH_LONG).show()
            }
        })
        loginViewModel.mensaje.observe(this, Observer {
            try
            {
                AlertView.showAlert("¡Atención!",it,this)
                MostrarControles()
            }
            catch (e:Exception)
            {
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                MostrarControles()
            }
        })
        binding.tvVersionDeLaApp.text = "Version " + this.packageManager.getPackageInfo(this.packageName, 0).versionName
    }

    fun IniciarSesion(user: String,password: String,automatico:Boolean)
    {
        try {
            if (user.equals(""))
            {
                AlertView.showAlert("¡Atención!","El nombre de usuario no puede ser vacío",binding.root.context)
                MostrarControles()
            }
            else
            {
                loginViewModel.iniciarSesionControlLogin(user,password,automatico)
            }
        }
        catch (e:Exception)
        {
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
            MostrarControles()
        }
    }

    fun MostrarControles()
    {
        try {
            binding.etUsuario.visibility = View.VISIBLE
            binding.etPass.visibility = View.VISIBLE
            binding.btnIniciarSesion.visibility = View.VISIBLE
            binding.tvinfoLoginHyc.visibility = View.GONE
            binding.progressCargandoLogin.isVisible = false
            binding.animationlogin.isVisible = false
        }
        catch (e:Exception)
        {
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
        }
    }

    fun GuardarEstadoLogin(user:String,password:String,valorSesion:Boolean)
    {
        val editor = Globales.sharedPreferences.edit()
        editor.putString("usuarioanterior",user)
        editor.putString("passanterior",password)
        editor.putBoolean("sesionviva",valorSesion)
        editor.commit()
        Globales.SesionViva = valorSesion
        Globales.UsuarioAnterior = user
        Globales.PassAnterior = password
    }
}
