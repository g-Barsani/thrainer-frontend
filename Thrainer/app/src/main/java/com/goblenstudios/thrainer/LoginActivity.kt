package com.goblenstudios.thrainer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.goblenstudios.thrainer.repositories.AuthRepository
import com.goblenstudios.thrainer.services.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.apply

class LoginActivity : AppCompatActivity() {
    private val authRepository = AuthRepository(RetrofitInstance.authService)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val textViewRegister = findViewById<TextView>(R.id.registerButton)
        val btnLogin = findViewById<Button>(R.id.loginButton)

        textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        btnLogin.setOnClickListener {
// Apply the custom theme here
            val videoDialog = android.app.Dialog(this@LoginActivity, R.style.FullScreenDialogTheme)

            val videoView = android.widget.VideoView(this@LoginActivity)
            videoView.layoutParams = android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
            videoDialog.setContentView(videoView)

// This line is good to keep, though the theme does most of the work
            videoDialog.window?.setLayout(
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.MATCH_PARENT
            )

            videoDialog.setCancelable(false)
            videoView.setVideoPath("android.resource://" + packageName + "/" + R.raw.door_animation)

            videoView.setOnCompletionListener {
                videoDialog.dismiss()
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }

            videoDialog.show()
            videoView.start()

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Coroutine serve para executar o login de forma assíncrona, sem travar a interface do app

            CoroutineScope(Dispatchers.Main).launch {

                val result = authRepository.login(email, password)
                if (result.isSuccess) {



                    //Armazena o token de autenticação nas SharedPreferences
                    val response = result.getOrNull()
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit()
                        .putString("auth_token", response?.token)
                        .putLong("user_id", response?.user?.idUser ?: -1L)
                        .putString("user_name", response?.user?.name)
                        .putString("user_email", response?.user?.email)
                        .putBoolean("user_is_public", response?.user?.isPublic ?: false)
                        .apply()

                    Toast.makeText(this@LoginActivity, "Login bem-sucedido: ${prefs.getString("user_email", "")}", Toast.LENGTH_LONG).show()

                    println("Token: ${response?.token}")
                    println("ID do usuário: ${response?.user?.idUser}")
                    println("Nome: ${response?.user?.name}")
                    println("Email: ${response?.user?.email}")
                    println("Público: ${response?.user?.isPublic}")

                    // Login bem-sucedido, navega para HomeActivity
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                } else {
                    // Erro no login
                    Toast.makeText(this@LoginActivity, "Login falhou: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}