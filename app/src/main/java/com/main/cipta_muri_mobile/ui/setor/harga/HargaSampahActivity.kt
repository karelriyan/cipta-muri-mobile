package com.main.cipta_muri_mobile.ui.setor.harga

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.databinding.ActivityHargaSampahBinding


class HargaSampahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHargaSampahBinding
    private val viewModel: HargaSampahViewModel by viewModels()
    private lateinit var adapter: HargaSampahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHargaSampahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupList()
        observeData()
        setupLoadMore()

        viewModel.loadHargaSampah()
    }

    private fun setupNavigation() {
        // Back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupList() {
        adapter = HargaSampahAdapter(emptyList())
        binding.rvHargaSampah.layoutManager = LinearLayoutManager(this)
        binding.rvHargaSampah.adapter = adapter
    }

    private fun setupLoadMore() {
        binding.tvLoadMore.setOnClickListener {
            viewModel.loadMore()
        }
    }

    private fun observeData() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.tvLoadMore.isEnabled = !isLoading
            if (isLoading) {
                binding.tvKosong.visibility = View.GONE
            }
        }

        viewModel.items.observe(this) { list ->
            adapter.updateData(list)
            if (list.isEmpty() && viewModel.isLoading.value != true) {
                binding.tvKosong.visibility = View.VISIBLE
                binding.tvKosong.text = "Belum ada data harga sampah."
            } else {
                binding.tvKosong.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(this) { msg ->
            if (!msg.isNullOrBlank()) {
                binding.tvKosong.visibility = View.VISIBLE
                binding.tvKosong.text = msg
            }
        }

        viewModel.showLoadMore.observe(this) { show ->
            binding.tvLoadMore.visibility = if (show) View.VISIBLE else View.GONE
        }

        viewModel.lastUpdated.observe(this) { date ->
            if (!date.isNullOrBlank()) {
                binding.tvLastUpdate.text = date
            }
        }
    }

}
