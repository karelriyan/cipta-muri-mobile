package com.main.cipta_muri_mobile.ui.setor.harga

import com.main.cipta_muri_mobile.ui.setor.harga.HargaSampahViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityHargaSampahBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity


class HargaSampahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHargaSampahBinding
    private val viewModel: HargaSampahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHargaSampahBinding.inflate(layoutInflater)
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
