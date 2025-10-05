package com.main.cipta_muri_mobile.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.data.User
import com.main.cipta_muri_mobile.databinding.ActivityMainBinding
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 1. Hilangkan action bar & buat status bar transparan
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // ✅ 2. Kirim data user login dari SessionManager ke ViewModel
        setUserDataFromSession()

        // ✅ 3. Observe LiveData agar UI selalu update
        observeUserData()

        // ✅ 4. Bottom navigation
        setupBottomNavListener(binding.bottomNavigationView)
    }

    private fun setUserDataFromSession() {
        val user = User(
            id = sessionManager.getUserId().orEmpty(),
            nik = sessionManager.getUserNik().orEmpty(),
            userId = sessionManager.getUsername().orEmpty(),
            name = sessionManager.getUserName().orEmpty(),
            accountNumber = sessionManager.getUserAccountNumber().orEmpty(),
            balance = sessionManager.getUserBalance() ?: 0.0,
            points = sessionManager.getUserPoints() ?: 0,
            totalWasteKg = sessionManager.getUserTotalWaste() ?: 0
        )

        // ✅ Kirim ke ViewModel
        viewModel.setUserData(user)
    }

    private fun observeUserData() {
        viewModel.user.observe(this) { user ->
            user?.let {
                binding.tvUserName.text = it.name.ifEmpty { "Pengguna Cipta Muri" }
                binding.tvBalance.text = "Rp ${"%,.2f".format(it.balance)}"
                binding.tvTotalWeight.text = "Total Berat Sampah Terjual: ${it.totalWasteKg} Kg"
                binding.tvAccountNumber.text = "No. Rekening: ${it.accountNumber}"
            }
        }
    }

    private fun setupBottomNavListener(navView: BottomNavigationView) {
        navView.itemIconTintList = null
        navView.selectedItemId = R.id.navigation_home

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.navigation_placeholder -> {
                    // TODO: Tambahkan navigasi ke Notifikasi
                    true
                }
                else -> false
            }
        }
    }
}
