package com.main.cipta_muri_mobile.ui.setor.bank

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.ui.aktivitas.RiwayatAktivitasActivity
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.profile.ProfileActivity
import kotlin.math.*

class BankSampahActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var tvJarak: TextView
    private lateinit var btnBagikan: TextView
    private lateinit var btnBack: ImageView

    private val LATITUDE_BANK = -7.449258
    private val LONGITUDE_BANK = 109.363158
    private val NAMA_BANK = "Bank Sampah Sahabatku"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_sampah)

        webView = findViewById(R.id.webViewMap)
        tvJarak = findViewById(R.id.tvJarak)
        btnBagikan = findViewById(R.id.btnBagikan)
        btnBack = findViewById(R.id.btnBack)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Gunakan iframe agar tidak error “must be used in iframe”
        val html = """
            <html>
              <body style="margin:0;padding:0;">
                <iframe 
                    width="100%" 
                    height="100%" 
                    frameborder="0" 
                    style="border:0"
                    src="https://www.google.com/maps?q=$LATITUDE_BANK,$LONGITUDE_BANK&z=17&output=embed" 
                    allowfullscreen>
                </iframe>
              </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)

        // Klik peta → buka aplikasi Google Maps
        webView.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:$LATITUDE_BANK,$LONGITUDE_BANK?q=$LATITUDE_BANK,$LONGITUDE_BANK($NAMA_BANK)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        // Klik tombol Bagikan → kirim tautan lokasi
        btnBagikan.setOnClickListener {
            val mapLink = "https://www.google.com/maps?q=$LATITUDE_BANK,$LONGITUDE_BANK"
            val shareText = "📍 $NAMA_BANK\nLihat lokasi di Google Maps:\n$mapLink"

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, NAMA_BANK)
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            startActivity(Intent.createChooser(shareIntent, "Bagikan lokasi via..."))
        }

        // 🧭 Tombol Back → kembali ke halaman sebelumnya
        btnBack.setOnClickListener {
            finish() // menutup activity dan kembali ke halaman sebelumnya
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // animasi halus
        }

        // Hitung jarak dari lokasi pengguna
        checkAndGetLocation()

        // Bottom Navigation
        val navView = findViewById<BottomNavigationView?>(R.id.bottom_navigation_view)
        if (navView != null) { setupBottomNavigation(navView) }
    }

    // 🧭 Hitung jarak dari lokasi pengguna
    private fun checkAndGetLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            val distance = calculateDistance(
                location.latitude,
                location.longitude,
                LATITUDE_BANK,
                LONGITUDE_BANK
            )
            tvJarak.text = "Jarak dari lokasimu: ${String.format("%.2f", distance)} km"
        } else {
            tvJarak.text = "Tidak dapat mendeteksi lokasi kamu."
        }
    }

    // 📏 Rumus Haversine untuk hitung jarak (km)
    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val R = 6371 // Radius bumi (km)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    // ⚙️ Bottom Navigation
    private fun setupBottomNavigation(navView: BottomNavigationView) {
        navView.itemIconTintList = null

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_history -> {
                    startActivity(Intent(this, RiwayatAktivitasActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_placeholder -> true
                R.id.navigation_news -> true
                else -> false
            }
        }
    }
}
