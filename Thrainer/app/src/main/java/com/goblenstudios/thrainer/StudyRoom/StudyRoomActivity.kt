package com.goblenstudios.thrainer.StudyRoom

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.goblenstudios.thrainer.CommunityActivity
import com.goblenstudios.thrainer.CreateCardActivity
import com.goblenstudios.thrainer.CreateDeckDialogFragment
import com.goblenstudios.thrainer.DeckScreenActivity
import com.goblenstudios.thrainer.FlashcardActivity
import com.goblenstudios.thrainer.HomeActivity
import com.goblenstudios.thrainer.R
import com.goblenstudios.thrainer.repositories.DeckRepository
import com.goblenstudios.thrainer.dtos.ReturnDeckDto
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide

class StudyRoomActivity : AppCompatActivity() {
    // Função utilitária para converter dp em px
    fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private var selectedDeckId: Long? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_study_room)

        // Aplica fade in na activity
        overridePendingTransition(R.drawable.fade_in, R.drawable.fade_out)

        val backgroundImage = findViewById<ImageView>(R.id.backgroundImage)
        Glide.with(this)
            .asGif()
            .load(R.drawable.study_room_animated) // seu arquivo GIF
            .into(backgroundImage)


        val tvDeckName = findViewById<TextView>(R.id.tvDeckName)
        val llTop = findViewById<LinearLayout>(R.id.llTop)  // De onde o objeto view pode ser arrastado
        val llCauldron = findViewById<LinearLayout>(R.id.llCauldron)  // Destino onde o objeto view pode ser solto
//        val itens = listOf("Arrasta 1", "Arrasta 2", "Arrasta 3", "Arrasta 4", "Arrasta 5")

        val deckRepository = DeckRepository(RetrofitInstance.deckService)

        val dragListener = View.OnDragListener { view, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                DragEvent.ACTION_DRAG_ENTERED -> { view.invalidate(); true }
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> { view.invalidate(); true }
                DragEvent.ACTION_DROP -> {
                    val item = event.clipData.getItemAt(0)
                    val dragData = item.text
                    tvDeckName.text = dragData
                    Toast.makeText(this, dragData, Toast.LENGTH_SHORT).show()
                    view.invalidate()
                    val v = event.localState as View
                    val owner = v.parent as ViewGroup
                    owner.removeView(v)

                    // Guardar o id do deck selecionado ao dropar no caldeirão
                    if (view.id == R.id.llCauldron) {
                        selectedDeckId = v.tag as? Long
                    }

                    when (view.id) {
                        R.id.llCauldron -> {
                            // Animação de retorno
                            v.animate()
                                .translationX(0f)
                                .translationY(0f)
                                .setDuration(300)
                                .withEndAction {
                                    llTop.addView(v)
                                    v.visibility = View.VISIBLE
                                }
                                .start()
                        }
                        R.id.llTop -> {
                            // Não faz nada, impede reorder
                            owner.addView(v) // Retorna para o local original
                            v.visibility = View.VISIBLE
                        }
                        else -> {
                            val destination = view as LinearLayout
                            destination.addView(v)
                            v.visibility = View.VISIBLE
                        }
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    view.invalidate()
                    val draggedView = event.localState as? View
                    draggedView?.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }

        llTop.setOnDragListener(dragListener)
        llCauldron.setOnDragListener(dragListener)

        // Buscar decks reais do usuário nas SharedPreferences
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = prefs.getLong("user_id", -1L)
        if (userId == -1L) {
            Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        // Buscar decks do usuário de forma assíncrona
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) { deckRepository.getDecksByUser(userId) }
            if (result.isSuccess) {
                val decks = result.getOrNull() ?: emptyList<ReturnDeckDto>()
                val deckMap = decks.associateBy { it.name } // Mapear nome para id
                if (decks.isEmpty()) {
                    // Exibe mensagem se não houver decks
                    val emptyText = TextView(this@StudyRoomActivity).apply {
                        text = "Nenhum deck encontrado."
                        setTextColor(resources.getColor(android.R.color.white, null))
                        textSize = 16f
                        gravity = android.view.Gravity.CENTER
                    }
                    llTop.addView(emptyText)
                } else {
                    // Para cada deck, cria um bloco arrastável
                    for ((index, deck) in decks.withIndex()) {
                        val frameLayout = FrameLayout(this@StudyRoomActivity).apply {
                            layoutParams = LinearLayout.LayoutParams(dp(200), dp(100)).apply {
                                marginEnd = dp(16)
                                topMargin = dp(8)
                            }
                            // Imagem de fundo do bloco
                            setBackgroundResource(R.drawable.sample2)
                        }
                        val textView = TextView(this@StudyRoomActivity).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            text = deck.name
                            setTextColor(resources.getColor(android.R.color.black, null))
                            gravity = android.view.Gravity.CENTER
                        }
                        frameLayout.addView(textView)
                        // Evento de toque longo para iniciar o arraste do deck
                        frameLayout.setOnLongClickListener { v ->
                            val item = ClipData.Item(deck.name)
                            val dragData = ClipData(
                                deck.name,
                                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                                item
                            )
                            val shadow = View.DragShadowBuilder(v)
                            v.visibility = View.INVISIBLE
                            v.startDragAndDrop(dragData, shadow, v, 0)
                            true
                        }
                        // Evento de clique simples para abrir a tela do deck
                        frameLayout.setOnClickListener {
                            val intent = Intent(this@StudyRoomActivity, DeckScreenActivity::class.java)
                            intent.putExtra("deckId", deck.idDeck)
                            intent.putExtra("deckName", deck.name)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                        }
                        // Guardar o id do deck no tag do frameLayout para uso no drop
                        frameLayout.tag = deck.idDeck
                        llTop.addView(frameLayout)
                    }
                }
            } else {
                // Exibe mensagem de erro se falhar ao buscar decks
                val errorText = TextView(this@StudyRoomActivity).apply {
                    text = "Erro ao carregar decks."
                    setTextColor(resources.getColor(android.R.color.white, null))
                    textSize = 16f
                    gravity = android.view.Gravity.CENTER
                }
                llTop.addView(errorText)
            }
        }



        // Botões e overlays
        val btnReturnToHome = findViewById<Button>(R.id.btnReturnToHome)
        val btnLeftCenter = findViewById<Button>(R.id.btnLeftCenter)
        val btnRightCenter = findViewById<Button>(R.id.btnRightCenter)
        val leftOverlay = findViewById<FrameLayout>(R.id.leftOverlay)
        val btnCloseOverlay = findViewById<ImageButton>(R.id.btnCloseOverlay)


        // Eventos de clique dos botões
        btnLeftCenter.setOnClickListener {
            leftOverlay.visibility = View.VISIBLE
            btnReturnToHome.visibility = View.INVISIBLE
            btnLeftCenter.visibility = View.INVISIBLE
        }

        btnRightCenter.setOnClickListener {
            // Só abrir se houver deck selecionado
            val deckId = selectedDeckId
            if (deckId != null) {
                val intent = Intent(this, FlashcardActivity::class.java)
                intent.putExtra("deckId", deckId)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            } else {
                Toast.makeText(this, "Selecione um deck para estudar!", Toast.LENGTH_SHORT).show()
            }
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

        // Botão de teste para abrir CreateDeckActivity
        val btnOpenCreateDeck = findViewById<Button>(R.id.btnOpenCreateDeck)
        btnOpenCreateDeck.setOnClickListener {
            CreateDeckDialogFragment().show(supportFragmentManager, "CreateDeckDialog")
        }
    }
}