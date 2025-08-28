package com.goblenstudios.thrainer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

class CommunityActivity : AppCompatActivity() {
    // Classe de dados para o deck
    data class Deck(val coluna1: String, val coluna2: String, val coluna3: String)

    // Adapter para o RecyclerView. Conecta dados á interface do RecyclerView
    class CommunityAdapter(private val decks: List<Deck>) :
        RecyclerView.Adapter<CommunityAdapter.DeckViewHolder>() {

        class DeckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvColuna1: TextView = itemView.findViewById(R.id.tvColuna1)
            val tvColuna2: TextView = itemView.findViewById(R.id.tvColuna2)
            val tvColuna3: TextView = itemView.findViewById(R.id.tvColuna3)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
            return DeckViewHolder(view)
        }

        override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
            val deck = decks[position]
            holder.tvColuna1.text = deck.coluna1
            holder.tvColuna2.text = deck.coluna2
            holder.tvColuna3.text = deck.coluna3
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

        // Dados mockados
        val mockDecks = listOf(
            Deck("Deck 1", "Autor 1", "10 cards"),
            Deck("Deck 2", "Autor 2", "15 cards"),
            Deck("Deck 3", "Autor 3", "8 cards")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDecks)

        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        recyclerView.adapter = CommunityAdapter(mockDecks)

        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )





        // Voltar para home
        val btnReturnToHome = findViewById<Button>(R.id.btnReturnToHome)

        btnReturnToHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }
}