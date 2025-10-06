package com.main.cipta_muri_mobile.ui.setor

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivitySetorSampahBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.setor.harga.HargaSampahActivity
import com.main.cipta_muri_mobile.ui.setor.riwayat.RiwayatSetoranActivity

class SetorSampahActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetorSampahBinding
    private val viewModel: SetorSampahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetorSampahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        // Back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnRiwayatPenyetoran.setOnClickListener {
            startActivity(Intent(this, RiwayatSetoranActivity::class.java))
        }
        binding.btnHargaSampah.setOnClickListener {
            startActivity(Intent(this, HargaSampahActivity::class.java))
        }
        binding.btnJadwalPenarikan.setOnClickListener {
            // TODO: Tambahkan logika untuk navigasi ke jadwal penarikan
        }
        binding.btnLokasiBankSampah.setOnClickListener {
            // TODO: Tambahkan logika untuk navigasi ke lokasi bank sampah
        }




    }

}
