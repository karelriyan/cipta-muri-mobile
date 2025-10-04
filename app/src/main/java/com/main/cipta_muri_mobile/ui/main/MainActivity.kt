package com.main.cipta_muri_mobile.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Build
import androidx.activity.viewModels // Untuk memanggil ViewModel
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityMainBinding
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity
import com.main.cipta_muri_mobile.data.SessionManager // WAJIB untuk data sesi
import com.main.cipta_muri_mobile.ui.login.LoginViewModel // Ganti dengan ViewModel Anda (jika perlu)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels() // Panggil MainViewModel Anda
    private lateinit var sessionManager: SessionManager // Deklarasikan SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // --- 1. PERBAIKAN TAMPILAN (HILANGKAN BAR UNGU) ---
        // Sembunyikan ActionBar/Toolbar
        supportActionBar?.hide()

        // Atur Status Bar Transparan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigationView

        // --- 2. MEMUAT DATA DAN OBSERVASI ---
        loadDataAndObserveViewModel()

        // --- 3. LISTENER NAVIGASI ---
        setupBottomNavListener(navView)
    }

    private fun loadDataAndObserveViewModel() {
        // Ambil data sesi awal untuk ditampilkan segera
        val namaSesi = sessionManager.getUserName()
        binding.tvUserName.text = namaSesi ?: "Pengguna Cipta Muri"

        // Amati (Observe) LiveData dari ViewModel untuk data yang lebih dinamis atau terformat
        viewModel.user.observe(this) { user ->
            // Pastikan Anda memformat data agar terlihat di UI
            binding.tvUserName.text = user.name // Data dari ViewModel (lebih up-to-date)
            binding.tvBalance.text = "Rp ${"%,.2f".format(user.balance)}"
            binding.tvTotalWeight.text = "Total Berat Sampah Terjual: ${user.totalWasteKg}Kg"
            // Pastikan binding.tvAccountNumber juga diisi
        }
    }

    private fun setupBottomNavListener(navView: BottomNavigationView) {
        // Hapus penandaan item yang dipilih secara otomatis, agar kode di bawah yang mengontrol
        navView.selectedItemId = R.id.navigation_home

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // R.id.navigation_home: Tetap di Activity ini
                R.id.navigation_home -> {
                    true
                }

                // R.id.navigation_profile: Pindah ke ProfileActivity
                R.id.navigation_profile -> {
                    // Pindah ke ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }

                // R.id.navigation_notifications: Tambahkan navigasi ke Notifikasi
                R.id.navigation_notifications -> {
                    // TODO: Tambahkan navigasi ke Activity/Fragment Notifikasi Anda
                    true
                }

                else -> false
            }
        }
    }
}