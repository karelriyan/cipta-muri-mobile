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
import com.main.cipta_muri_mobile.ui.aktivitas.RiwayatAktivitasActivity
import com.main.cipta_muri_mobile.ui.donasi.DonasiSampahActivity
import com.main.cipta_muri_mobile.ui.leaderboard.LeaderboardActivity
import com.main.cipta_muri_mobile.ui.mutasi.MutasiSaldoActivity
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity
import com.main.cipta_muri_mobile.ui.saldo.tarik.TarikSaldoActivity
import com.main.cipta_muri_mobile.ui.saldo.riwayat.RiwayatPenarikanActivity

import com.main.cipta_muri_mobile.ui.setor.SetorSampahActivity
import com.main.cipta_muri_mobile.ui.setor.riwayat.RiwayatSetoranActivity

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

        // ✅ 5. Setup navigasi menu utama (GridLayout)
        setupMenuNavigation()
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
                binding.tvBalance.text = com.main.cipta_muri_mobile.util.Formatters.formatRupiah(it.balance)
                binding.tvTotalWeight.text = "Total Berat Sampah Terjual: ${it.totalWasteKg} Kg"
                binding.tvAccountNumber.text = "No. Rekening: ${it.accountNumber}"
            }
        }
        viewModel.userInitial.observe(this) { initial ->
            binding.tvProfileInitial.text = initial
        }
    }

    private fun setupBottomNavListener(navView: BottomNavigationView) {
        navView.itemIconTintList = null
        navView.selectedItemId = R.id.navigation_home

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_profile -> {
                    startActivityWithFade(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.navigation_placeholder -> {
                    // TODO: Tambahkan navigasi ke SetorSammpah Tapi di FAB
                    true
                }
                R.id.navigation_news -> {
                    // TODO: NEWS
                    true
                }
                R.id.navigation_history -> {
                    startActivityWithFade(Intent(this, RiwayatAktivitasActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupMenuNavigation() {
        // Setor Sampah
        binding.mnSetor.setOnClickListener {
            startActivityWithFade(Intent(this, SetorSampahActivity::class.java))
        }

        // Riwayat Setoran
        binding.mnRiwayatSetor.setOnClickListener {
            startActivityWithFade(Intent(this, RiwayatSetoranActivity::class.java))
        }

        // Tarik Saldo
        binding.mnTarik.setOnClickListener {
            startActivityWithFade(Intent(this, TarikSaldoActivity::class.java))
        }

        // Riwayat Penarikan
        binding.mnRiwayatPenarikan.setOnClickListener {
            startActivityWithFade(Intent(this, RiwayatPenarikanActivity::class.java))
        }

        // Donasi Sampah
        binding.mnDonasi.setOnClickListener {
            startActivityWithFade(Intent(this, DonasiSampahActivity::class.java))
        }

        // Leaderboard
        binding.mnLeaderboard.setOnClickListener {
            startActivityWithFade(Intent(this, LeaderboardActivity::class.java))
        }

        // Mutasi Saldo
        binding.cardSaldo.setOnClickListener {
            startActivityWithFade(Intent(this, MutasiSaldoActivity::class.java))
        }

        // Profil Nasabah
        binding.tvProfileInitial.setOnClickListener {
            startActivityWithFade(Intent(this, ProfileActivity::class.java))
        }

        // FAB QR
        binding.fabQr.setOnClickListener {
            startActivityWithFade(Intent(this, SetorSampahActivity::class.java))
        }

        // FAB Leaderboard
        binding.fabLeaderboard.setOnClickListener {
            startActivityWithFade(Intent(this, LeaderboardActivity::class.java))
        }
    }

    // ✅ Fungsi untuk navigasi dengan efek fade
    private fun startActivityWithFade(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    // ✅ Opsional: Tambahkan juga untuk back button jika ingin konsisten
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
