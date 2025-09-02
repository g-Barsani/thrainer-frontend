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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.goblenstudios.thrainer.FlashcardActivity
import com.goblenstudios.thrainer.HomeActivity
import com.goblenstudios.thrainer.R

class StudyRoomActivity : AppCompatActivity() {
    // Função utilitária para converter dp em px
    fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_study_room)

        val tvDeckName = findViewById<TextView>(R.id.tvDeckName)
        val llTop = findViewById<LinearLayout>(R.id.llTop)  // De onde o objeto view pode ser arrastado
        val llCauldron = findViewById<LinearLayout>(R.id.llCauldron)  // Destino onde o objeto view pode ser solto
        val itens = listOf("Arrasta 1", "Arrasta 2", "Arrasta 3", "Arrasta 4", "Arrasta 5")
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

        // Cria dinamicamente os blocos arrastáveis
        for ((index, texto) in itens.withIndex()) {
            val frameLayout = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(dp(200), dp(100)).apply {
                    marginEnd = dp(16)
                }
                setBackgroundResource(if (index % 2 == 0) android.R.color.black else android.R.color.darker_gray)
            }

            val textView = TextView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                text = texto
                setTextColor(resources.getColor(android.R.color.white, null))
                gravity = android.view.Gravity.CENTER
            }

            frameLayout.addView(textView)

            frameLayout.setOnLongClickListener {
                val item = ClipData.Item(textView.text)
                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(textView.text, mimeTypes, item)
                val dragShadowBuilder = View.DragShadowBuilder(frameLayout)
                frameLayout.startDragAndDrop(data, dragShadowBuilder, frameLayout, 0)
                frameLayout.visibility = View.INVISIBLE
                true
            }

            llTop.addView(frameLayout)
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