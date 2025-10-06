package com.main.cipta_muri_mobile.ui.saldo.konfirmasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.databinding.ActivityKonfirmasiPenarikanBinding
import com.main.cipta_muri_mobile.ui.saldo.animasi.AnimasiActivity
import com.main.cipta_muri_mobile.ui.saldo.berhasil.PenarikanBerhasilActivity

class KonfirmasiPenarikanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKonfirmasiPenarikanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonfirmasiPenarikanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {
        // 🔙 Tombol back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 💾 Tombol Simpan — misalnya kembali ke halaman utama setelah simpan
        binding.btnSimpan.setOnClickListener {
            val intent = Intent(this, PenarikanBerhasilActivity::class.java)
            startActivity(intent)
            finish()
        }

        // ❌ Tombol Batalkan — kembali ke halaman sebelumnya
        binding.btnBatalkan.setOnClickListener {
            finish()
        }

        // 🟩 Tombol Kode QR (opsional)
        binding.btnKodeQR.setOnClickListener {
            val intent = Intent(this, AnimasiActivity::class.java)
            startActivity(intent)
        }

        // 🧾 Klik pada gambar QR code → langsung ke halaman animasi (PenarikanBerhasilActivity)
        binding.imgQRCode.setOnClickListener {
            val intent = Intent(this, AnimasiActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
