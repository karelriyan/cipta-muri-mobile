package com.main.cipta_muri_mobile.ui.mutasi

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.databinding.ActivityMutasiSaldoBinding

class MutasiSaldoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMutasiSaldoBinding
    private val viewModel: MutasiSaldoViewModel by viewModels()
    private lateinit var adapter: MutasiSaldoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMutasiSaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali
        binding.btnBack.setOnClickListener { finish() }

        // Setup RecyclerView
        setupRecyclerView()

        // Observe LiveData dari ViewModel
        observeViewModel()

        // Ambil userId dari SessionManager
        val session = SessionManager(this)
        val userId = session.getUserId()?.toIntOrNull()

        if (userId != null) {
            // 🔹 Load data dari backend
            viewModel.loadMutasiSaldo(userId)
        } else {
            // 🔹 Kalau user belum login / ID tidak ada
            loadDummyData()
        }
    }

    private fun setupRecyclerView() {
        adapter = MutasiSaldoAdapter(emptyList())
        binding.rvMutasiSaldo.layoutManager = LinearLayoutManager(this)
        binding.rvMutasiSaldo.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.mutasiList.observe(this) { list ->
            if (list.isNotEmpty()) {
                // 🔄 Konversi dari data model backend → UI model
                val convertedList = list.map { data ->

                    // Ubah nominal jadi string dulu (jaga-jaga kalau nilainya Int/Double)
                    val nominalStr = data.nominal.toString()

                    MutasiSaldoItem(
                        tanggal = data.tanggal,
                        judul = if (nominalStr.startsWith("+")) "Saldo Masuk" else "Saldo Keluar",
                        keterangan = data.keterangan ?: "",
                        nominal = if (nominalStr.startsWith("+") || nominalStr.startsWith("-"))
                            "Rp ${nominalStr.replace("+", "").replace("-", "")}"
                        else
                            "Rp $nominalStr",
                        warnaNominal = if (nominalStr.startsWith("+"))
                            getColor(R.color.green)
                        else
                            getColor(R.color.red)
                    )
                }

                adapter.updateData(convertedList)
                binding.tvTampilkanLebihBanyak.text = "Tampilkan Lebih Banyak"
            } else {
                loadDummyData()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                binding.tvTampilkanLebihBanyak.text = error
            }
        }
    }


    private fun loadDummyData() {
        val dummyList = listOf(
            MutasiSaldoItem(
                tanggal = "09 Maret 2025",
                judul = "Saldo Masuk",
                keterangan = "Hasil Penjualan\nSampah",
                nominal = "+Rp 100.000,00",
                warnaNominal = getColor(R.color.green)
            ),
            MutasiSaldoItem(
                tanggal = "05 Maret 2025",
                judul = "Saldo Keluar",
                keterangan = "Penarikan ke rekening\nBRI",
                nominal = "-Rp 50.000,00",
                warnaNominal = getColor(R.color.red)
            ),
            MutasiSaldoItem(
                tanggal = "27 Februari 2025",
                judul = "Saldo Masuk",
                keterangan = "Setor Sampah\nPlastik & Kertas",
                nominal = "+Rp 75.000,00",
                warnaNominal = getColor(R.color.green)
            ),
            MutasiSaldoItem(
                tanggal = "20 Februari 2025",
                judul = "Saldo Keluar",
                keterangan = "Tarik Tunai",
                nominal = "-Rp 25.000,00",
                warnaNominal = getColor(R.color.red)
            )
        )

        adapter.updateData(dummyList)
    }
}
