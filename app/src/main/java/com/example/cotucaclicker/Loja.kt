package com.example.cotucaclicker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.TextView

class Loja : MainActivity() {
    private lateinit var buttonSucoBandeco: Button
    private lateinit var buttonHorasSono: Button

    private lateinit var TextQuantosSucos: TextView
    private lateinit var TextQuantasHorasSono: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loja)

        buttonSucoBandeco = findViewById(R.id.buttonSucoBandeco)
        buttonHorasSono = findViewById(R.id.buttonHorasSono)

        buttonSucoBandeco.text = ("${(quantosSucosBandecos + 1) * 50} - suco do bandeco")
        buttonHorasSono.text = ("${(quantasHorasSono + 1) * 150} - horas de sono")

        TextQuantosSucos = findViewById(R.id.TextQuantosSucos)
        TextQuantasHorasSono = findViewById(R.id.TextQuantasHorasSono)

        TextQuantosSucos.text = quantosSucosBandecos.toString()
        TextQuantasHorasSono.text = quantasHorasSono.toString()

        findViewById<ImageView>(R.id.navigation_home).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.navigation_bosses).setOnClickListener {
            val intent = Intent(this, Bosses::class.java)
            startActivity(intent)
        }
    }

    fun comprarSucoBandeco(view: android.view.View?) {
        if (contador >= (quantosSucosBandecos + 1) * 50) {
            Toast.makeText(this, "Suco do bandeco comprado", Toast.LENGTH_SHORT).show()
            contador -= (quantosSucosBandecos + 1) * 50
            quantosSucosBandecos++
            TextQuantosSucos.text = quantosSucosBandecos.toString()
            textContador.text = contador.toString()
            buttonSucoBandeco.text = ("${(quantosSucosBandecos + 1) * 50} - suco do bandeco")
        } else {
            Toast.makeText(this, "Você não tem nota o suficiente", Toast.LENGTH_SHORT).show()
        }
    }

    fun comprarHorasSono(view: android.view.View?) {
        if (contador >= (quantasHorasSono + 1) * 150) {
            Toast.makeText(this, "Hora de sono comprada", Toast.LENGTH_SHORT).show()
            contador -= (quantasHorasSono + 1) * 150
            quantasHorasSono++
            TextQuantasHorasSono.text = quantasHorasSono.toString()
            textContador.text = contador.toString()
            buttonHorasSono.text = ("${(quantasHorasSono + 1) * 150} - horas de sono")
        } else {
            Toast.makeText(this, "Você não tem nota o suficiente", Toast.LENGTH_SHORT).show()
        }
    }
}
