package com.main.cipta_muri_mobile.ui.saldo.berhasil

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.main.cipta_muri_mobile.databinding.ActivityPenarikanBerhasilBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PenarikanBerhasilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPenarikanBerhasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenarikanBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupButtons()
    }

    private fun setupUI() {
        // Format waktu otomatis
        val currentDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        binding.tvDateTime.text = currentDateTime

        // Ambil data dari Intent
        val nama = intent.getStringExtra("nama") ?: "Karel Tsalasatir Riyan"
        val nik = intent.getStringExtra("nik") ?: "329358329852397"
        val petugas = intent.getStringExtra("petugas") ?: "Rifky Prakoso"
        val harga = intent.getStringExtra("harga") ?: "Rp50.000,00"
        val total = intent.getStringExtra("total") ?: "Rp50.000,00"

        // Tampilkan data
        binding.tvPenarikanOleh.text = nama
        binding.tvNik.text = nik
        binding.tvPetugas.text = petugas
        binding.tvHarga.text = harga
        binding.tvTotalTransaksi.text = total
    }

    private fun setupButtons() {
        // Tombol kembali ke MainActivity
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Tombol Bagikan (gambar)
        binding.btnBagikan.setOnClickListener {
            try {
                val imageUri = captureViewAsImage()
                if (imageUri != null) {
                    shareImage(imageUri)
                } else {
                    Toast.makeText(this, "Gagal membuat gambar.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔹 Ambil screenshot dari card transaksi (CardView)
    private fun captureViewAsImage(): Uri? {
        val view = binding.root // atau bisa binding.cardViewUtama kalau kamu beri id
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false

        // Simpan bitmap ke cache
        val file = File(cacheDir, "transaksi_${System.currentTimeMillis()}.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()

        // Gunakan FileProvider biar bisa di-share
        return FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider", // pastikan sama dengan di manifest
            file
        )
    }

    // 🔹 Kirim intent share gambar
    private fun shareImage(imageUri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Bagikan melalui"))
    }
}
