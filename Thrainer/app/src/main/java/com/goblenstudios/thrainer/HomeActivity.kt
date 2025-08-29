package com.goblenstudios.thrainer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.goblenstudios.thrainer.StudyRoom.StudyRoomActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botão para direcionar para StudyRoomActivity
        val btnGoToStudyRoom = findViewById<Button>(R.id.btnGoToStudyRoom)
        btnGoToStudyRoom.setOnClickListener {
            startActivity(Intent(this, StudyRoomActivity::class.java))
            overridePendingTransition(0, 0)
            finish() // Encerra a atividade atual e retorna para a anterior
        }

        // Botão para direcionar para CommunityActivity
        val btnGoToCommunity = findViewById<Button>(R.id.btnGoToCommunity)
        btnGoToCommunity.setOnClickListener {
            startActivity(Intent(this, CommunityActivity::class.java))
            overridePendingTransition(0, 0)
            finish() // Encerra a atividade atual e retorna para a anterior
        }

    }
}