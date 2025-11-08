package com.main.cipta_muri_mobile.ui.leaderboard

data class LeaderboardItem(
    val rank: String,
    val name: String,
    val address: String,
    val points: Int,
    val pointsText: String? = null
)
