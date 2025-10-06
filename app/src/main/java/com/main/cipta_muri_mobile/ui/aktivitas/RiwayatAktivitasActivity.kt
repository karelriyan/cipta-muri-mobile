package com.main.cipta_muri_mobile.ui.aktivitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatAktivitasBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity

class RiwayatAktivitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatAktivitasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatAktivitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Samakan ID dengan yang di XML
        val navView = binding.bottomNavigationView

        // ✅ Pakai warna asli ikon dari drawable
        navView.itemIconTintList = null

        // ✅ Tandai item aktivitas sebagai yang aktif
        navView.selectedItemId = R.id.navigation_history

        // ✅ Navigasi antar halaman
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_history -> true // halaman ini
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}
