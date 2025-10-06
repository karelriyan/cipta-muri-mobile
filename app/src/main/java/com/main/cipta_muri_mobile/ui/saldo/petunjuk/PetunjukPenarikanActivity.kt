package com.main.cipta_muri_mobile.ui.saldo.petunjuk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.databinding.ActivityPetunjukPenarikanBinding

class PetunjukPenarikanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPetunjukPenarikanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetunjukPenarikanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
    }

    private fun setupListener() {
        // 🔙 Tombol "kembali" di bagian bawah
        binding.tvKembali.setOnClickListener {
            // kembali ke halaman sebelumnya
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
