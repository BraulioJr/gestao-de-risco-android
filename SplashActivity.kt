package com.example.gestaoderisco.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gestaoderisco.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tvSlogan = findViewById<TextView>(R.id.tvSlogan)
        
        // Animação Enterprise: Suave, silenciosa e elegante (Fade In + Slide Up)
        // Utiliza o recurso R.anim.slogan_anim já existente no projeto
        val elegantAnim = AnimationUtils.loadAnimation(this, R.anim.slogan_anim)
        tvSlogan.startAnimation(elegantAnim)

        // Navegação rápida (2 segundos): Respeita o tempo do usuário corporativo
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)
    }
}