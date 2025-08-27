package com.goblenstudios.thrainer

import android.content.Intent
import android.os.Bundle
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
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // oroutine serve para executar o login de forma assíncrona, sem travar a interface do app
            CoroutineScope(Dispatchers.Main).launch {
                val result = authRepository.login(email, password)
                if (result.isSuccess) {
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