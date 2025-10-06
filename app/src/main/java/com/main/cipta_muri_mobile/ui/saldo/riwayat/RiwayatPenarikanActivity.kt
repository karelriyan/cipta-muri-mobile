package com.main.cipta_muri_mobile.ui.saldo.riwayat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatPenarikanBinding

class RiwayatPenarikanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatPenarikanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatPenarikanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupRecyclerView()
    }

    private fun setupNavigation() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        val dataDummy = listOf(
            RiwayatPenarikanItem("05 FEBRUARI 2025", "Saldo Keluar", "Lihat Rincian", "-Rp 150.000,00", getColor(R.color.red)),
            RiwayatPenarikanItem("03 FEBRUARI 2025", "Saldo Masuk", "Lihat Rincian", "+Rp 300.000,00", getColor(R.color.green)),
            RiwayatPenarikanItem("28 JANUARI 2025", "Saldo Keluar", "Lihat Rincian", "-Rp 90.000,00", getColor(R.color.red))
        )

        binding.rvTukarPoin.layoutManager = LinearLayoutManager(this)
        binding.rvTukarPoin.adapter = RiwayatPenarikanAdapter(dataDummy)
    }
}
