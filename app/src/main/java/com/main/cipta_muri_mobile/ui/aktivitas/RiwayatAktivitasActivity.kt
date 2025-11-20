package com.main.cipta_muri_mobile.ui.aktivitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.Aktivitas
import com.main.cipta_muri_mobile.data.ApiRepository
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatAktivitasBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.news.NewsActivity
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity
import com.main.cipta_muri_mobile.ui.setor.SetorSampahActivity
import com.main.cipta_muri_mobile.util.Formatters
import kotlinx.coroutines.launch
import kotlin.math.min

class RiwayatAktivitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatAktivitasBinding
    private lateinit var adapter: AktivitasAdapter
    private var isBottomRefreshing: Boolean = false
    private var isLoading: Boolean = false
    private val initialLimit = 10
    private val loadStep = 5
    private var allItems: List<Aktivitas> = emptyList()
    private var visibleCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatAktivitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav(findViewById(R.id.bottom_navigation_view))

        binding.rvAktivitas.layoutManager = LinearLayoutManager(this)
        adapter = AktivitasAdapter(emptyList())
        binding.rvAktivitas.adapter = adapter

        setupScrollListener()

        binding.tvLoadMore.setOnClickListener { loadMore() }

        refreshData()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupBottomNav(navView: BottomNavigationView) {
        navView.itemIconTintList = null
        navView.selectedItemId = R.id.navigation_history

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
    }

    private fun setupScrollListener() {
        binding.rvAktivitas.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && !recyclerView.canScrollVertically(1) && !isBottomRefreshing && !isLoading) {
                    isBottomRefreshing = true
                    if (allItems.size > visibleCount) {
                        loadMore()
                        isBottomRefreshing = false
                    } else {
                        refreshData()
                    }
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
                    applyData(mapped)
                }.onFailure {
                    // keep existing data on failure
                }
            } finally {
                isLoading = false
                isBottomRefreshing = false
            }
        }
    }

    private fun applyData(items: List<Aktivitas>) {
        allItems = items
        visibleCount = min(initialLimit, allItems.size)
        renderVisible()
    }

    private fun loadMore() {
        if (allItems.isEmpty()) return
        val newCount = min(allItems.size, visibleCount + loadStep)
        if (newCount != visibleCount) {
            visibleCount = newCount
            renderVisible()
        }
    }

    private fun renderVisible() {
        val visible = allItems.take(visibleCount)
        adapter.updateData(visible)
        val hasMore = allItems.size > visibleCount
        binding.tvLoadMore.isVisible = allItems.isNotEmpty()
        binding.tvLoadMore.text = if (hasMore) "Tampilkan Lebih Banyak" else if (allItems.isEmpty()) "Belum ada data" else "Tidak ada data lagi"
    }

    private fun startActivityWithFade(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
