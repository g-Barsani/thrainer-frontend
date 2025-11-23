// CreateDeckDialogFragment.kt
package com.goblenstudios.thrainer

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.goblenstudios.thrainer.dtos.CreateDeckDto
import com.goblenstudios.thrainer.repositories.AuthRepository
import com.goblenstudios.thrainer.repositories.DeckRepository
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.launch

class CreateDeckDialogFragment : DialogFragment() {

    // Interface para callback quando deck for criado
    interface OnDeckCreatedListener {
        fun onDeckCreated()
    }

    private var onDeckCreatedListener: OnDeckCreatedListener? = null

    // Método para definir o listener
    fun setOnDeckCreatedListener(listener: OnDeckCreatedListener) {
        onDeckCreatedListener = listener
    }

    private val deckRepository = DeckRepository(RetrofitInstance.deckService)


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_create_deck, null)

        //Caixa de texto do nome do deck
        val editTextDeckName = view.findViewById<EditText>(R.id.eTDeckName)

        //Switch para definir se o deck é público ou privado
        val switchIsPublic = view.findViewById<Switch>(R.id.switchPublicDeck)

        //Botão para criar o deck
        val btnCreateDeck = view.findViewById<Button>(R.id.createDeckButton)

        //Pega as preferências salvas (dados do usuário logado)
        val prefs = requireActivity().getSharedPreferences(
            "user_prefs",
            android.content.Context.MODE_PRIVATE
        )

        //id do usuário logado
        val idUserCreator = prefs.getLong("user_id", -1L)

        //Método de clique do botão de criar deck
        btnCreateDeck.setOnClickListener {

            val createDeckDto: CreateDeckDto = CreateDeckDto(
                name = editTextDeckName.text.toString(),
                idUserCreator = idUserCreator,
                isPublic = switchIsPublic.isChecked
            )


            requireActivity().lifecycleScope.launch {
                val result = deckRepository.createDeck(createDeckDto)

                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Deck criado com sucesso!", Toast.LENGTH_SHORT)
                        .show()
                    // Notificar o listener que o deck foi criado
                    onDeckCreatedListener?.onDeckCreated()
                    dismiss()
                } else {
                    val errorMsg =
                        result.exceptionOrNull()?.message ?: "Erro desconhecido ao criar deck."
                    println("Erro ao criar deck: $errorMsg")
                    Toast.makeText(
                        requireContext(),
                        "Falha ao criar deck: $errorMsg",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

    }
}