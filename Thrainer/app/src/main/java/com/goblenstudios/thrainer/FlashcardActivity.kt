package com.goblenstudios.thrainer

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.goblenstudios.thrainer.dtos.ReturnCardDto
import com.goblenstudios.thrainer.repositories.UserCardRepository
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlashcardActivity : AppCompatActivity() {
    private lateinit var tvQuestion: TextView
    private lateinit var tvAnswer: TextView
    private lateinit var btnShowAnswer: ImageView
    private lateinit var btnAcertou: Button
    private lateinit var btnErrou: Button
    private lateinit var btnEndSession: Button
    private lateinit var ivQuestion: ImageView
    private lateinit var ivAnswer: ImageView
    private lateinit var ivGifOverlay: ImageView
    private lateinit var ivBackground: ImageView

    private lateinit var userCardRepository: UserCardRepository
    private var cards: List<ReturnCardDto> = emptyList()
    private var currentIndex = 0
    private var userId: Long = 0L
    private var deckId: Long = 0L
    private var isShowingAnswer = false

    // referências para controlar o GifDrawable e callback
    private var gifDrawable: GifDrawable? = null
    private var gifAnimationCallback: Animatable2Compat.AnimationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnEndSession = findViewById(R.id.btnEndSession)
        btnEndSession.visibility = View.INVISIBLE

        tvQuestion = findViewById(R.id.tvQuestion)
        tvAnswer = findViewById(R.id.tvAnswer)
        btnShowAnswer = findViewById(R.id.btnShowAnswer)
        btnAcertou = findViewById(R.id.btnAcertou)
        btnErrou = findViewById(R.id.btnErrou)
        ivQuestion = findViewById(R.id.imageView)
        ivAnswer = findViewById(R.id.imageView2)
        ivGifOverlay = findViewById(R.id.ivGifOverlay)
        ivBackground = findViewById(R.id.ivBackground)

        // Caso o XML tenha definido um drawable animável, pare ele imediatamente
        (ivBackground.drawable as? Animatable)?.stop()

        // Carregar o background como bitmap (primeiro frame) para NÃO animar constantemente
        Glide.with(this)
            .asBitmap()
            .load(R.drawable.flashcard_study)
            .into(ivBackground)

        // Carregar GIF animado no botão "Mostrar resposta"
        Glide.with(this)
            .asGif()
            .load(R.drawable.cauldron_symbol) // Temporário - adicione seu GIF personalizado aqui
            .into(btnShowAnswer)

        // estado inicial
        tvAnswer.visibility = View.GONE
        ivAnswer.visibility = View.GONE
        ivQuestion.visibility = View.VISIBLE
        btnAcertou.visibility = View.GONE
        btnErrou.visibility = View.GONE
        btnShowAnswer.visibility = View.VISIBLE

        userCardRepository = UserCardRepository(RetrofitInstance.userCardService)
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        userId = prefs.getLong("user_id", 0L)
        deckId = intent.getLongExtra("deckId", 0L)
        if (userId == 0L || deckId == 0L) {
            Toast.makeText(this, "Usuário ou deck inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) { userCardRepository.practiceCardsByDeck(userId, deckId) }
            if (result.isSuccess) {
                cards = result.getOrNull() ?: emptyList()
                if (cards.isEmpty()) {
                    tvQuestion.text = "Nenhum card para praticar."
                    btnShowAnswer.isEnabled = false
                    btnAcertou.isEnabled = false
                    btnErrou.isEnabled = false
                    ivQuestion.visibility = View.GONE
                    ivAnswer.visibility = View.GONE
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
            if (cards.isNotEmpty() && currentIndex < cards.size && !isShowingAnswer) {
                btnShowAnswer.isEnabled = false

                // limpar GIF anterior e callback se houver
                gifAnimationCallback?.let { gifDrawable?.unregisterAnimationCallback(it) }
                gifAnimationCallback = null
                gifDrawable = null
                try { Glide.with(this).clear(ivGifOverlay) } catch (_: Exception) {}

                ivGifOverlay.visibility = View.VISIBLE

                // carregar como GIF, forçar 1 loop e registrar callback para detectar fim da animação
                Glide.with(this)
                    .asGif()
                    .load(R.drawable.answer_click_animated)
                    .listener(object : RequestListener<GifDrawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            ivGifOverlay.visibility = View.GONE
                            revealAnswerAfterGif()
                            return true
                        }

                        override fun onResourceReady(
                            resource: GifDrawable,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            // retornar false para permitir o CustomTarget receber o resource também
                            return false
                        }
                    })
                    .into(object : CustomTarget<GifDrawable>() {
                        override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
                            // segura referência para parar/limpar depois
                            gifDrawable = resource

                            // garantir que rode apenas 1 vez
                            resource.setLoopCount(1)

                            // registrar callback para capturar fim da animação
                            gifAnimationCallback = object : Animatable2Compat.AnimationCallback() {
                                override fun onAnimationEnd(drawable: Drawable?) {
                                    runOnUiThread {
                                        try { resource.stop() } catch (_: Exception) {}
                                        ivGifOverlay.visibility = View.GONE
                                        try { Glide.with(this@FlashcardActivity).clear(ivGifOverlay) } catch (_: Exception) {}
                                        revealAnswerAfterGif()
                                    }
                                    resource.unregisterAnimationCallback(this)
                                }
                            }
                            gifAnimationCallback?.let { resource.registerAnimationCallback(it) }

                            try { ivGifOverlay.setImageDrawable(resource) } catch (_: Exception) {}
                            try { resource.start() } catch (_: Exception) {}
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // limpar referências se necessário
                        }
                    })
            }
        }

        btnAcertou.setOnClickListener { marcarCard(true) }
        btnErrou.setOnClickListener { marcarCard(false) }
        btnEndSession.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        // parar e desregistrar callback do GIF ativo
        try {
            gifAnimationCallback?.let { gifDrawable?.unregisterAnimationCallback(it) }
        } catch (_: Exception) {}
        gifAnimationCallback = null
        try { gifDrawable?.stop() } catch (_: Exception) {}
        gifDrawable = null
        try { Glide.with(this).clear(ivGifOverlay) } catch (_: Exception) {}
        try { (ivBackground.drawable as? Animatable)?.stop() } catch (_: Exception) {}
    }

    private fun revealAnswerAfterGif() {
        try { (ivGifOverlay.drawable as? Animatable)?.stop() } catch (_: Exception) {}
        try { Glide.with(this).clear(ivGifOverlay) } catch (_: Exception) {}
        ivGifOverlay.visibility = View.GONE

        if (cards.isEmpty() || currentIndex >= cards.size) return
        tvAnswer.text = cards[currentIndex].answer
        tvAnswer.visibility = View.VISIBLE
        tvQuestion.visibility = View.GONE
        ivQuestion.visibility = View.GONE
        ivAnswer.visibility = View.VISIBLE

        btnAcertou.visibility = View.VISIBLE
        btnErrou.visibility = View.VISIBLE
        btnShowAnswer.visibility = View.GONE

        isShowingAnswer = true
    }

    private fun showCurrentCard() {
        if (cards.isEmpty()) {
            tvQuestion.text = "Nenhum card para praticar."
            btnShowAnswer.isEnabled = false
            btnAcertou.isEnabled = false
            btnErrou.isEnabled = false
            tvAnswer.visibility = View.GONE
            ivAnswer.visibility = View.GONE
            ivQuestion.visibility = View.GONE
            return
        }

        if (currentIndex >= cards.size) {
            tvQuestion.visibility = View.VISIBLE
            tvQuestion.text = "Oba! Concluímos a sintese desse feitiço!"
            ivQuestion.visibility = View.VISIBLE
            tvAnswer.visibility = View.GONE
            ivAnswer.visibility = View.GONE

            btnShowAnswer.visibility = View.GONE
            btnAcertou.visibility = View.GONE
            btnErrou.visibility = View.GONE

            btnEndSession.visibility = View.VISIBLE
            btnEndSession.isEnabled = true
            return
        }

        val card = cards[currentIndex]
        tvQuestion.visibility = View.VISIBLE
        tvQuestion.text = card.question
        tvAnswer.visibility = View.GONE
        ivAnswer.visibility = View.GONE
        ivQuestion.visibility = View.VISIBLE
        btnShowAnswer.visibility = View.VISIBLE
        btnAcertou.visibility = View.GONE
        btnErrou.visibility = View.GONE

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
            tvQuestion.visibility = View.VISIBLE
            tvQuestion.text = "Oba! Concluímos a sintese desse feitiço!"
            ivQuestion.visibility = View.VISIBLE
            tvAnswer.visibility = View.GONE
            ivAnswer.visibility = View.GONE

            btnShowAnswer.visibility = View.GONE
            btnAcertou.visibility = View.GONE
            btnErrou.visibility = View.GONE

            btnEndSession.isEnabled = true
            btnEndSession.visibility = View.VISIBLE
        }
    }

    private fun marcarCard(acertou: Boolean) {
        if (cards.isEmpty() || currentIndex >= cards.size) return
        val cardId = cards[currentIndex].idCard
        println("Tentando marcar card - userId: $userId, cardId: $cardId, acertou: $acertou")
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    if (acertou) userCardRepository.markCardAsCorrect(userId, cardId)
                    else userCardRepository.markCardAsWrong(userId, cardId)
                }

                if (result.isSuccess) {
                    Toast.makeText(
                        this@FlashcardActivity,
                        if (acertou) "Marcado como acertou" else "Marcado como errou",
                        Toast.LENGTH_SHORT
                    ).show()
                    nextCard()
                } else {
                    val exception = result.exceptionOrNull()
                    val errorMessage = exception?.message ?: "Erro desconhecido"
                    Toast.makeText(this@FlashcardActivity, "Erro ao marcar card: $errorMessage", Toast.LENGTH_LONG).show()
                    println("Erro ao marcar card: $errorMessage")
                    exception?.printStackTrace()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FlashcardActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}