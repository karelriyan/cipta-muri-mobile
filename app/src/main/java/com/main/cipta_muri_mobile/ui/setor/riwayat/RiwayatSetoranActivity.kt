package com.main.cipta_muri_mobile.ui.setor.riwayat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.RiwayatSetoran
import com.main.cipta_muri_mobile.ui.setor.riwayat.RiwayatSetoranViewModel
import android.content.Intent
import android.widget.TextView
import androidx.activity.viewModels
import com.main.cipta_muri_mobile.databinding.ActivityRiwayatSetoranBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity


class RiwayatSetoranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatSetoranBinding
    private val viewModel: RiwayatSetoranViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatSetoranBinding.inflate(layoutInflater)
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
