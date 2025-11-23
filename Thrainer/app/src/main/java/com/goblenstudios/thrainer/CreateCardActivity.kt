package com.goblenstudios.thrainer

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.goblenstudios.thrainer.dtos.CreateCardDto
import com.goblenstudios.thrainer.repositories.CardRepository
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random
import android.os.Handler
import android.os.Looper

class CreateCardActivity : AppCompatActivity() {
    private val movingAnimators = mutableListOf<ValueAnimator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar 3 GIFs animados que se movem
        val movingGif1 = findViewById<ImageView>(R.id.movingGif1)
        val movingGif2 = findViewById<ImageView>(R.id.movingGif2)
        val movingGif3 = findViewById<ImageView>(R.id.movingGif3)

        // Carregar GIFs usando Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.magic_creation)
            .into(movingGif1)

        Glide.with(this)
            .asGif()
            .load(R.drawable.magic_creation)
            .into(movingGif2)

        Glide.with(this)
            .asGif()
            .load(R.drawable.magic_creation)
            .into(movingGif3)

        // Iniciar animações com delays diferentes
        movingGif1.post {
            // Primeiro GIF começa imediatamente
            startMovingAnimation(movingGif1, 0)
        }

        movingGif2.post {
            // Segundo GIF começa após 800ms
            Handler(Looper.getMainLooper()).postDelayed({
                startMovingAnimation(movingGif2, 1)
            }, 800)
        }

        movingGif3.post {
            // Terceiro GIF começa após 1600ms
            Handler(Looper.getMainLooper()).postDelayed({
                startMovingAnimation(movingGif3, 2)
            }, 1600)
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
                    setResult(RESULT_OK) // Notificar que o card foi criado com sucesso
                    finish()
                } else {
                    Toast.makeText(this@CreateCardActivity, "Erro ao criar carta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startMovingAnimation(imageView: ImageView, animatorIndex: Int) {
        val parent = imageView.parent as android.view.View
        val maxX = parent.width - imageView.width
        val maxY = parent.height - imageView.height

        // Garantir que há espaço para mover
        if (maxX <= 0 || maxY <= 0) return

        // Criar animação que move o GIF para posições aleatórias
        fun animateToRandomPosition() {
            val randomX = Random.nextInt(0, maxX).toFloat()
            val randomY = Random.nextInt(0, maxY).toFloat()

            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 2000 + Random.nextLong(1000) // 2-3 segundos por movimento
                interpolator = LinearInterpolator()

                val startX = imageView.x
                val startY = imageView.y

                addUpdateListener { animator ->
                    val progress = animator.animatedValue as Float
                    imageView.x = startX + (randomX - startX) * progress
                    imageView.y = startY + (randomY - startY) * progress
                }

                // Quando terminar, animar para próxima posição aleatória
                addListener(object : android.animation.Animator.AnimatorListener {
                    override fun onAnimationStart(animation: android.animation.Animator) {}
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        animateToRandomPosition()
                    }
                    override fun onAnimationCancel(animation: android.animation.Animator) {}
                    override fun onAnimationRepeat(animation: android.animation.Animator) {}
                })

                start()
            }

            // Armazenar ou substituir o animator no índice correto
            if (animatorIndex < movingAnimators.size) {
                movingAnimators[animatorIndex] = animator
            } else {
                movingAnimators.add(animator)
            }
        }

        // Iniciar primeira animação
        animateToRandomPosition()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancelar todas as animações ao destruir activity
        movingAnimators.forEach { it.cancel() }
        movingAnimators.clear()
    }
}