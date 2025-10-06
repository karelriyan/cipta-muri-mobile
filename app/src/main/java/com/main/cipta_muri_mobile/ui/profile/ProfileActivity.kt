package com.main.cipta_muri_mobile.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.databinding.ActivityProfileBinding
import com.main.cipta_muri_mobile.ui.aktivitas.RiwayatAktivitasActivity
import com.main.cipta_muri_mobile.ui.main.MainActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Set data user
        val session = SessionManager(this)
        val name = session.getUserName() ?: "Pengguna"

        binding.tvProfileName.text = name
        binding.tvProfileAvatar.text = name.first().uppercaseChar().toString()

        // ✅ Setup bottom navigation (warna ikon asli + item profile terpilih)
        setupBottomNavigation(binding.bottomNavigationView)
    }

    private fun setupBottomNavigation(navView: BottomNavigationView) {
        // 🔥 Supaya ikon tampil dengan warna asli dari drawable
        navView.itemIconTintList = null

        // ✅ Tandai item Profile sebagai yang terpilih
        navView.selectedItemId = R.id.navigation_profile

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> true // sudah di halaman ini
                R.id.navigation_history -> {
                    startActivity(Intent(this, RiwayatAktivitasActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_placeholder -> true // kalau nanti mau pakai FAB
                R.id.navigation_news -> true // kalau nanti mau pakai berita
                else -> false
            }
        }
    }
}
