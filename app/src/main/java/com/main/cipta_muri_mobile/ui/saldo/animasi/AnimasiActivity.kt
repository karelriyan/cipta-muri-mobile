package com.main.cipta_muri_mobile.ui.saldo.animasi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.databinding.ActivityAnimasiBinding
import com.main.cipta_muri_mobile.ui.saldo.berhasil.PenarikanBerhasilActivity

class AnimasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startLoadingAnimation()
    }

    private fun startLoadingAnimation() {
        // Jalankan animasi loading (misalnya Lottie / ProgressBar)
        // Setelah beberapa detik, lanjut ke halaman "PenarikanBerhasilActivity"
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, PenarikanBerhasilActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
