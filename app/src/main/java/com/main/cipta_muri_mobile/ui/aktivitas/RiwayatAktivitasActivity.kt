package com.main.cipta_muri_mobile.ui.aktivitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatAktivitasBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.news.NewsActivity
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.data.Aktivitas

class RiwayatAktivitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatAktivitasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatAktivitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.bottomNavigationView
        navView.itemIconTintList = null
        navView.selectedItemId = R.id.navigation_history

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_history -> true
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_news -> {
                    startActivity(Intent(this, NewsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        // ✅ Tambahkan dummy data
        val dummyList = listOf(
            Aktivitas("09 Maret 2025", "Saldo Masuk", "Hasil Penjualan Sampah", "+Rp 10.000,00", true),
            Aktivitas("09 Maret 2025", "Saldo Keluar", "Tarik Tunai ke Rekening", "-Rp 5.000,00", false),
            Aktivitas("08 Maret 2025", "Saldo Masuk", "Bonus Poin Penukaran", "+Rp 2.500,00", true),
            Aktivitas("07 Maret 2025", "Saldo Keluar", "Pembayaran Tagihan", "-Rp 15.000,00", false)
        )

        // ✅ Set adapter RecyclerView
        binding.rvAktivitas.layoutManager = LinearLayoutManager(this)
        binding.rvAktivitas.adapter = AktivitasAdapter(dummyList)
    }
}

