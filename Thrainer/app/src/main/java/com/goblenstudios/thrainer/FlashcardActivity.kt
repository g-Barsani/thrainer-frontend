package com.goblenstudios.thrainer

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Button
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
    private lateinit var btnShowAnswer: Button
    private lateinit var btnNext: Button

    // Dados de exemplo - substitua pelos seus dados reais
    private val flashcards = listOf(
        "Qual é a capital do Brasil?",
        "Em que ano foi descoberto o Brasil?",
        "Quem escreveu Dom Casmurro?"
    )

    private var currentCardIndex = 0


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
        btnShowAnswer = findViewById(R.id.btnShowAnswer)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun setupClickListeners() {
        btnShowAnswer.setOnClickListener {
            // Implementar mostrar resposta
        }

        btnNext.setOnClickListener {
            nextCard()
        }
    }

    private fun showCurrentCard() {
        val currentQuestion = flashcards[currentCardIndex]
        typeWriter(tvQuestion, currentQuestion)
    }

    private fun nextCard() {
        currentCardIndex = (currentCardIndex + 1) % flashcards.size
        showCurrentCard()
    }

    private fun typeWriter(textView: TextView, text: String) {
        // Cancela animações anteriores
        animationJobs[textView.id]?.first?.cancel()
        animationJobs[textView.id]?.second?.cancel()

        val typingJob = CoroutineScope(Dispatchers.Main).launch {
            val stringBuilder = StringBuilder()
            text.forEach { char ->
                stringBuilder.append(char)
                textView.text = stringBuilder.toString()
                delay(50) // Velocidade da digitação
            }
            // Inicia cursor piscante
            startBlinkingCursor(textView, text)
        }
        animationJobs[textView.id] = Pair(typingJob, null)
    }

    private fun startBlinkingCursor(textView: TextView, originalText: String) {
        val textWithCursor = SpannableStringBuilder("$originalText ▼")
        val cursorStart = textWithCursor.length - 1
        val cursorEnd = textWithCursor.length

        val blinkingJob = CoroutineScope(Dispatchers.Main).launch {
            var isCursorVisible = true
            while (isActive) {
                val spanToApply = if (isCursorVisible) {
                    ForegroundColorSpan(Color.WHITE)
                } else {
                    ForegroundColorSpan(Color.TRANSPARENT)
                }

                textWithCursor.setSpan(
                    spanToApply,
                    cursorStart,
                    cursorEnd,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                textView.text = textWithCursor
                isCursorVisible = !isCursorVisible
                delay(500)
            }
        }

        val currentTypingJob = animationJobs[textView.id]?.first
        animationJobs[textView.id] = Pair(currentTypingJob, blinkingJob)
    }

    override fun onDestroy() {
        super.onDestroy()
        animationJobs.values.forEach { (typingJob, blinkingJob) ->
            typingJob?.cancel()
            blinkingJob?.cancel()
        }
    }
}