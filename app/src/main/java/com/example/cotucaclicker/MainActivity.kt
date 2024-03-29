package com.example.cotucaclicker

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

private val handler = Handler()

open class MainActivity : AppCompatActivity() {

    var contador: Long = 0
    var cooldownSampaio = 0
    var cooldownGincana = 0
    var quantosSucosBandecos = 0
    var quantasHorasSono = 0
    var quantosEsgotos = 0
    var quantasAulasExtras = 0
    var multiplicadorPassivo = 1.0f
    var multiplicadorAtivo = 1.0f
    private lateinit var botaoContador: ImageView
    private lateinit var danoPorSegundo : TextView
    lateinit var textContador: TextView

    private lateinit var buttonDormindoSampaio: Button
    private lateinit var buttonGincana: Button

    var Connection_Info = BuildConfig.Connection_Info.split("/")
    lateinit var conexao : Connection;
    var conexaoEstabelecida = false
    var username: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), PackageManager.PERMISSION_GRANTED)

        class ConnectToDatabaseTask : AsyncTask<Void, Void, Unit>() {
            override fun doInBackground(vararg params: Void?) {
                if(!conexaoEstabelecida) {
                    try {
                        Class.forName("net.sourceforge.jtds.jdbc.Driver")
                        conexao = DriverManager.getConnection(
                            "jdbc:jtds:sqlserver://${Connection_Info[0]}/${Connection_Info[1]}",
                            Connection_Info[2],
                            Connection_Info[3]
                        )
                        println("Conexão bem-sucedida!")
                        conexaoEstabelecida = true;
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        ConnectToDatabaseTask().execute()


        textContador = findViewById(R.id.textContador)
        botaoContador = findViewById(R.id.botaoContador)
        danoPorSegundo = findViewById(R.id.danoPorSegundo)

        buttonDormindoSampaio = findViewById(R.id.buttonDormindoSampaio)
        buttonGincana = findViewById(R.id.buttonGincana)

        restaurarEstado()

        danoPorSegundo.text = "${Math.round((quantasHorasSono * multiplicadorPassivo * 2))} DPS | ${Math.round(quantosSucosBandecos+1 *multiplicadorAtivo)} click"

        handler.post(atualizarContadorRunnable)
        handler.post(atualizarCooldownSampaio)
        handler.post(atualizarCooldownGincana)

        botaoContador.setOnClickListener {
            val scaleX = ObjectAnimator.ofFloat(botaoContador, View.SCALE_X, 0.9f)
            val scaleY = ObjectAnimator.ofFloat(botaoContador, View.SCALE_Y, 0.9f)
            val scaleDown = AnimatorSet().apply {
                play(scaleX).with(scaleY)
                duration = 100
            }

            val scaleXBack = ObjectAnimator.ofFloat(botaoContador, View.SCALE_X, 1.0f)
            val scaleYBack = ObjectAnimator.ofFloat(botaoContador, View.SCALE_Y, 1.0f)
            val scaleUp = AnimatorSet().apply {
                play(scaleXBack).with(scaleYBack)
                startDelay = 100
                duration = 100
            }

            scaleDown.start()
            scaleUp.start()
            btnClicar(it)
        }

        findViewById<ImageView>(R.id.navigation_loja).setOnClickListener {
            val intent = Intent(this, Loja::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.navigation_bosses).setOnClickListener {
            val intent = Intent(this, Bosses::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.navigation_online).setOnClickListener {
            Log.d("Navigation", "Clicou no ícone online")
            val intent = Intent(this, Rank::class.java)
            startActivity(intent)
        }
    }

    fun btnClicar(view: View){
        contador += ((quantosSucosBandecos+1) * multiplicadorAtivo).toLong()
        textContador.text = contador.toString()
    }

    fun ativarDormindoSampaio(view: View?) {
        if (cooldownSampaio <= 0) {
            multiplicadorPassivo += 2.0f
            Toast.makeText(this, "Dormindo na aula do Sampaio? Nota passiva triplicada", Toast.LENGTH_SHORT).show()
            cooldownSampaio = 20
            buttonDormindoSampaio.text = cooldownSampaio.toString()
            buttonDormindoSampaio.setBackgroundColor(0xFFFF0000.toInt())
            danoPorSegundo.text = "${Math.round((quantasHorasSono * multiplicadorPassivo * 2))} DPS | ${Math.round(quantosSucosBandecos+1 *multiplicadorAtivo)} click"
        } else {
            Toast.makeText(this, "A habilidade ainda está em cooldown", Toast.LENGTH_SHORT).show()
        }
    }

    fun ativarGincana(view: View?) {
        if (cooldownGincana <= 0) {
            multiplicadorPassivo += 2.0f
            multiplicadorAtivo += 2.0f
            Toast.makeText(this, "A gincana chegou! Todos pontos triplicados", Toast.LENGTH_SHORT).show()
            cooldownGincana = 60
            buttonGincana.text = cooldownGincana.toString()
            buttonGincana.setBackgroundColor(0xFFFF0000.toInt())
            danoPorSegundo.text = "${Math.round((quantasHorasSono * multiplicadorPassivo * 2))} DPS | ${Math.round(quantosSucosBandecos+1 *multiplicadorAtivo)} click"
        } else {
            Toast.makeText(this, "A habilidade ainda está em cooldown", Toast.LENGTH_SHORT).show()
        }
    }

    private val atualizarCooldownSampaio: Runnable = object : Runnable {
        override fun run() {
            cooldownSampaio--
            if (cooldownSampaio > 0) {
                buttonDormindoSampaio.text = cooldownSampaio.toString()
                if(cooldownSampaio == 10){
                    multiplicadorPassivo -= 2.0f
                    buttonDormindoSampaio.setBackgroundColor(0xE2312C.toInt())
                    danoPorSegundo.text = "${Math.round((quantasHorasSono * multiplicadorPassivo * 2))} DPS | ${Math.round(quantosSucosBandecos+1 *multiplicadorAtivo)} click"
                }
            } else {
                buttonDormindoSampaio.text = "Zz"
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val atualizarCooldownGincana: Runnable = object : Runnable {
        override fun run() {
            cooldownGincana--
            if (cooldownGincana > 0) {
                buttonGincana.text = cooldownGincana.toString()
                if(cooldownGincana == 40){
                    multiplicadorPassivo -= 2.0f
                    multiplicadorAtivo -= 2.0f
                    buttonGincana.setBackgroundColor(0xE2312C.toInt())
                    danoPorSegundo.text = "${Math.round((quantasHorasSono * multiplicadorPassivo * 2))} DPS | ${Math.round(quantosSucosBandecos+1 *multiplicadorAtivo)} click"
                }
            } else {
                buttonGincana.text = "Gincana"
            }
            handler.postDelayed(this, 1000)
        }
    }



    private val atualizarContadorRunnable: Runnable = object : Runnable {
        override fun run() {
            contador += (quantasHorasSono * multiplicadorPassivo).toInt()
            textContador.text = contador.toString()
            handler.postDelayed(this, 500)
        }
    }

    override fun onPause() {
        super.onPause()
        salvarEstado()
    }

    override fun onResume() {
        super.onResume()
        restaurarEstado()
    }

    override fun onDestroy() {
        super.onDestroy()
        salvarEstado()
        handler.removeCallbacks(atualizarContadorRunnable)
    }

    override fun onStop() {
        super.onStop()
        if(username != ""){
            atualizarPontosNoBancoDeDados()
        }
    }

    private fun atualizarPontosNoBancoDeDados() {
        class AtualizarPontosTask : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                try {
                    val statement = conexao.prepareStatement("UPDATE CotucaClicker.Usuarios SET pontos = ? WHERE nome = ?")
                    statement.setLong(1, contador)
                    statement.setString(2, username)
                    statement.executeUpdate()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                return null
            }
        }
        AtualizarPontosTask().execute()
    }

    private fun salvarEstado() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("contador", contador)
        editor.putInt("cooldownSampaio", cooldownSampaio)
        editor.putInt("cooldownGincana", cooldownGincana)
        editor.putInt("quantosSucosBandecos", quantosSucosBandecos)
        editor.putInt("quantasHorasSono", quantasHorasSono)
        editor.putInt("quantosEsgotos", quantosEsgotos)
        editor.putInt("quantasAulasExtras", quantasAulasExtras)
        editor.putFloat("multiplicadorPassivo", multiplicadorPassivo)
        editor.putFloat("multiplicadorAtivo", multiplicadorAtivo)
        editor.putString("username", username)
        editor.apply()
    }

    private fun restaurarEstado() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        contador = sharedPreferences.getLong("contador", 0)
        cooldownSampaio = sharedPreferences.getInt("cooldownSampaio", 0)
        cooldownGincana = sharedPreferences.getInt("cooldownGincana", 0)
        quantosSucosBandecos = sharedPreferences.getInt("quantosSucosBandecos", 0)
        quantasHorasSono = sharedPreferences.getInt("quantasHorasSono", 0)
        quantosEsgotos = sharedPreferences.getInt("quantosEsgotos", 0)
        quantasAulasExtras = sharedPreferences.getInt("quantasAulasExtras", 0)
        multiplicadorPassivo = sharedPreferences.getFloat("multiplicadorPassivo", 1.0f)
        multiplicadorAtivo = sharedPreferences.getFloat("multiplicadorAtivo", 1.0f)
        username = sharedPreferences.getString("username", "")
    }

}