package com.main.cipta_muri_mobile.ui.donasi

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import com.main.cipta_muri_mobile.R
import java.text.NumberFormat
import java.util.*

class DonasiSampahActivity : AppCompatActivity() {

    private val viewModel: DonasiSampahViewModel by viewModels()

    private lateinit var tvBalance: TextView
    private lateinit var etNominal: EditText
    private lateinit var btn10k: MaterialButton
    private lateinit var btn20k: MaterialButton
    private lateinit var btn30k: MaterialButton
    private lateinit var btn50k: MaterialButton
    private lateinit var btnLanjutkan: MaterialButton
    private lateinit var tvLihatPetunjuk: TextView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donasi_sampah)

        initViews()
        observeData()
        setupListeners()
    }

    private fun initViews() {
        tvBalance = findViewById(R.id.tv_balance)
        etNominal = findViewById(R.id.etNominalDonasi)
        btn10k = findViewById(R.id.btn_amount_10k)
        btn20k = findViewById(R.id.btn_amount_20k)
        btn30k = findViewById(R.id.btn_amount_30k)
        btn50k = findViewById(R.id.btn_amount_50k)
        btnLanjutkan = findViewById(R.id.btnLanjutkan)
        tvLihatPetunjuk = findViewById(R.id.tvLihatPetunjuk)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun observeData() {
        viewModel.saldo.observe(this) {
            tvBalance.text = formatRupiah(it)
        }

        viewModel.nominalDonasi.observe(this) {
            if (it > 0) {
                etNominal.setText(formatRupiah(it))
            } else if (it == 0.0) {
                etNominal.setText("")
            }
        }
    }

    private fun setupListeners() {
        btn10k.setOnClickListener { viewModel.pilihNominal(10000.0) }
        btn20k.setOnClickListener { viewModel.pilihNominal(20000.0) }
        btn30k.setOnClickListener { viewModel.pilihNominal(30000.0) }
        btn50k.setOnClickListener { viewModel.pilihNominal(50000.0) }

        etNominal.doOnTextChanged { text, _, _, _ ->
            viewModel.ubahNominalManual(text.toString())
        }

        btnLanjutkan.setOnClickListener {
            if (viewModel.nominalDonasi.value == 0.0) {
                Toast.makeText(this, "Masukkan nominal terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!viewModel.cekSaldoCukup()) {
                Toast.makeText(this, "Saldo tidak mencukupi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Donasi sebesar ${formatRupiah(viewModel.nominalDonasi.value ?: 0.0)} diproses",
                Toast.LENGTH_SHORT
            ).show()

            // TODO: Navigasi ke halaman konfirmasi donasi
        }

        tvLihatPetunjuk.setOnClickListener {
            Toast.makeText(this, "Menampilkan petunjuk donasi...", Toast.LENGTH_SHORT).show()
            // TODO: Buka activity petunjuk
        }

        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(amount).replace(",00", "")
    }
}
