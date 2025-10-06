package com.main.cipta_muri_mobile.ui.saldo.riwayat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R
import android.content.Intent
import android.widget.TextView
import androidx.activity.viewModels
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatPenarikanBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity

class RiwayatPenarikanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatPenarikanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatPenarikanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        // Back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}
