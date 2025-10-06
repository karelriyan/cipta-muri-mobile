package com.main.cipta_muri_mobile.ui.leaderboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityLeaderboardBinding

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupRecyclerView() // 🔥 panggil langsung biar tampil di Android
    }

    private fun setupNavigation() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        // 🔹 Dummy data
        val dataDummy = listOf(
            LeaderboardItem("4", "Budi Santoso", "RT. 01/RW. 02", 1560),
            LeaderboardItem("5", "Siti Nurhaliza", "RT. 03/RW. 01", 1480),
            LeaderboardItem("6", "Rizky Ananda", "RT. 02/RW. 05", 1360),
            LeaderboardItem("7", "Andi Saputra", "RT. 04/RW. 03", 1250),
            LeaderboardItem("8", "Nurul Aini", "RT. 05/RW. 06", 1190),
            LeaderboardItem("9", "Ahmad Faiz", "RT. 06/RW. 07", 1130)
        )

        // 🔹 Setup RecyclerView
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(this)
        binding.rvLeaderboard.adapter = LeaderboardAdapter(dataDummy)
    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
