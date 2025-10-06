package com.main.cipta_muri_mobile.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder // <-- BARU: Import untuk dialog
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.databinding.ActivityProfileBinding
import com.main.cipta_muri_mobile.ui.aktivitas.RiwayatAktivitasActivity
import com.main.cipta_muri_mobile.ui.login.LoginActivity
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.news.NewsActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var session: SessionManager // <-- BARU: Jadikan session sebagai properti kelas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Inisialisasi SessionManager
        session = SessionManager(this)

        // ✅ Set data user
        val name = session.getUserName() ?: "Pengguna"
        binding.tvProfileName.text = name
        binding.tvProfileAvatar.text = if (name.isNotEmpty()) name.first().uppercaseChar().toString() else "P"

        // ✅ Setup bottom navigation
        setupBottomNavigation(binding.bottomNavigationView)

        // BARU: Tambahkan listener untuk tombol logout dari XML
        // Pastikan ID di XML adalah btn_logout
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    // --- Fungsi yang sudah ada (tidak ada perubahan) ---
    private fun setupBottomNavigation(navView: BottomNavigationView) {
        navView.itemIconTintList = null
        navView.selectedItemId = R.id.navigation_profile
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivityWithFade(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> true
                R.id.navigation_history -> {
                    startActivityWithFade(Intent(this, RiwayatAktivitasActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_placeholder -> true
                R.id.navigation_news -> {
                    startActivityWithFade(Intent(this, NewsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun startActivityWithFade(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    // --- FUNGSI BARU UNTUK LOGOUT ---

    /**
     * Menampilkan dialog konfirmasi sebelum logout.
     */
    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari akun Anda?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .show()
    }

    /**
     * Menjalankan proses logout dengan membersihkan session dan navigasi.
     */
    private fun performLogout() {
        // Hapus data sesi menggunakan SessionManager yang sudah ada
        session.clearSession()

        // Pindah ke LoginActivity dan hapus back stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Gunakan fungsi yang ada untuk konsistensi animasi
        startActivityWithFade(intent)
        finish()
    }
}