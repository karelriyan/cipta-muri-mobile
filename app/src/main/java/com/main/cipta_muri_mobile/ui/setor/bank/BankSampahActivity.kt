package com.main.cipta_muri_mobile.ui.setor.bank

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.ui.aktivitas.RiwayatAktivitasActivity
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity

class BankSampahActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_sampah)
    }

    private fun setupBottomNavigation(navView: BottomNavigationView) {
        navView.itemIconTintList = null

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
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
