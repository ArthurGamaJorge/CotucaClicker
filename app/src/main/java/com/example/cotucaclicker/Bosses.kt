package com.example.cotucaclicker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class Bosses : MainActivity() {
    private lateinit var vidaBossTextView: TextView
    private lateinit var nomeBossTextView: TextView
    private lateinit var textFalaBoss: TextView
    private lateinit var iconBossImageView: ImageView

    private lateinit var buttonGincana: Button
    private lateinit var buttonDormindoSampaio: Button
    private lateinit var tempoBoss: TextView

    private val handler = Handler()
    private var contadorTempo = 30 + (quantasAulasExtras/2).toInt()
    private var fase2Ativa = false
    private var fase3Ativa = false
    private var originalHeight = 0
    private lateinit var bossAnimation: ObjectAnimator
    private var isBossFading = false
    private var posicaoAtualY = 0f
    private var posicaoAtualX = 0f

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
        val barbarinhaLayout: LinearLayout = findViewById(R.id.Barbarinha)
        val trigoLayout: LinearLayout = findViewById(R.id.Trigo)
        val AnaPaulaLayout: LinearLayout = findViewById(R.id.AnaPaula)
        val JodirLayout: LinearLayout = findViewById(R.id.Jodir)
        val CelioLayout: LinearLayout = findViewById(R.id.Celio)
        val chicoLayout: LinearLayout = findViewById(R.id.Chico)
        val sampaioLayout: LinearLayout = findViewById(R.id.Sampaio)
        val malignoLayout: LinearLayout = findViewById(R.id.Maligno)

        handler.post(atualizarCooldownSampaio)
        handler.post(atualizarCooldownGincana)
        handler.post(atualizarContadorBoss)
        handler.post(atualizarContadorTempoBoss)

        iconBossImageView.setOnClickListener {

            if (!isBlocked && !isBossFading) {
                val currentTime = System.currentTimeMillis()
                val interval = currentTime - lastClickTime
                if (clickTimes.size >= 2) {
                    val lastInterval =
                        clickTimes[clickTimes.size - 1] - clickTimes[clickTimes.size - 2]

                    if (Math.abs(interval - lastInterval) <= 5) {
                        consecutiveClicks++
                    } else {
                        consecutiveClicks = 0
                    }

                    if (consecutiveClicks >= 10) {
                        bloquearCliques()
                        val snackbar = Snackbar.make(findViewById(android.R.id.content), "Uso de auto clicker detectado -> Clicks bloqueados por 60 segundos, sair da tela aumentará o tempo", Snackbar.LENGTH_LONG)
                        snackbar.duration = 60000
                        snackbar.show()
                        return@setOnClickListener
                    }
                }
                clickTimes.add(currentTime)
                lastClickTime = currentTime

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
        }

        simoneLayout.setOnClickListener {
            resetBoss("Simone", 5000)
        }

        barbarinhaLayout.setOnClickListener {
            resetBoss("Barbarinha", 12000)
        }

        trigoLayout.setOnClickListener {
            resetBoss("Trigo", 25000)
        }

        AnaPaulaLayout.setOnClickListener {
            resetBoss("Ana Paula", 50000)
        }

        JodirLayout.setOnClickListener {
            resetBoss("Jodir", 90000)
        }

        CelioLayout.setOnClickListener {
            resetBoss("Celio", 200000)
        }

        chicoLayout.setOnClickListener {
            resetBoss("Chico", 425000)
        }

        sampaioLayout.setOnClickListener {
            resetBoss("Sampaio", 1200000)
        }

        malignoLayout.setOnClickListener {
            resetBoss("Maligno", 3000000)
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

        findViewById<ImageView>(R.id.navigation_online).setOnClickListener {
            startActivity(Intent(this, Rank::class.java))
        }
    }

    private fun resetBoss(nome: String, vidaTotal: Int) {
        originalHeight = iconBossImageView.height
        nomeBossTextView.text = "Boss: $nome"
        vidaBossTextView.text = "$vidaTotal/$vidaTotal"
        textFalaBoss.text = when (nome) {
            "Simone" -> "Para de clicar"
            "Barbarinha" -> "Eu quero a redação perfeita"
            "Trigo" -> "Hi, I love Taylor Swift"
            "Ana Paula" -> "Isso da cancer ein"
            "Jodir" -> "Vamos fazer um mergulho"
            "Celio" -> "Eu sou a verdade absoluta"
            "Chico" -> "*Chico começa a voar*"
            "Sampaio" -> "Dormindo na aula do Sampaio?"
            "Maligno" -> "Olá Malignicios"
            else -> "Boss inválido"
        }
        iconBossImageView.setImageResource(resources.getIdentifier(nome.replace(" ", "").toLowerCase(), "drawable", packageName))
        fase2Ativa = false
        fase3Ativa = false
        contadorTempo = 30 + (quantasAulasExtras/2).toInt()
        tempoBoss.text = contadorTempo.toString()
    }

    private val atualizarContadorBoss = object : Runnable {
        override fun run() {
            if (nomeBossTextView.text != "Boss: não escolhido" && !isBossFading) {
                val dano = (quantasHorasSono * multiplicadorPassivo).toInt()
                sofrerDano(dano)
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
                        contadorTempo = 30 + (quantasAulasExtras/2).toInt()
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

    private fun startBossAnimation(duracao: Long, raio: Float, originalHeight : Float) {
        posicaoAtualX = originalHeight
        posicaoAtualY = iconBossImageView.y + originalHeight

        val path = Path().apply {
            moveTo(posicaoAtualX + raio, posicaoAtualY)
            addCircle(posicaoAtualX, posicaoAtualY, raio, Path.Direction.CW)
        }

        bossAnimation = ObjectAnimator.ofFloat(iconBossImageView, View.X, View.Y, path).apply {
            duration = duracao
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }

        bossAnimation.start()
    }

    private fun stopBossAnimation() {
        if (::bossAnimation.isInitialized && bossAnimation.isRunning) {
            bossAnimation.cancel()

            iconBossImageView.x = posicaoAtualX
            iconBossImageView.y = posicaoAtualY
        }
    }

    private fun startDeathAnimation() {
        isBossFading = true
        val fadeOut = ObjectAnimator.ofFloat(iconBossImageView, View.ALPHA, 1f, 0f).apply {
            duration = 500
        }

        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                resetBoss(nomeBossTextView.text.toString().replace("Boss: ", ""), vidaBossTextView.text.split('/')[1].toInt())
                val fadeIn = ObjectAnimator.ofFloat(iconBossImageView, View.ALPHA, 0f, 1f).apply {
                    duration = 500
                }
                fadeIn.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        isBossFading = false
                    }
                })
                fadeIn.start()
            }
        })

        fadeOut.start()
    }

    fun clicarBoss(view: View) {
        if (nomeBossTextView.text != "Boss: não escolhido") {
            val dano = ((quantosSucosBandecos + 1) * multiplicadorAtivo).toInt()
            sofrerDano(dano)
        }
    }

    fun sofrerDano(dano: Int) {
        val vida = vidaBossTextView.text.split('/')
        val vidaAtual = vida[0].toInt() - dano
        val vidaTotal = vida[1].toInt()
        vidaBossTextView.text = "$vidaAtual/$vidaTotal"

        if (vidaAtual <= vidaTotal / 2) {
            when (nomeBossTextView.text) {
                "Boss: Simone" -> textFalaBoss.text = "*Começa a bater na mesa*"
                "Boss: Barbarinha" -> textFalaBoss.text = "Qual é esse pokemon?"
                "Boss: Trigo" -> textFalaBoss.text = "LOOK! A monkey!"
                "Boss: Ana Paula" -> textFalaBoss.text = "ô inferno (falo inferno por causa de Jesus)"
                "Boss: Jodir" -> textFalaBoss.text = "*Plopt*"
                "Boss: Celio" -> textFalaBoss.text = "*Alarme começa a tocar*"
                "Boss: Chico" -> textFalaBoss.text = "EU DISSE PARA LER A APOSTILA"
                "Boss: Sampaio" -> {
                    textFalaBoss.text = "Celulares na aula do Sampaio?"
                    iconBossImageView.setImageResource(R.drawable.celular)
                }
                "Boss: Maligno" -> {
                    if (!fase3Ativa) {
                        textFalaBoss.text = "Parem de estudar java e foquem no subway money"
                        iconBossImageView.setImageResource(R.drawable.maligniciofase2)
                    }
                }
            }
        }

        if (vidaAtual < vidaTotal * 0.2 && !fase2Ativa) {
            val layoutParams = iconBossImageView.layoutParams
            originalHeight = iconBossImageView.height
            if (nomeBossTextView.text == "Boss: Chico" || nomeBossTextView.text == "Boss: Sampaio" || nomeBossTextView.text == "Boss: Maligno") {
                when (nomeBossTextView.text) {
                    "Boss: Chico" -> {
                        startBossAnimation(200 + (quantosEsgotos * 10).toLong(), 200.0f, (originalHeight * 0.5).toFloat())
                        layoutParams.width = (originalHeight * 0.4).toInt()
                        layoutParams.height = (originalHeight * 0.4).toInt()
                    }
                    "Boss: Sampaio" -> {
                        startBossAnimation(100 + (quantosEsgotos * 10).toLong(), 300.0f, (originalHeight * 0.6).toFloat())
                        layoutParams.width = (originalHeight * 0.2).toInt()
                        layoutParams.height = (originalHeight * 0.2).toInt()
                    }
                    "Boss: Maligno" -> {
                        startBossAnimation(50 + (quantosEsgotos * 10).toLong(), 500.0f, (originalHeight * 0.6).toFloat())
                        layoutParams.width = (originalHeight * 0.2).toInt()
                        layoutParams.height = (originalHeight * 0.2).toInt()
                        textFalaBoss.text = "ele está pagando DEMAIS"
                        iconBossImageView.setImageResource(R.drawable.maligniciofase3)
                        fase3Ativa = true
                    }
                }
                iconBossImageView.layoutParams = layoutParams
                fase2Ativa = true
            }
        }

        if (vidaAtual <= 0) {
            // Lógica para a morte do boss
            var multiplicadorPassivoAtual = multiplicadorPassivo
            var multiplicadorAtivoAtual = multiplicadorAtivo
            var multiplicadorAtivoTemp = 0.0f
            var multiplicadorPassivoTemp = 0.0f

            if (cooldownGincana >= 40) {
                multiplicadorAtivoAtual -= 2
                multiplicadorPassivoAtual -= 2
            }
            if (cooldownSampaio >= 10) {
                multiplicadorPassivoAtual -= 2
            }

            when (nomeBossTextView.text) {
                "Boss: Simone" -> if (multiplicadorAtivoAtual < 5) multiplicadorAtivoTemp += 0.05f
                "Boss: Barbarinha" -> if (multiplicadorPassivoAtual < 10) multiplicadorPassivoTemp += 0.05f
                "Boss: Trigo" -> if (multiplicadorPassivoAtual < 15) multiplicadorAtivoTemp += 0.05f
                "Boss: Ana Paula" -> if (multiplicadorPassivoAtual < 20) multiplicadorPassivoTemp += 0.05f
                "Boss: Jodir" -> if (multiplicadorAtivoAtual < 25) multiplicadorAtivoTemp += 0.05f
                "Boss: Celio" -> if (multiplicadorPassivoAtual < 30) multiplicadorPassivoTemp += 0.1f
                "Boss: Chico" -> if (multiplicadorAtivoAtual < 35) multiplicadorAtivoTemp += 0.1f
                "Boss: Sampaio" -> {
                    if (multiplicadorPassivoAtual < 40 && multiplicadorAtivoAtual < 40) {
                        multiplicadorPassivoTemp += 0.1f
                        multiplicadorAtivoTemp += 0.1f
                    } else {
                        if (multiplicadorPassivoAtual < 40) {
                            multiplicadorPassivoTemp += 0.1f
                        } else if (multiplicadorAtivoAtual < 40) {
                            multiplicadorAtivoTemp += 0.1f
                        }
                    }
                    iconBossImageView.setImageResource(R.drawable.sampaio)
                }
                "Boss: Maligno" -> {
                    if (multiplicadorPassivoAtual < 45 && multiplicadorAtivoAtual < 45) {
                        multiplicadorPassivoTemp += 0.15f
                        multiplicadorAtivoTemp += 0.15f
                    } else {
                        if (multiplicadorPassivoAtual < 45) {
                            multiplicadorPassivoTemp += 0.15f
                        } else if (multiplicadorAtivoAtual < 45) {
                            multiplicadorAtivoTemp += 0.15f
                        }
                    }
                    iconBossImageView.setImageResource(R.drawable.maligno)
                }
            }
            val layoutParams = iconBossImageView.layoutParams
            layoutParams.width = originalHeight
            layoutParams.height = originalHeight
            iconBossImageView.layoutParams = layoutParams
            stopBossAnimation()
            startDeathAnimation()

            multiplicadorAtivo += multiplicadorAtivoTemp
            multiplicadorPassivo += multiplicadorPassivoTemp

            when {
                multiplicadorAtivoTemp > 0 && multiplicadorPassivoTemp > 0 -> textFalaBoss.text = "+ $multiplicadorPassivoTemp multiplicador passivo e ativo"
                multiplicadorPassivoTemp > 0 -> textFalaBoss.text = "+ $multiplicadorPassivoTemp multiplicador passivo"
                multiplicadorAtivoTemp > 0 -> textFalaBoss.text = "+ $multiplicadorAtivoTemp multiplicador ativo"
                else -> textFalaBoss.text = "Não há mais multiplicador para ganhar de mim"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}