package com.goblenstudios.thrainer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class LoginVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração para tela cheia completa
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Criar e configurar TextureView
        val textureView = android.view.TextureView(this)
        textureView.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Criar FrameLayout com fundo preto para eliminar bordas
        val frameLayout = android.widget.FrameLayout(this)
        frameLayout.setBackgroundColor(android.graphics.Color.BLACK)
        frameLayout.addView(textureView)

        setContentView(frameLayout)

        textureView.surfaceTextureListener = object : android.view.TextureView.SurfaceTextureListener {
            var mediaPlayer: android.media.MediaPlayer? = null

            override fun onSurfaceTextureAvailable(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {
                mediaPlayer = android.media.MediaPlayer()
                val afd = resources.openRawResourceFd(R.raw.door_animation)
                mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                mediaPlayer?.setSurface(android.view.Surface(surface))

                mediaPlayer?.setOnVideoSizeChangedListener { mp, videoWidth, videoHeight ->
                    // Usar as dimensões reais da tela
                    val displayMetrics = resources.displayMetrics
                    val screenWidth = displayMetrics.widthPixels.toFloat()
                    val screenHeight = displayMetrics.heightPixels.toFloat()

                    val matrix = android.graphics.Matrix()
                    val scaleX = screenWidth / videoWidth
                    val scaleY = screenHeight / videoHeight

                    // Força tela cheia - pode cortar o vídeo mas elimina bordas
                    val scale = maxOf(scaleX, scaleY)

                    matrix.setScale(scale, scale, screenWidth / 2, screenHeight / 2)
                    textureView.setTransform(matrix)
                }

                mediaPlayer?.setOnCompletionListener {
                    mediaPlayer?.release()
                    startActivity(Intent(this@LoginVideoActivity, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }

                mediaPlayer?.setOnPreparedListener { it.start() }
                mediaPlayer?.prepareAsync()
            }

            override fun onSurfaceTextureSizeChanged(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surface: android.graphics.SurfaceTexture): Boolean {
                mediaPlayer?.release()
                return true
            }
            override fun onSurfaceTextureUpdated(surface: android.graphics.SurfaceTexture) {}
        }
    }
}