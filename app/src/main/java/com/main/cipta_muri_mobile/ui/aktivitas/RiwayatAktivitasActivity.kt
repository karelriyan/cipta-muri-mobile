package com.main.cipta_muri_mobile.ui.aktivitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatAktivitasBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.news.NewsActivity
import com.main.cipta_muri_mobile.ui.setor.SetorSampahActivity
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.data.Aktivitas
import com.main.cipta_muri_mobile.data.ApiRepository
import kotlinx.coroutines.launch
import com.main.cipta_muri_mobile.util.Formatters

class RiwayatAktivitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatAktivitasBinding
    private lateinit var adapter: AktivitasAdapter
    private var isBottomRefreshing: Boolean = false
    private var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatAktivitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation_view)
        navView.itemIconTintList = null
        navView.selectedItemId = R.id.navigation_history

        // FAB QR from included bottom nav
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_qr)
            ?.setOnClickListener {
                startActivityWithFade(Intent(this, SetorSampahActivity::class.java))
            }

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

        // ✅ Setup RecyclerView + Adapter
        binding.rvAktivitas.layoutManager = LinearLayoutManager(this)
        adapter = AktivitasAdapter(emptyList())
        binding.rvAktivitas.adapter = adapter

        // ✅ Listener scroll untuk refresh saat mentok bawah
        setupScrollListener()

        // ✅ Muat awal / refresh saat halaman dibuka
        refreshData()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupScrollListener() {
        binding.rvAktivitas.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !recyclerView.canScrollVertically(1) && !isBottomRefreshing && !isLoading) {
                    isBottomRefreshing = true
                    refreshData()
                }
            }
        })
    }

    private fun refreshData() {
        lifecycleScope.launch {
            isLoading = true
            try {
                val repo = ApiRepository(this@RiwayatAktivitasActivity)
                val result = repo.getSaldoTransactions(null)
                result.onSuccess { list ->
                    val mapped = list.map { tx ->
                        val isMasuk = tx.type.equals("credit", ignoreCase = true)
                        val nominalDisp = Formatters.formatRupiah(tx.amount, isMasuk)
                        Aktivitas(
                            tanggal = Formatters.formatTanggalIndo(tx.createdAt),
                            jenis = if (isMasuk) "Saldo Masuk" else "Saldo Keluar",
                            keterangan = tx.description ?: "",
                            jumlah = nominalDisp,
                            isMasuk = isMasuk,
                            waktu = Formatters.formatWaktu(tx.createdAt)
                        )
                    }
                    adapter.updateData(mapped)
                }.onFailure {
                    // fallback: keep current data or clear
                }
            } finally {
                isLoading = false
                isBottomRefreshing = false
            }
        }
    }

    private fun startActivityWithFade(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
