package com.main.cipta_muri_mobile.ui.setor.riwayat

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatSetoranBinding
import com.main.cipta_muri_mobile.data.SessionManager

class RiwayatSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatSetoranBinding
    private val viewModel: RiwayatSetoranViewModel by viewModels()
    private lateinit var adapter: RiwayatSetoranAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatSetoranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        setupNavigation()
        observeData()

        // Panggil data dari ViewModel
        val userId = sessionManager.getUserId()
        if (!userId.isNullOrEmpty()) {
            viewModel.loadRiwayatSetoran(userId)
        }
        else {
            binding.tvKosong.apply {
                visibility = View.VISIBLE
            }
        }
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
        }
        viewModel.riwayatList.observe(this) { list ->
            if (list.isNotEmpty()) {
                binding.tvKosong.visibility = View.GONE
                adapter.updateData(list) // ✅ ini kuncinya
            } else {
                binding.tvKosong.apply {
                    visibility = View.VISIBLE
                    text = "Belum ada data penyetoran."
                }
            }
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
}
