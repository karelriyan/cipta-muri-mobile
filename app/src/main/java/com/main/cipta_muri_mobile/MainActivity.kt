package com.main.cipta_muri_mobile

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
// Import Activity tujuan Anda
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity
import com.main.cipta_muri_mobile.databinding.ActivityMainBinding
import com.main.cipta_muri_mobile.ui.aktivitas.RiwayatAktivitasActivity
import com.main.cipta_muri_mobile.ui.news.NewsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BottomNavigationView yang kita gunakan untuk navigasi manual
        val navView: BottomNavigationView = binding.bottomNavigationView

        // Kode Navigasi yang ERROR dihapus:
        // val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // val appBarConfiguration = AppBarConfiguration(...)
        // setupActionBarWithNavController(navController, appBarConfiguration)
        // navView.setupWithNavController(navController)

        // --- Tambahkan Listener untuk Navigasi Manual ---
        setupBottomNavListener(navView)
    }

    private fun setupBottomNavListener(navView: BottomNavigationView) {
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // R.id.navigation_home adalah ID menu untuk Home/Main (yang sedang aktif)
                R.id.navigation_home -> {
                    // Tidak perlu navigasi karena sudah di layar Home
                    true
                }

                // R.id.navigation_profile adalah ID menu ke halaman Profil
                R.id.navigation_profile -> {
                    // Pindah ke ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_news -> {
                    // Pindah ke ProfileActivity
                    val intent = Intent(this, NewsActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_history -> {
                    // Pindah ke ProfileActivity
                    val intent = Intent(this, RiwayatAktivitasActivity::class.java)
                    startActivity(intent)
                    true
                }

                // Tambahkan case untuk ID menu lainnya
                // R.id.navigation_dashboard -> { ... }
                // R.id.navigation_notifications -> { ... }

                else -> false
            }
        }
    }
}