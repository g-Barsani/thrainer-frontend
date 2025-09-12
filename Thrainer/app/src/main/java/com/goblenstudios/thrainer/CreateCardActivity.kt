package com.goblenstudios.thrainer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.goblenstudios.thrainer.dtos.CreateCardDto
import com.goblenstudios.thrainer.repositories.CardRepository
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val inputQuestion = findViewById<EditText>(R.id.inputQuestion)
        val inputAnswer = findViewById<EditText>(R.id.inputAnswer)
        val btnAddCard = findViewById<Button>(R.id.btnAddCard)
        val cardRepository = CardRepository(RetrofitInstance.cardService)

        btnAddCard.setOnClickListener {
            val question = inputQuestion.text.toString().trim()
            val answer = inputAnswer.text.toString().trim()
            val deckId = intent.getLongExtra("deckId", 0L)

            if (question.isEmpty() || answer.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (deckId == 0L) {
                Toast.makeText(this, "Deck inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                val dto = CreateCardDto(question, answer, deckId)
                val result = withContext(Dispatchers.IO) { cardRepository.createCard(dto) }
                if (result.isSuccess) {
                    Toast.makeText(this@CreateCardActivity, "Carta criada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateCardActivity, "Erro ao criar carta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}