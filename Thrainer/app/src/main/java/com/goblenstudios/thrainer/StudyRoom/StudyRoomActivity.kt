package com.goblenstudios.thrainer.StudyRoom

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goblenstudios.thrainer.FlashcardActivity
import com.goblenstudios.thrainer.HomeActivity
import com.goblenstudios.thrainer.R
import androidx.recyclerview.widget.ItemTouchHelper
import java.util.Collections

class StudyRoomActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_study_room)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // AQUI !!!
        val btnReturnToHome = findViewById<Button>(R.id.btnReturnToHome)

        val btnLeftCenter = findViewById<Button>(R.id.btnLeftCenter)

        val btnRightCenter = findViewById<Button>(R.id.btnRightCenter)

        val leftOverlay = findViewById<FrameLayout>(R.id.leftOverlay)

        val btnCloseOverlay = findViewById<Button>(R.id.btnCloseOverlay)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val items = listOf("Item 1", "Item 2", "Item 3")
        recyclerView.adapter = OverlayAdapter(items)

        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                Collections.swap(items, fromPos, toPos)
                recyclerView.adapter?.notifyItemMoved(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Não faz nada, pois não queremos swipe
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)







        btnLeftCenter.setOnClickListener {
            leftOverlay.visibility = View.VISIBLE
            btnReturnToHome.visibility = View.INVISIBLE
            btnLeftCenter.visibility = View.INVISIBLE
        }

        btnRightCenter.setOnClickListener {
            startActivity(Intent(this, FlashcardActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
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
    }
}