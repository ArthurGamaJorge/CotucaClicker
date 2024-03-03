package com.example.cotucaclicker

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.animation.AnimatorSet

class Bosses : MainActivity() {
    private lateinit var vidaBossTextView: TextView
    private lateinit var nomeBossTextView: TextView
    private lateinit var textFalaBoss: TextView
    private lateinit var iconBossImageView: ImageView

    private lateinit var buttonGincana: Button
    private lateinit var buttonDormindoSampaio: Button
    private lateinit var tempoBoss: TextView

    private val handler = Handler()
    private var contadorTempo = 30
    private var fase2Ativa = false
    private var originalHeight = 0
    private lateinit var bossAnimationX: ObjectAnimator
    private lateinit var bossAnimationY: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bosses)

        nomeBossTextView = findViewById(R.id.NomeBoss)
        vidaBossTextView = findViewById(R.id.VidaBoss)
        textFalaBoss = findViewById(R.id.textFalaBoss)
        iconBossImageView = findViewById(R.id.iconBoss)

        buttonGincana = findViewById(R.id.buttonGincana)
        buttonDormindoSampaio = findViewById(R.id.buttonDormindoSampaio)
        tempoBoss = findViewById(R.id.tempoBoss)

        val simoneLayout: LinearLayout = findViewById(R.id.Simone)
        val chicoLayout: LinearLayout = findViewById(R.id.Chico)
        val sampaioLayout: LinearLayout = findViewById(R.id.Sampaio)

        handler.post(atualizarCooldownSampaio)
        handler.post(atualizarCooldownGincana)
        handler.post(atualizarContadorBoss)
        handler.post(atualizarContadorTempoBoss)

        iconBossImageView.setOnClickListener {
            val scaleX = ObjectAnimator.ofFloat(iconBossImageView, View.SCALE_X, 0.9f)
            val scaleY = ObjectAnimator.ofFloat(iconBossImageView, View.SCALE_Y, 0.9f)
            val scaleDown = AnimatorSet().apply {
                play(scaleX).with(scaleY)
                duration = 100
            }

            val scaleXBack = ObjectAnimator.ofFloat(iconBossImageView, View.SCALE_X, 1.0f)
            val scaleYBack = ObjectAnimator.ofFloat(iconBossImageView, View.SCALE_Y, 1.0f)
            val scaleUp = AnimatorSet().apply {
                play(scaleXBack).with(scaleYBack)
                startDelay = 100
                duration = 100
            }

            scaleDown.start()
            scaleUp.start()
            clicarBoss(it)
        }

        simoneLayout.setOnClickListener {
            resetBoss("Simone", 2500)
        }

        chicoLayout.setOnClickListener {
            resetBoss("Chico", 5000)
        }

        sampaioLayout.setOnClickListener {
            resetBoss("Sampaio", 13000)
        }

        buttonGincana.setOnClickListener {
            ativarGincana(it)
        }

        findViewById<ImageView>(R.id.navigation_loja).setOnClickListener {
            startActivity(Intent(this, Loja::class.java))
        }

        findViewById<ImageView>(R.id.navigation_home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun resetBoss(nome: String, vidaTotal: Int) {
        nomeBossTextView.text = "Boss: $nome"
        vidaBossTextView.text = "$vidaTotal/$vidaTotal"
        textFalaBoss.text = when (nome) {
            "Simone" -> "Para de clicar"
            "Chico" -> "*Chico começa a voar*"
            else -> "Dormindo na aula do Sampaio?"
        }
        iconBossImageView.setImageResource(resources.getIdentifier(nome.toLowerCase(), "drawable", packageName))
        fase2Ativa = false
        contadorTempo = 30
    }

    private val atualizarContadorBoss = object : Runnable {
        override fun run() {
            if (nomeBossTextView.text != "Boss: não escolhido") {
                val vida = vidaBossTextView.text.split('/')
                if (vida[0].toInt() > 0) {
                    vidaBossTextView.text = "${vida[0].toInt() - (quantasHorasSono * multiplicadorPassivo).toInt()}/${vida[1]}"
                }
            }
            handler.postDelayed(this, 500)
        }
    }

    private val atualizarContadorTempoBoss = object : Runnable {
        override fun run() {
            if (nomeBossTextView.text != "Boss: não escolhido") {
                val vida = vidaBossTextView.text.split('/')
                if (vida[0].toInt() > 0) {
                    if (contadorTempo > 0) {
                        contadorTempo--
                        tempoBoss.text = contadorTempo.toString()
                    } else {
                        vidaBossTextView.text = "${vida[1]}/${vida[1]}"
                        contadorTempo = 30
                    }
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val atualizarCooldownSampaio = object : Runnable {
        override fun run() {
            if (cooldownSampaio > 0) {
                buttonDormindoSampaio.text = cooldownSampaio.toString()
                if (cooldownSampaio == 10) {
                    buttonDormindoSampaio.setBackgroundColor(0xE2312C.toInt())
                }
            } else {
                buttonDormindoSampaio.text = "Zz"
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val atualizarCooldownGincana = object : Runnable {
        override fun run() {
            if (cooldownGincana > 0) {
                buttonGincana.text = cooldownGincana.toString()
                if (cooldownGincana == 40) {
                    buttonGincana.setBackgroundColor(0xE2312C.toInt())
                }
            } else {
                buttonGincana.text = "Gincana"
            }
            handler.postDelayed(this, 1000)
        }
    }

    private fun startBossAnimation(duracao: Long, distanciaX: Float, distanciaY: Float) {
        bossAnimationX = ObjectAnimator.ofFloat(iconBossImageView, "translationX", -distanciaX, distanciaX).apply {
            duration = duracao
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        bossAnimationY = ObjectAnimator.ofFloat(iconBossImageView, "translationY", -distanciaY, distanciaY).apply {
            duration = duracao
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
    }

    private fun stopBossAnimation() {
        if (::bossAnimationX.isInitialized && ::bossAnimationY.isInitialized &&
            bossAnimationX.isRunning && bossAnimationY.isRunning) {
            bossAnimationX.cancel()
            bossAnimationY.cancel()
        }
    }

    fun clicarBoss(view: View) {
        if (nomeBossTextView.text != "Boss: não escolhido") {
            val vida = vidaBossTextView.text.split('/')
            vidaBossTextView.text =
                "${vida[0].toInt() - ((quantosSucosBandecos + 1) * multiplicadorAtivo).toInt()}/${vida[1].toInt()}"

            if (vida[0].toInt() - ((quantosSucosBandecos + 1) * multiplicadorAtivo).toInt() < vida[1].toInt() / 2) {
                when (nomeBossTextView.text) {
                    "Boss: Simone" -> textFalaBoss.text = "*Começa a bater na mesa*"
                    "Boss: Chico" -> textFalaBoss.text = "EU DISSE PARA LER A APOSTILA"
                    "Boss: Sampaio" -> {
                        textFalaBoss.text = "Celulares na aula do Sampaio?"
                        iconBossImageView.setImageResource(R.drawable.celular)
                    }
                }
            }

            if (vida[0].toInt() < vida[1].toInt() * 0.2 && !fase2Ativa) {
                val layoutParams = iconBossImageView.layoutParams
                originalHeight = iconBossImageView.height
                if (nomeBossTextView.text == "Boss: Chico" || nomeBossTextView.text == "Boss: Sampaio") {
                    if (nomeBossTextView.text == "Boss: Chico") {
                        layoutParams.width = (originalHeight * 0.4).toInt()
                        layoutParams.height = (originalHeight * 0.4).toInt()
                        startBossAnimation(200, 100.0f, 100.0f)
                    }
                    if (nomeBossTextView.text == "Boss: Sampaio") {
                        layoutParams.width = (originalHeight * 0.2).toInt()
                        layoutParams.height = (originalHeight * 0.2).toInt()
                        startBossAnimation(100, 120.0f, 50.0f)
                    }
                    iconBossImageView.layoutParams = layoutParams
                    fase2Ativa = true
                }
            }

            if (vida[0].toInt() - ((quantosSucosBandecos + 1) * multiplicadorAtivo).toInt() <= 0) {
                when (nomeBossTextView.text) {
                    "Boss: Simone" -> {
                        textFalaBoss.text = "Finalmente silêncio... | +0.05 multiplicador passivo"
                        multiplicadorAtivo += 0.05f
                    }
                    "Boss: Chico" -> {
                        textFalaBoss.text = "Você usou as técnicas de programação | +0.05 multiplicador passivo"
                        multiplicadorPassivo += 0.1f
                        var layoutParams = iconBossImageView.layoutParams
                        layoutParams.width = originalHeight
                        layoutParams.height = originalHeight
                        iconBossImageView.layoutParams = layoutParams
                    }
                    "Boss: Sampaio" -> {
                        textFalaBoss.text = "Você conseguiu guardar a tempo... | +0.1 multiplicador ativo e passivo"
                        multiplicadorAtivo += 0.1f
                        multiplicadorPassivo += 0.1f
                        var layoutParams = iconBossImageView.layoutParams
                        layoutParams.width = originalHeight
                        layoutParams.height = originalHeight
                        iconBossImageView.layoutParams = layoutParams
                    }
                }
                stopBossAnimation()
                fase2Ativa = false
                contadorTempo = 30;
                vidaBossTextView.text = "${vida[1].toInt()}/${vida[1].toInt()}"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
