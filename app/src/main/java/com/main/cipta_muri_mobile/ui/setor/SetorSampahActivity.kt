package com.main.cipta_muri_mobile.ui.setor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivitySetorSampahBinding
import com.main.cipta_muri_mobile.ui.setor.bank.BankSampahActivity
import com.main.cipta_muri_mobile.ui.setor.harga.HargaSampahActivity
import com.main.cipta_muri_mobile.ui.setor.riwayat.RiwayatSetoranActivity

class SetorSampahActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetorSampahBinding
    private val viewModel: SetorSampahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetorSampahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMapView()       // tampilkan mini map
        setupNavigation()    // navigasi antar tombol
    }

    // 🌍 Menampilkan peta kecil di dalam halaman ini
    private fun setupMapView() {
        val webViewMap = binding.root.findViewById<WebView>(R.id.webViewMapLokasi)
        val webSettings: WebSettings = webViewMap.settings
        webSettings.javaScriptEnabled = true
        webViewMap.webViewClient = WebViewClient()

        val latitude = -7.449258
        val longitude = 109.363158
        val namaBank = "Bank Sampah Sahabatku"

        // HTML iframe map mini
        val html = """
            <html>
              <body style="margin:0;padding:0;">
                <iframe 
                    width="100%" 
                    height="100%" 
                    frameborder="0" 
                    style="border:0"
                    src="https://www.google.com/maps?q=$latitude,$longitude&z=16&output=embed"
                    allowfullscreen>
                </iframe>
              </body>
            </html>
        """.trimIndent()

        webViewMap.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)

        // Klik pada peta kecil → langsung buka Google Maps app
        webViewMap.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($namaBank)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    // ⚙️ Navigasi tombol-tombol
    private fun setupNavigation() {
        // Tombol back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Tombol ke halaman detail lokasi bank sampah (peta besar)
        val btnDetailLokasi = binding.root.findViewById<android.widget.TextView>(R.id.btnDetailLokasi)
        btnDetailLokasi.setOnClickListener {
            startActivity(Intent(this, BankSampahActivity::class.java))
        }

        // Riwayat Penyetoran
        binding.btnRiwayatPenyetoran.setOnClickListener {
            startActivity(Intent(this, RiwayatSetoranActivity::class.java))
        }

        // Harga Sampah
        binding.btnHargaSampah.setOnClickListener {
            startActivity(Intent(this, HargaSampahActivity::class.java))
        }

        // Jadwal Penarikan (belum dibuat)
    }
}
