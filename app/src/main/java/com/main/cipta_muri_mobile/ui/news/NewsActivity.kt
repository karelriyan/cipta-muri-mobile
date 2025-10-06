package com.main.cipta_muri_mobile.ui.news

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.databinding.ActivityNewsBinding
import com.main.cipta_muri_mobile.ui.aktivitas.RiwayatAktivitasActivity
import com.main.cipta_muri_mobile.ui.main.MainActivity

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation(binding.bottomNavigationView)
    }

    private fun setupBottomNavigation(navView: BottomNavigationView) {
        // 🔥 Supaya ikon tampil dengan warna asli dari drawable
        navView.itemIconTintList = null

        // ✅ Tandai item News sebagai yang terpilih
        navView.selectedItemId = R.id.navigation_news

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivityWithFade(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> true // sudah di halaman ini
                R.id.navigation_history -> {
                    startActivityWithFade(Intent(this, RiwayatAktivitasActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_placeholder -> true // kalau nanti mau pakai FAB
                R.id.navigation_news -> {
                    startActivityWithFade(Intent(this, NewsActivity::class.java))
                    finish()
                    true
                } // kalau nanti mau pakai berita
                else -> false
            }
        }
    }

    // ✅ Fungsi untuk start activity dengan animasi fade
    private fun startActivityWithFade(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    // ✅ Override animasi saat activity dimulai
    override fun onStart() {
        super.onStart()
        // Animasi fade in saat activity dimulai
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    // ✅ Override animasi saat back ditekan
    override fun onBackPressed() {
        super.onBackPressed()
        // Animasi fade out saat kembali
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    // ✅ Opsional: Override finish untuk konsistensi
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}