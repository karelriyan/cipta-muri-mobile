package com.main.cipta_muri_mobile.ui.news

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityNewsBinding
import com.main.cipta_muri_mobile.databinding.SectionNewsBinding
import com.main.cipta_muri_mobile.ui.aktivitas.RiwayatAktivitasActivity
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity
import com.main.cipta_muri_mobile.ui.setor.SetorSampahActivity

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private var isBottomRefreshing: Boolean = false
    private val viewModel: NewsViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var sectionNewsBinding: SectionNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sectionNewsBinding = SectionNewsBinding.bind(binding.root.findViewById(R.id.sectionNews))

        setupNewsList()
        setupBottomNavigation(findViewById(R.id.bottom_navigation_view))
        setupScrollListener()

        viewModel.refresh()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupBottomNavigation(navView: BottomNavigationView) {
        // 🔥 Supaya ikon tampil dengan warna asli dari drawable
        navView.itemIconTintList = null

        // ✅ Tandai item News sebagai yang terpilih
        navView.selectedItemId = R.id.navigation_news

        // FAB QR from included bottom nav
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_qr)
            ?.setOnClickListener {
                startActivityWithFade(Intent(this, SetorSampahActivity::class.java))
            }

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivityWithFade(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    startActivityWithFade(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_history -> {
                    startActivityWithFade(Intent(this, RiwayatAktivitasActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_placeholder -> true // kalau nanti mau pakai FAB
                R.id.navigation_news -> {

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

    private fun setupScrollListener() {
        binding.nestedScroll.setOnScrollChangeListener(
            androidx.core.widget.NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                val nested = v as? androidx.core.widget.NestedScrollView ?: return@OnScrollChangeListener
                val contentHeight = nested.getChildAt(0)?.measuredHeight ?: return@OnScrollChangeListener
                val containerHeight = nested.measuredHeight
                val isAtBottom = scrollY >= (contentHeight - containerHeight)
                if (isAtBottom && !isBottomRefreshing) {
                    isBottomRefreshing = true
                    if (viewModel.state.value?.hasMore == true) {
                        viewModel.loadMore()
                    }
                    isBottomRefreshing = false
                }
            }
        )
    }

    private fun refreshData() {
        viewModel.refresh()
    }

    private fun setupNewsList() {
        newsAdapter = NewsAdapter()
        sectionNewsBinding.rvNews.apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = newsAdapter
        }
        sectionNewsBinding.tvViewMore.isVisible = false
        sectionNewsBinding.tvViewMore.text = "Tampilkan Lebih Banyak"
        sectionNewsBinding.tvViewMore.setOnClickListener {
            viewModel.loadMore()
        }

        viewModel.state.observe(this) { state ->
            sectionNewsBinding.progressNews.isVisible = state.loading

            val showMessage = !state.loading && (state.error != null || state.items.isEmpty())
            sectionNewsBinding.tvNewsMessage.isVisible = showMessage
            sectionNewsBinding.tvNewsMessage.text = state.error ?: "Belum ada berita"

            sectionNewsBinding.rvNews.isVisible = state.items.isNotEmpty()
            sectionNewsBinding.tvViewMore.isVisible = state.items.isNotEmpty() && state.hasMore

            newsAdapter.submitList(state.items)
        }
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
