package com.example.cotucaclicker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class Loja : MainActivity() {
    private lateinit var buttonSucoBandeco: Button
    private lateinit var buttonHorasSono: Button
    private lateinit var buttonEsgoto: Button
    private lateinit var buttonAulaExtra: Button

    private lateinit var TextQuantosSucos: TextView
    private lateinit var TextQuantasHorasSono: TextView
    private lateinit var TextQuantosEsgotos: TextView
    private lateinit var TextQuantasAulasExtras : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loja)

        buttonSucoBandeco = findViewById(R.id.buttonSucoBandeco)
        buttonHorasSono = findViewById(R.id.buttonHorasSono)
        buttonEsgoto = findViewById(R.id.buttonEsgoto)
        buttonAulaExtra = findViewById(R.id.buttonAulaExtra)

        buttonSucoBandeco.text = ("${Math.round(0.5 * Math.pow((quantosSucosBandecos + 1).toDouble(), 1.7) * 50).toInt()} - suco do bandeco")
        buttonHorasSono.text = ("${Math.round(0.5 * Math.pow((quantasHorasSono + 1).toDouble(), 1.7) * 150).toInt()} - horas de sono")
        buttonEsgoto.text = ("${Math.round(0.5 * Math.pow((quantosEsgotos + 1).toDouble(), 1.7) * 200).toInt()} - esgoto")
        buttonAulaExtra.text = ("${Math.round(0.5 * Math.pow((quantasAulasExtras + 1).toDouble(), 1.7) * 500).toInt()} - aula extra")

        TextQuantosSucos = findViewById(R.id.TextQuantosSucos)
        TextQuantasHorasSono = findViewById(R.id.TextQuantasHorasSono)
        TextQuantosEsgotos = findViewById(R.id.TextQuantosEsgotos)
        TextQuantasAulasExtras = findViewById(R.id.TextQuantasAulasExtras)

        TextQuantosSucos.text = quantosSucosBandecos.toString()
        TextQuantasHorasSono.text = quantasHorasSono.toString()
        TextQuantosEsgotos.text = quantosEsgotos.toString()
        TextQuantasAulasExtras.text = quantasAulasExtras.toString()

        findViewById<ImageView>(R.id.navigation_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.navigation_bosses).setOnClickListener {
            val intent = Intent(this, Bosses::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.navigation_online).setOnClickListener {
            val intent = Intent(this, Rank::class.java)
            startActivity(intent)
        }
    }

    fun comprarSucoBandeco(view: android.view.View?) {
        if (contador >= (0.5 * Math.pow((quantosSucosBandecos + 1).toDouble(), 1.7) * 50).toInt()) {
            Toast.makeText(this, "Suco do bandeco comprado", Toast.LENGTH_SHORT).show()
            contador -= Math.round(0.5 * Math.pow((quantosSucosBandecos + 1).toDouble(), 1.7) * 50).toInt()
            quantosSucosBandecos++
            TextQuantosSucos.text = quantosSucosBandecos.toString()
            textContador.text = contador.toString()
            buttonSucoBandeco.text = ("${Math.round(0.5 * Math.pow((quantosSucosBandecos + 1).toDouble(), 1.7) * 50).toInt()} - suco do bandeco")
        } else {
            Toast.makeText(this, "Você não tem nota o suficiente", Toast.LENGTH_SHORT).show()
        }
    }

    fun comprarHorasSono(view: android.view.View?) {
        if (contador >= (0.5 * Math.pow((quantasHorasSono + 1).toDouble(), 1.7) * 150).toInt()) {
            Toast.makeText(this, "Hora de sono comprada", Toast.LENGTH_SHORT).show()
            contador -= Math.round(0.5 * Math.pow((quantasHorasSono + 1).toDouble(), 1.7) * 150).toInt()
            quantasHorasSono++
            TextQuantasHorasSono.text = quantasHorasSono.toString()
            textContador.text = contador.toString()
            buttonHorasSono.text = ("${Math.round(0.5 * Math.pow((quantasHorasSono + 1).toDouble(), 1.7) * 150).toInt()} - horas de sono")
        } else {
            Toast.makeText(this, "Você não tem nota o suficiente", Toast.LENGTH_SHORT).show()
        }
    }

    fun comprarEsgoto(view: android.view.View?) {
        if (contador >= (0.5 * Math.pow((quantosEsgotos + 1).toDouble(), 1.7) * 200).toInt()) {
            Toast.makeText(this, "Esgoto comprado", Toast.LENGTH_SHORT).show()
            contador -= Math.round(0.5 * Math.pow((quantosEsgotos + 1).toDouble(), 1.7) * 200).toInt()
            quantosEsgotos++
            TextQuantosEsgotos.text = quantosEsgotos.toString()
            textContador.text = contador.toString()
            buttonEsgoto.text = ("${Math.round(0.5 * Math.pow((quantosEsgotos + 1).toDouble(), 1.7) * 200).toInt()} - esgoto")
        } else {
            Toast.makeText(this, "Você não tem nota o suficiente", Toast.LENGTH_SHORT).show()
        }
    }

    fun comprarAulaExtra(view: android.view.View?) {
        if (contador >= (0.5 * Math.pow((quantasAulasExtras + 1).toDouble(), 1.7) * 500).toInt()) {
            Toast.makeText(this, "Aula extra comprada", Toast.LENGTH_SHORT).show()
            contador -= Math.round(0.5 * Math.pow((quantasAulasExtras + 1).toDouble(), 1.7) * 500).toInt()
            quantasAulasExtras++
            TextQuantasAulasExtras.text = quantasAulasExtras.toString()
            textContador.text = contador.toString()
            buttonAulaExtra.text = ("${Math.round(0.5 * Math.pow((quantasAulasExtras + 1).toDouble(), 1.7) * 500).toInt()} - aula extra")
        } else {
            Toast.makeText(this, "Você não tem nota o suficiente", Toast.LENGTH_SHORT).show()
        }
    }
}