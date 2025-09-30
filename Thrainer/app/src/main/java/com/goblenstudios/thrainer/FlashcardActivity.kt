package com.goblenstudios.thrainer

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FlashcardActivity : AppCompatActivity() {
    private val animationJobs = mutableMapOf<Int, Pair<Job?, Job?>>()

    private lateinit var tvQuestion: TextView
    private lateinit var tvAnswer: TextView
    private lateinit var btnShowAnswer: Button
    private lateinit var btnNext: Button
    private lateinit var imageView: ImageView

    // Dados de exemplo - substitua pelos seus dados reais
    private val flashcards = listOf(
        Pair("Qual é a capital do Brasil?", "Brasília BrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasíliaBrasília"),
        Pair("Em que ano foi descoberto o Brasil?", "1500"),
        Pair("Quem escreveu Dom Casmurro?", "Machado de Assis")
    )

    private var currentCardIndex = 0
    private var isAnswerVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_flashcard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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