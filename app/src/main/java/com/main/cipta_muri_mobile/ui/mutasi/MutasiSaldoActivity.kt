package com.main.cipta_muri_mobile.ui.mutasi

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.databinding.ActivityMutasiSaldoBinding
import com.main.cipta_muri_mobile.ui.mutasi.MutasiSaldoViewModel
class MutasiSaldoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMutasiSaldoBinding
    private val viewModel: MutasiSaldoViewModel by viewModels()
    private lateinit var adapter: MutasiSaldoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMutasiSaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali
        binding.btnBack.setOnClickListener { finish() }

        // Setup RecyclerView
        setupRecyclerView()

        // Observe data dari ViewModel
        observeViewModel()

        // Ambil userId dari SessionManager
        val session = SessionManager(this)
        val userId = session.getUserId() // ini yang disimpan saat login
        val userIdInt = userId?.toIntOrNull()

        if (userIdInt != null) {
            viewModel.loadMutasiSaldo(userIdInt)
        } else {
            binding.tvTampilkanLebihBanyak.text = "User ID tidak ditemukan"
        }
    }

    private fun setupRecyclerView() {
        adapter = MutasiSaldoAdapter(emptyList())
        binding.rvMutasiSaldo.layoutManager = LinearLayoutManager(this)
        binding.rvMutasiSaldo.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.mutasiList.observe(this) { list ->
            adapter.updateData(list)
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                binding.tvTampilkanLebihBanyak.text = error
            }
        }
    }
}
