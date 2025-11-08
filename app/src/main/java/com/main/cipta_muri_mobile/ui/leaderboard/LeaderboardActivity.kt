package com.main.cipta_muri_mobile.ui.leaderboard

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityLeaderboardBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val vm: RankingViewModel by viewModels()
    private var currentTab = 0 // 0: Paling Banyak, 1: Paling Rajin Setor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupTabsAndContent()
    }

    private fun setupNavigation() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupTabsAndContent() {
        // Recycler for 4..10
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(this)

        // default: Paling Banyak
        updateTabsAlpha(0)

        // Collect API state
        vm.load(limit = 10)
        lifecycleScope.launch {
            vm.state.collectLatest { state -> renderState(state) }
        }

        // Tab click listeners
        binding.tabLeft.setOnClickListener { switchTo(0) }
        binding.tabRight.setOnClickListener { switchTo(1) }

        // Swipe between tabs
        val detector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private val swipeThreshold = 100
            private val swipeVelocityThreshold = 100
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val absDiffX = kotlin.math.abs(diffX)
                val absVelX = kotlin.math.abs(velocityX)
                if (absDiffX > swipeThreshold && absVelX > swipeVelocityThreshold) {
                    if (diffX < 0) switchTo(1) else switchTo(0)
                    return true
                }
                return false
            }
        })
        binding.scrollContent.setOnTouchListener { _, event -> detector.onTouchEvent(event) }
    }

    private fun switchTo(tab: Int) {
        if (currentTab == tab) return
        currentTab = tab
        updateTabsAlpha(tab)
        // re-render with current tab
        lifecycleScope.launch { renderState(vm.state.value) }
    }

    private fun renderState(state: RankingUiState) {
        val mapped = if (currentTab == 0) {
            state.berat.mapIndexed { idx, item ->
                val pts = item.total_berat.toDoubleOrNull()?.toInt() ?: 0
                LeaderboardItem((idx + 1).toString(), item.nama ?: "-", item.no_rekening ?: "-", pts)
            }
        } else {
            state.setor.mapIndexed { idx, item ->
                LeaderboardItem((idx + 1).toString(), item.nama ?: "-", item.no_rekening ?: "-", item.total_setor)
            }
        }

        val podium = mapped.take(3)
        if (podium.size >= 3) {
            val first = podium[0]
            val second = podium[1]
            val third = podium[2]
            binding.tvFirstName.text = first.name
            binding.tvFirstPoints.text = first.points.toString()
            binding.tvSecondName.text = second.name
            binding.tvSecondPoints.text = second.points.toString()
            binding.tvThirdName.text = third.name
            binding.tvThirdPoints.text = third.points.toString()
        }

        val rest = if (mapped.size > 3) mapped.drop(3) else emptyList()
        binding.rvLeaderboard.adapter = LeaderboardAdapter(rest)
    }

    private fun updateTabsAlpha(position: Int) {
        val active = 1.0f
        val inactive = 0.4f
        binding.imgTabLeftBg.alpha = if (position == 0) active else inactive
        binding.imgTabRightBg.alpha = if (position == 1) active else inactive
        binding.txtTabLeft.alpha = if (position == 0) 1.0f else 0.6f
        binding.txtTabRight.alpha = if (position == 1) 1.0f else 0.6f
    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
