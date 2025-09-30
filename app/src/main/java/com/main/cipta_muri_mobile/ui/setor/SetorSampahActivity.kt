package com.main.cipta_muri_mobile.ui.setor

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R

class SetorSampahActivity : AppCompatActivity() {

    private val viewModel: SetorSampahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setor_sampah)

//        val menuBank = findViewById<TextView>(R.id.menuBank)
//        val menuJadwal = findViewById<TextView>(R.id.menuJadwal)
//        val menuRiwayat = findViewById<TextView>(R.id.menuRiwayat)
//        val menuHarga = findViewById<TextView>(R.id.menuHarga)

        // Observe LiveData untuk navigasi
//        viewModel.navigateTo.observe(this) { destination ->
//            when (destination) {
//                SetorSampahViewModel.Destination.BANK -> {
//                    startActivity(Intent(this, BankSampahActivity::class.java))
//                }
//                SetorSampahViewModel.Destination.JADWAL -> {
//                    startActivity(Intent(this, JadwalPenarikanActivity::class.java))
//                }
//                SetorSampahViewModel.Destination.RIWAYAT -> {
//                    startActivity(Intent(this, RiwayatPenyetoranActivity::class.java))
//                }
//                SetorSampahViewModel.Destination.HARGA -> {
//                    startActivity(Intent(this, HargaSampahActivity::class.java))
//                }
//            }
//        }

//        menuBank.setOnClickListener { viewModel.onMenuClicked(SetorSampahViewModel.Destination.BANK) }
//        menuJadwal.setOnClickListener { viewModel.onMenuClicked(SetorSampahViewModel.Destination.JADWAL) }
//        menuRiwayat.setOnClickListener { viewModel.onMenuClicked(SetorSampahViewModel.Destination.RIWAYAT) }
//        menuHarga.setOnClickListener { viewModel.onMenuClicked(SetorSampahViewModel.Destination.HARGA) }
//    }
//}
