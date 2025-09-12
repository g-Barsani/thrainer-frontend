package com.goblenstudios.thrainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goblenstudios.thrainer.repositories.CardRepository
import com.goblenstudios.thrainer.dtos.ReturnCardDto
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
//    private val cardRepository = CardRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deck_screen)

        recyclerView = findViewById(R.id.recyclerViewDecks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Exemplo: obter deckId de algum lugar (intent, etc)
        val deckId = intent.getLongExtra("deckId", 0L)
        if (deckId == 0L) {
            Toast.makeText(this, "Deck não informado", Toast.LENGTH_SHORT).show()
            return
        }

//        lifecycleScope.launch {
//            val result = withContext(Dispatchers.IO) { cardRepository.getCardsByDeck(deckId) }
//            if (result.isSuccess) {
//                val cards = result.getOrNull() ?: emptyList<ReturnCardDto>()
//                adapter = CardAdapter(cards)
//                recyclerView.adapter = adapter
//            } else {
//                Toast.makeText(this@DeckScreenActivity, "Erro ao carregar cartas", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}