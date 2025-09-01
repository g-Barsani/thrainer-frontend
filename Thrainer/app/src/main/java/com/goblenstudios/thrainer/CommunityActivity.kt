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
    data class Deck(val deckName: String, val userName: String, val numberOfCards: String)

    // Adapter para o RecyclerView. Conecta dados á interface do RecyclerView
    class CommunityAdapter(private val decks: List<Deck>) :
        RecyclerView.Adapter<CommunityAdapter.DeckViewHolder>() {

        class DeckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvColumn1: TextView = itemView.findViewById(R.id.tvColumn1)
            val tvColumn2: TextView = itemView.findViewById(R.id.tvColumn2)
            val tvColumn3: TextView = itemView.findViewById(R.id.tvColumn3)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
            return DeckViewHolder(view)
        }

        override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
            val deck = decks[position]
            holder.tvColumn1.text = deck.deckName
            holder.tvColumn2.text = deck.userName
            holder.tvColumn3.text = deck.numberOfCards
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
                        numberOfCards = dto.numberOfCards ?.toString() ?: "0"
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