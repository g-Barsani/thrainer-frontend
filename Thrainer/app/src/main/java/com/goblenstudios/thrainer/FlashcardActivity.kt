

package com.goblenstudios.thrainer

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.goblenstudios.thrainer.dtos.ReturnCardDto
import com.goblenstudios.thrainer.repositories.UserCardRepository
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlashcardActivity : AppCompatActivity() {
    private lateinit var tvQuestion: TextView
    private lateinit var btnShowAnswer: Button
    private lateinit var btnAcertou: Button
    private lateinit var btnErrou: Button
    private lateinit var userCardRepository: UserCardRepository
    private var cards: List<ReturnCardDto> = emptyList()
    private var currentIndex = 0
    private var userId: Long = 0L
    private var deckId: Long = 0L
    private var isShowingAnswer = false // Nova variável para controlar o estado


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvQuestion = findViewById(R.id.tvQuestion)
        btnShowAnswer = findViewById(R.id.btnShowAnswer)
        btnAcertou = findViewById(R.id.btnAcertou)
        btnErrou = findViewById(R.id.btnErrou)
        userCardRepository = UserCardRepository(RetrofitInstance.userCardService)

        // Obter userId das SharedPreferences
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        userId = prefs.getLong("user_id", 0L)
        deckId = intent.getLongExtra("deckId", 0L)
        if (userId == 0L || deckId == 0L) {
            Toast.makeText(this, "Usuário ou deck inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Buscar cards para praticar
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) { userCardRepository.practiceCardsByDeck(userId, deckId) }
            if (result.isSuccess) {
                cards = result.getOrNull() ?: emptyList()
                if (cards.isEmpty()) {
                    tvQuestion.text = "Nenhum card para praticar."
                    btnShowAnswer.isEnabled = false
                    btnAcertou.isEnabled = false
                    btnErrou.isEnabled = false
                } else {
                    currentIndex = 0
                    showCurrentCard()
                }
            } else {
                Toast.makeText(this@FlashcardActivity, "Erro ao buscar cards para prática", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnShowAnswer.setOnClickListener {
            if (cards.isNotEmpty() && currentIndex < cards.size) {
                if (!isShowingAnswer) {
                    // Mostrar resposta
                    tvQuestion.text = cards[currentIndex].answer
                    btnShowAnswer.text = "Mostrar pergunta"
                    isShowingAnswer = true
                } else {
                    // Mostrar pergunta
                    tvQuestion.text = cards[currentIndex].question
                    btnShowAnswer.text = "Mostrar resposta"
                    isShowingAnswer = false
                }
            }
        }


        btnAcertou.setOnClickListener {
            marcarCard(true)
        }
        btnErrou.setOnClickListener {
            marcarCard(false)
        }
    }

    private fun showCurrentCard() {
        if (cards.isEmpty() || currentIndex >= cards.size) {
            tvQuestion.text = "Fim dos cards!"
            btnShowAnswer.isEnabled = false
            btnAcertou.isEnabled = false
            btnErrou.isEnabled = false
            return
        }
        val card = cards[currentIndex]
        tvQuestion.text = card.question
        btnShowAnswer.text = "Mostrar resposta"
        isShowingAnswer = false
        btnShowAnswer.isEnabled = true
        btnAcertou.isEnabled = true
        btnErrou.isEnabled = true
    }

    private fun nextCard() {
        currentIndex++
        if (currentIndex < cards.size) {
            showCurrentCard()
        } else {
            tvQuestion.text = "Parabéns! Você terminou todos os cards."
            btnShowAnswer.isEnabled = false
            btnAcertou.isEnabled = false
            btnErrou.isEnabled = false

        }
    }

    private fun marcarCard(acertou: Boolean) {
        if (cards.isEmpty() || currentIndex >= cards.size) return
        val cardId = cards[currentIndex].idCard

        // Log dos dados que serão enviados
        println("Tentando marcar card - userId: $userId, cardId: $cardId, acertou: $acertou")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    if (acertou) {
                        println("Chamando markCardAsCorrect...")
                        userCardRepository.markCardAsCorrect(userId, cardId)
                    } else {
                        println("Chamando markCardAsWrong...")
                        userCardRepository.markCardAsWrong(userId, cardId)
                    }
                }
                if (result.isSuccess) {
                    Toast.makeText(this@FlashcardActivity, if (acertou) "Marcado como acertou" else "Marcado como errou", Toast.LENGTH_SHORT).show()
                    nextCard()
                } else {
                    val exception = result.exceptionOrNull()
                    val errorMessage = exception?.message ?: "Erro desconhecido"
                    Toast.makeText(this@FlashcardActivity, "Erro ao marcar card: $errorMessage", Toast.LENGTH_LONG).show()
                    println("Erro ao marcar card: $errorMessage")

                    // Log detalhado do erro
                    println("Erro detalhado ao marcar card:")
                    println("- Tipo da exceção: ${exception?.javaClass?.simpleName}")
                    println("- Mensagem: ${exception?.message}")
                    println("- Causa: ${exception?.cause}")
                    if (exception != null) {
                        println("- Stack trace completo:")
                        exception.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@FlashcardActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                println("Exceção capturada ao marcar card:")
                println("- Tipo: ${e.javaClass.simpleName}")
                println("- Mensagem: ${e.message}")
                println("- Causa: ${e.cause}")
                println("- Stack trace completo:")
                e.printStackTrace()
            }
        // Aplica fade in na activity
        overridePendingTransition(R.drawable.fade_in, R.drawable.fade_out)
        initViews()
        setupClickListeners()
        showCurrentCard()
    }

    private fun initViews() {
        tvQuestion = findViewById(R.id.tvFlashcardQuestion)
        tvAnswer = findViewById(R.id.tvAnswer)
        btnShowAnswer = findViewById(R.id.btnShowAnswer)
        btnNext = findViewById(R.id.btnNext)
        imageView = findViewById(R.id.imageView)
    }

    private fun setupClickListeners() {
        btnShowAnswer.setOnClickListener {
            if (!isAnswerVisible) {
                showAnswer()
            } else {
                hideAnswer()
            }
        }

        btnNext.setOnClickListener {
            nextCard()
        }
    }

    private fun showCurrentCard() {
        isAnswerVisible = false
        tvAnswer.visibility = TextView.GONE
        btnShowAnswer.text = "Mostrar resposta"

        // Muda para imagem de pergunta
        imageView.setImageResource(R.drawable.question_balloon)

        val currentQuestion = flashcards[currentCardIndex].first
        typeWriter(tvQuestion, currentQuestion)
    }

    private fun showAnswer() {
        isAnswerVisible = true
        tvAnswer.visibility = TextView.VISIBLE
        tvQuestion.visibility = TextView.INVISIBLE
        btnShowAnswer.text = "Ocultar resposta"

        // Muda para imagem de resposta - use sua imagem de resposta aqui
        imageView.setImageResource(R.drawable.answer_balloon) // Substitua pelo drawable da resposta

        val currentAnswer = flashcards[currentCardIndex].second
        typeWriter(tvAnswer, currentAnswer)
    }

    private fun hideAnswer() {
        isAnswerVisible = false
        tvAnswer.visibility = TextView.GONE
        tvQuestion.visibility= TextView.VISIBLE
        btnShowAnswer.text = "Mostrar resposta"

        // Volta para imagem de pergunta
        imageView.setImageResource(R.drawable.question_balloon)

        // Cancela a animação da resposta
        animationJobs[tvAnswer.id]?.first?.cancel()
        animationJobs[tvAnswer.id]?.second?.cancel()
    }

    private fun nextCard() {
        currentCardIndex = (currentCardIndex + 1) % flashcards.size
        tvQuestion.visibility = TextView.VISIBLE
        showCurrentCard()
    }

    private fun typeWriter(textView: TextView, text: String) {
        // Cancela animações anteriores
        animationJobs[textView.id]?.first?.cancel()
        animationJobs[textView.id]?.second?.cancel()

        val typingJob = CoroutineScope(Dispatchers.Main).launch {
            textView.text = "" // Limpa o texto antes de começar
            val stringBuilder = StringBuilder()
            text.forEach { char ->
                stringBuilder.append(char)
                textView.text = stringBuilder.toString()
                delay(50) // Velocidade da digitação
            }
        }
        animationJobs[textView.id] = Pair(typingJob, null)
    }



    override fun onDestroy() {
        super.onDestroy()
        animationJobs.values.forEach { (typingJob, blinkingJob) ->
            typingJob?.cancel()
            blinkingJob?.cancel()

        }
    }
}