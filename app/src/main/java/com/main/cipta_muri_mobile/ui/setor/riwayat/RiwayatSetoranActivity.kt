package com.main.cipta_muri_mobile.ui.setor.riwayat

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatSetoranBinding
import com.main.cipta_muri_mobile.data.SessionManager

class RiwayatSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatSetoranBinding
    private val viewModel: RiwayatSetoranViewModel by viewModels()
    private lateinit var adapter: RiwayatSetoranAdapter
    private lateinit var sessionManager: SessionManager
    private var isBottomRefreshing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatSetoranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        setupNavigation()
        observeData()
        setupScrollListener()

        binding.tvLoadMore.setOnClickListener { viewModel.loadMore() }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupRecyclerView() {
        adapter = RiwayatSetoranAdapter(emptyList())
        binding.rvRiwayatSetoran.layoutManager = LinearLayoutManager(this)
        binding.rvRiwayatSetoran.adapter = adapter
    }

    private fun setupNavigation() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeData() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) {
                isBottomRefreshing = false
            }
        }
        viewModel.riwayatList.observe(this) { list ->
            if (list.isNotEmpty()) {
                binding.tvKosong.visibility = View.GONE
                adapter.updateData(list)
            } else {
                binding.tvKosong.apply {
                    visibility = View.VISIBLE
                    text = "Belum ada data penyetoran."
                }
            }
            binding.tvLoadMore.visibility = if (viewModel.hasMore()) View.VISIBLE else View.GONE
            binding.tvLoadMore.text = if (viewModel.hasMore()) "Tampilkan Lebih Banyak" else "Tidak ada data lagi"
        }


        viewModel.errorMessage.observe(this) { error ->
            if (error.isNotEmpty()) {
                binding.tvKosong.apply {
                    visibility = View.VISIBLE
                    text = error
                }
            }
        }
    }

    private fun refreshData() {
        val userId = sessionManager.getUserId()
        if (!userId.isNullOrEmpty()) {
            binding.tvKosong.visibility = View.GONE
            viewModel.loadRiwayatSetoran(userId)
        } else {
            binding.tvKosong.apply {
                visibility = View.VISIBLE
                text = "Belum ada data penyetoran."
            }
        }
    }

    private fun setupScrollListener() {
        binding.nestedScroll.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                val nested = v as? NestedScrollView ?: return@OnScrollChangeListener
                val contentHeight = nested.getChildAt(0)?.measuredHeight ?: return@OnScrollChangeListener
                val containerHeight = nested.measuredHeight

                val isAtBottom = scrollY >= (contentHeight - containerHeight)
                val isLoading = viewModel.isLoading.value == true

                if (isAtBottom && !isLoading && !isBottomRefreshing) {
                    isBottomRefreshing = true
                    if (viewModel.hasMore()) {
                        viewModel.loadMore()
                        isBottomRefreshing = false
                    } else {
                        refreshData()
                    }
                }
            }
        )
    }
}
