package com.goblenstudios.thrainer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.goblenstudios.thrainer.repositories.DeckRepository
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.launch

class CommunityActivity : AppCompatActivity() {

    val deckRepository = DeckRepository(RetrofitInstance.deckService)

    // Classe de dados para o deck
    data class Deck(val deckName: String, val userName: String, val numberOfCards: String, val numberOfDownloads: String)

    // Adapter para o RecyclerView. Conecta dados á interface do RecyclerView
    class CommunityAdapter(private val decks: List<Deck>) :
        RecyclerView.Adapter<CommunityAdapter.DeckViewHolder>() {

        class DeckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvDeckName: TextView = itemView.findViewById(R.id.tvDeckName)
            val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
            val tvNumberOfCards: TextView = itemView.findViewById(R.id.tvNumberOfCards)
            val tvNumberOfDownloads: TextView = itemView.findViewById(R.id.tvNumberOfDownloads)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
            return DeckViewHolder(view)
        }

        override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
            val deck = decks[position]
            holder.tvDeckName.text = deck.deckName
            holder.tvUsername.text = deck.userName
            holder.tvNumberOfCards.text = deck.numberOfCards
            holder.tvNumberOfDownloads.text = deck.numberOfDownloads
        }

        override fun getItemCount() = decks.size
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_community)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Aplica fade in na activity
        overridePendingTransition(R.drawable.fade_in, R.drawable.fade_out)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDecks)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        //Quando iniciar a activity, carrega os decks mais populares
        lifecycleScope.launch {
            val result = deckRepository.getMostPopularDecks()
            if (result.isSuccess) {
                val deckDtos = result.getOrNull() ?: emptyList()
                val decks = deckDtos.map { dto ->
                    Deck(
                        deckName = dto.name ?: "",
                        userName = dto.creatorUserName ?: "",
                        numberOfCards = dto.numberOfCards ?.toString() ?: "0",
                        numberOfDownloads = dto.numberOfDownloads ?.toString() ?: "0",
                    )
                }
                recyclerView.adapter = CommunityAdapter(decks)
            } else {
                Toast.makeText(
                    this@CommunityActivity,
                    "Erro ao carregar decks: ${result.exceptionOrNull()?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Voltar para home
        val btnReturnToHome = findViewById<Button>(R.id.btnReturnToHome)

        btnReturnToHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }
}