package com.main.cipta_muri_mobile.ui.leaderboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.TextView
import androidx.activity.viewModels
import com.main.cipta_muri_mobile.databinding.ActivityLeaderboardBinding

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Sembunyikan ActionBar bawaan agar Toolbar kustom Anda terlihat
        supportActionBar?.hide()

        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()

        // (TODO) Anda perlu menambahkan kode untuk menyiapkan RecyclerView di sini
        // setupRecyclerView()
    }

    private fun setupNavigation() {
        // 2. Gunakan ID yang benar dari file XML (`iv_back_arrow`)
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // ✅ Override animasi saat activity dimulai
    override fun onStart() {
        super.onStart()
        // Animasi fade in saat activity dimulai
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    // ✅ Override animasi saat back ditekan
    override fun onBackPressed() {
        super.onBackPressed()
        // Animasi fade out saat kembali
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    // (Contoh fungsi untuk RecyclerView, Anda akan memerlukannya nanti)
    /*
    private fun setupRecyclerView() {
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(this)
        // val adapter = LeaderboardAdapter(dataAnda)
        // binding.rvLeaderboard.adapter = adapter
    }
    */
}