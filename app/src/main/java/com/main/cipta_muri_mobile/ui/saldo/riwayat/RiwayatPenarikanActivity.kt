package com.main.cipta_muri_mobile.ui.saldo.riwayat

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatPenarikanBinding

class RiwayatPenarikanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatPenarikanBinding
    private val viewModel: RiwayatPenarikanViewModel by viewModels()
    private lateinit var adapter: RiwayatPenarikanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatPenarikanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRiwayatPenarikan()
    }

    private fun setupNavigation() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = RiwayatPenarikanAdapter(emptyList())
        binding.rvTukarPoin.layoutManager = LinearLayoutManager(this)
        binding.rvTukarPoin.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.items.observe(this) { list ->
            adapter.updateData(list)
            val showEmpty = list.isEmpty() && viewModel.errorMessage.value.isNullOrBlank()
            binding.tvKosong.isVisible = showEmpty
            if (showEmpty) {
                binding.tvKosong.text = "Belum ada data penarikan."
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
            if (!isLoading && viewModel.items.value?.isNotEmpty() == true) {
                binding.tvKosong.isVisible = false
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrBlank()) {
                binding.tvKosong.isVisible = true
                binding.tvKosong.text = error
            } else if (viewModel.items.value.isNullOrEmpty() && viewModel.isLoading.value == false) {
                binding.tvKosong.isVisible = true
                binding.tvKosong.text = "Belum ada data penarikan."
            }
        }
    }
}
