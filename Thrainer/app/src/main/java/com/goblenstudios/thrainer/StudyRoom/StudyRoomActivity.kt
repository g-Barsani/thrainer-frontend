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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.goblenstudios.thrainer.FlashcardActivity
import com.goblenstudios.thrainer.HomeActivity
import com.goblenstudios.thrainer.R

class StudyRoomActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_study_room)

        val tvDeckName = findViewById<TextView>(R.id.tvDeckName)

        // Drag and Drop
        val dragListener = View.OnDragListener { view, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> {
                    view.invalidate()
                    true
                }

                DragEvent.ACTION_DROP -> {
                    val item = event.clipData.getItemAt(0)
                    val dragData = item.text
                    tvDeckName.text = dragData // Atualiza o TextView com o texto arrastado

                    Toast.makeText(this, dragData, Toast.LENGTH_SHORT).show()

                    view.invalidate()

                    val v = event.localState as View
                    val owner = v.parent as ViewGroup
                    owner.removeView(v)
                    val destination = view as LinearLayout
                    destination.addView(v)
                    v.visibility = View.VISIBLE
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

        val lltop = findViewById<LinearLayout>(R.id.llTop)  // De onde o objeto view pode ser arrastado
        val llBottom = findViewById<LinearLayout>(R.id.llBottom)  // Destino onde o objeto view pode ser solto
//        val dragLayout = findViewById<FrameLayout>(R.id.draggableArea)  // Objeto layout que será arrastado

        val itens = listOf("Arrasta 1", "Arrasta 2", "Arrasta 3", "Arrasta 4")

        lltop.setOnDragListener(dragListener)
        llBottom.setOnDragListener(dragListener)
//        dragLayout.setOnLongClickListener {
//            val clipText = "Deck de Exemplo"
//            val item = ClipData.Item(clipText)
//            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
//            val data = ClipData(clipText, mimeTypes, item)
//
//            val dragShadowBuilder = View. DragShadowBuilder(it)
//            it.startDragAndDrop(data, dragShadowBuilder, it, 0)
//
//            it.visibility = View.INVISIBLE
//            true
//        }

        // Botões e overlays
        val btnReturnToHome = findViewById<Button>(R.id.btnReturnToHome)
        val btnLeftCenter = findViewById<Button>(R.id.btnLeftCenter)
        val btnRightCenter = findViewById<Button>(R.id.btnRightCenter)
        val leftOverlay = findViewById<FrameLayout>(R.id.leftOverlay)
        val btnCloseOverlay = findViewById<Button>(R.id.btnCloseOverlay)


        // Eventos de clique dos botões
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