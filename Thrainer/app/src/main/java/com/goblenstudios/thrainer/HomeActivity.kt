package com.goblenstudios.thrainer

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.goblenstudios.thrainer.StudyRoom.StudyRoomActivity
import kotlin.random.Random

class HomeActivity : AppCompatActivity() {
    private var pulseStudyAnimator: ObjectAnimator? = null
    private var pulseCommunityAnimator: ObjectAnimator? = null
    private var pulseLogoutAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets -> insets }

        // Transição de entrada suave
        overridePendingTransition(R.drawable.fade_in, R.drawable.fade_out)

        // Botão para StudyRoomActivity
        val btnGoToStudyRoom = findViewById<ImageButton>(R.id.btnGoToStudyRoom)
        btnGoToStudyRoom.setOnClickListener {
            startActivity(Intent(this, StudyRoomActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // Botão para CommunityActivity
        val btnGoToCommunity = findViewById<ImageButton>(R.id.btnGoToCommunity)
        btnGoToCommunity.setOnClickListener {
            startActivity(Intent(this, CommunityActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // Botão para Logout
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            prefs.edit()
                .remove("auth_token")
                .remove("user_id")
                .remove("user_name")
                .remove("user_email")
                .remove("user_is_public")
                .apply()

            println("Token removido: ${prefs.getString("auth_token", "Nulo")}")

            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // Cria os animadores de pulse com delays aleatórios
        pulseStudyAnimator = btnGoToStudyRoom.createPulseAnimator()
        pulseCommunityAnimator = btnGoToCommunity.createPulseAnimator()
        pulseLogoutAnimator = btnLogout.createPulseAnimator()

        // Atrasos aleatórios (0 a 600 ms)
        pulseStudyAnimator?.startDelay = Random.nextLong(0, 600)
        pulseCommunityAnimator?.startDelay = Random.nextLong(0, 600)
        pulseLogoutAnimator?.startDelay = Random.nextLong(0, 600)
    }

    override fun onResume() {
        super.onResume()
        pulseStudyAnimator?.start()
        pulseCommunityAnimator?.start()
        pulseLogoutAnimator?.start()
    }

    override fun onPause() {
        super.onPause()
        pulseStudyAnimator?.cancel()
        pulseCommunityAnimator?.cancel()
        pulseLogoutAnimator?.cancel()
    }

    // Função de extensão para criar o efeito pulse
    private fun View.createPulseAnimator(
        startScale: Float = 0.96f,
        endScale: Float = 1.04f,
        durationMs: Long = 600L
    ): ObjectAnimator {
        val pX = PropertyValuesHolder.ofFloat(View.SCALE_X, startScale, endScale)
        val pY = PropertyValuesHolder.ofFloat(View.SCALE_Y, startScale, endScale)
        return ObjectAnimator.ofPropertyValuesHolder(this, pX, pY).apply {
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            duration = durationMs
        }
    }
}
