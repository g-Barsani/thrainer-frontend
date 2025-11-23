package com.goblenstudios.thrainer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.goblenstudios.thrainer.repositories.CardRepository
import com.goblenstudios.thrainer.dtos.ReturnCardDto
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeckScreenActivity : AppCompatActivity() {
    // Adapter para exibir cartas
    class CardAdapter(private val cards: List<ReturnCardDto>) :
        RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

        class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
            val tvAnswer: TextView = itemView.findViewById(R.id.tvAnswer)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_deck_screen, parent, false)
            return CardViewHolder(view)
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            val card = cards[position]
            holder.tvQuestion.text = card.question
            holder.tvAnswer.text = card.answer
        }

        override fun getItemCount() = cards.size
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private val cardRepository = CardRepository(RetrofitInstance.cardService)
    private lateinit var tvDeckName: TextView
    private var currentDeckId: Long = 0L

    // Registrar launcher para receber resultado da CreateCardActivity
    private val createCardLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Card foi criado com sucesso, recarregar a lista
            loadCards(currentDeckId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deck_screen)

        // Carregar GIF animado no background
        val backgroundImage = findViewById<ImageView>(R.id.backgroundImage)
        Glide.with(this)
            .asGif()
            .load(R.drawable.study_room_animated)
            .into(backgroundImage)

        recyclerView = findViewById(R.id.recyclerViewDecks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tvDeckName = findViewById(R.id.tvDeckName)

        val deckId = intent.getLongExtra("deckId", 0L)
        if (deckId == 0L) {
            Toast.makeText(this, "Deck não informado", Toast.LENGTH_SHORT).show()
            return
        }

        currentDeckId = deckId

        // Buscar nome do deck (opcional: pode ser passado por intent ou buscar via API)
        val deckName = intent.getStringExtra("deckName")
        if (deckName != null) {
            tvDeckName.text = deckName
        } else {
            tvDeckName.text = "Deck"
        }

        // Carregar cards pela primeira vez
        loadCards(deckId)

        val btnReturnToHome = findViewById<Button>(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            finish()
        }

        val btnCreateCard = findViewById<Button>(R.id.btnCreateCard)
        btnCreateCard.setOnClickListener {
            val intent = Intent(this, CreateCardActivity::class.java)
            intent.putExtra("deckId", currentDeckId)
            createCardLauncher.launch(intent) // Usar launcher ao invés de startActivity
        }
    }

    // Função para carregar/recarregar cards
    private fun loadCards(deckId: Long) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { cardRepository.getCardsByDeck(deckId) }
            if (result.isSuccess) {
                val cards = result.getOrNull() ?: emptyList<ReturnCardDto>()
                adapter = CardAdapter(cards)
                recyclerView.adapter = adapter
            } else {
                Toast.makeText(this@DeckScreenActivity, "Erro ao carregar cartas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}