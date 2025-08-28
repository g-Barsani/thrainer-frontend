package com.goblenstudios.thrainer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StudyRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_study_room)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // AQUI !!!
        val btnReturnToHome = findViewById<Button>(R.id.btnReturnToHome)

        val btnLeftCenter = findViewById<Button>(R.id.btnLeftCenter)

        val btnRightCenter = findViewById<Button>(R.id.btnRightCenter)

        val leftOverlay = findViewById<FrameLayout>(R.id.leftOverlay)

        val btnCloseOverlay = findViewById<Button>(R.id.btnCloseOverlay)


        btnLeftCenter.setOnClickListener {
            leftOverlay.visibility = View.VISIBLE
            btnReturnToHome.visibility = View.INVISIBLE
            btnLeftCenter.visibility = View.INVISIBLE
        }

        btnRightCenter.setOnClickListener {
            startActivity(Intent(this, FlashcardActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        btnCloseOverlay.setOnClickListener {
            leftOverlay.visibility = View.GONE
            btnReturnToHome.visibility = View.VISIBLE
            btnLeftCenter.visibility = View.VISIBLE
        }


        btnReturnToHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)) // Inicia a MainActivity
            overridePendingTransition(0, 0)
            finish() // Finaliza a atividade atual e retorna para a anterior (MainActivity)
        }
    }
}