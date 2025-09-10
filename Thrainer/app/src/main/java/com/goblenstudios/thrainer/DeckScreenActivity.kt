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
import com.goblenstudios.thrainer.repositories.DeckRepository
import com.goblenstudios.thrainer.dtos.ReturnDeckDto
import com.goblenstudios.thrainer.services.DeckService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeckScreenActivity : AppCompatActivity() {
    // Classe de dados para o deck (sem autor)
    data class Deck(val deckName: String, val numberOfCards: String, val numberOfDownloads: String)

    // Adapter interno para o RecyclerView
    class DeckAdapter(private val decks: List<Deck>) :
        RecyclerView.Adapter<DeckAdapter.DeckViewHolder>() {

        class DeckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvDeckName: TextView = itemView.findViewById(R.id.tvDeckName)
            val tvNumberOfCards: TextView = itemView.findViewById(R.id.tvNumberOfCards)
            val tvNumberOfDownloads: TextView = itemView.findViewById(R.id.tvNumberOfDownloads)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_deck_screen, parent, false)
            return DeckViewHolder(view)
        }

        override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
            val deck = decks[position]
            holder.tvDeckName.text = deck.deckName
            holder.tvNumberOfCards.text = deck.numberOfCards
            holder.tvNumberOfDownloads.text = deck.numberOfDownloads
        }

        override fun getItemCount() = decks.size
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeckAdapter
//    private val deckRepository = DeckRepository(DeckService())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deck_screen)

        recyclerView = findViewById(R.id.recyclerViewDecks)
        recyclerView.layoutManager = LinearLayoutManager(this)

//        lifecycleScope.launch {
//            val result = withContext(Dispatchers.IO) { deckRepository.getAllPublicDecks() } // Substitua por getUserDecks() se existir
//            if (result.isSuccess) {
//                val decksApi = result.getOrNull() ?: emptyList<com.goblenstudios.thrainer.dtos.ReturnDeckDto>()
//                val decks = decksApi.map {
//                    Deck(
//                        deckName = it.name ?: "",
//                        numberOfCards = it.cards?.size?.toString() ?: "0",
//                        numberOfDownloads = it.downloads?.toString() ?: "0"
//                    )
//                }
//                adapter = DeckAdapter(decks)
//                recyclerView.adapter = adapter
//            } else {
//                Toast.makeText(this@DeckScreenActivity, "Erro ao carregar decks", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}