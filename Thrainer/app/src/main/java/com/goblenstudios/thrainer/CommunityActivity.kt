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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.goblenstudios.thrainer.repositories.DeckRepository
import com.goblenstudios.thrainer.repositories.DeckUserRepository
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.launch
import kotlin.compareTo

class CommunityActivity : AppCompatActivity() {

    private val deckRepository = DeckRepository(RetrofitInstance.deckService)
    private val deckUserRepository = DeckUserRepository(RetrofitInstance.deckUserService)

    data class Deck(val id: Long, val deckName: String, val userName: String, val numberOfCards: String, val numberOfDownloads: String)

    class CommunityAdapter(
        private val decks: List<Deck>,
        private val onItemClick: (Long) -> Unit
    ) : RecyclerView.Adapter<CommunityAdapter.DeckViewHolder>() {

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

            holder.itemView.setOnClickListener {
                onItemClick(deck.id)
            }
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

        // Carregar GIF animado no background
        val backgroundImage = findViewById<ImageView>(R.id.backgroundImage)
        Glide.with(this)
            .asGif()
            .load(R.drawable.community_room)
            .into(backgroundImage)

        overridePendingTransition(R.drawable.fade_in, R.drawable.fade_out)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDecks)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        lifecycleScope.launch {
            val result = deckRepository.getMostPopularDecks()
            if (result.isSuccess) {
                val deckDtos = result.getOrNull() ?: emptyList()
                val decks = deckDtos.map { dto ->
                    Deck(
                        id = dto.idDeck,
                        deckName = dto.name,
                        userName = dto.creatorUserName,
                        numberOfCards = dto.numberOfCards?.toString() ?: "0",
                        numberOfDownloads = dto.numberOfDownloads?.toString() ?: "0"
                    )
                }
                recyclerView.adapter = CommunityAdapter(decks) { clickedDeckId ->
                    handleCopyDeckToUser(clickedDeckId)
                }
            } else {
                Toast.makeText(
                    this@CommunityActivity,
                    "Erro ao carregar decks: ${result.exceptionOrNull()?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val btnReturnToHome = findViewById<Button>(R.id.btnReturnToHome)
        btnReturnToHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun handleCopyDeckToUser(deckId: Long) {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = prefs.getLong("user_id", -1L)
        if (userId <= 0L) {
            Toast.makeText(this, "Usuário não está logado", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val result = deckUserRepository.copyDeckToUser(userId, deckId)
                if (result.isSuccess) {
                    val dto = result.getOrNull()
                    val name = dto?.deckName ?: "deck"
                    Toast.makeText(this@CommunityActivity, "Copiado! $name", Toast.LENGTH_LONG).show()
                } else {
                    val e = result.exceptionOrNull()
                    when (e) {
                        is retrofit2.HttpException -> {
                            Toast.makeText(this@CommunityActivity, "Você já copiou este feitiço!", Toast.LENGTH_LONG).show()

                        }
                        else -> {
                            Toast.makeText(this@CommunityActivity, "Copia falhou: ${e?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (ex: Exception) {
                Toast.makeText(this@CommunityActivity, "Erro ao copiar deck: ${ex.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
