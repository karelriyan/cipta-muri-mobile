package com.main.cipta_muri_mobile.ui.mutasi

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.data.ApiService
import com.main.cipta_muri_mobile.databinding.ActivityMutasiSaldoBinding
import com.main.cipta_muri_mobile.ui.donasi.MutasiSaldoViewModel
class MutasiSaldoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMutasiSaldoBinding
    private val viewModel: MutasiSaldoViewModel by viewModels {
        MutasiSaldoViewModelFactory(ApiService.getApiService())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMutasiSaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        // Observe data
        observeViewModel()

        // Ambil user_id dari session/local
        val userId = 1 // contoh
        viewModel.loadMutasiSaldo(userId)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.mutasiList.observe(this) { list ->
            if (list.isNotEmpty()) {
                val last = list.last()
                binding.tvTanggal.text = last.tanggal
                binding.tvKeterangan.text = last.keterangan
                binding.tvNominal.text = "Rp ${String.format("%,.0f", last.nominal)}"
            } else {
                binding.tvKeterangan.text = "Belum ada mutasi saldo"
            }
        }

        viewModel.errorMessage.observe(this) {
            it?.let { msg -> binding.tvKeterangan.text = msg }
        }
    }
}
