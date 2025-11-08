package com.main.cipta_muri_mobile.ui.mutasi

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.databinding.ActivityMutasiSaldoBinding
import com.main.cipta_muri_mobile.data.ApiRepository
import kotlinx.coroutines.launch
import com.main.cipta_muri_mobile.util.Formatters

class MutasiSaldoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMutasiSaldoBinding
    private val viewModel: MutasiSaldoViewModel by viewModels()
    private lateinit var adapter: MutasiSaldoAdapter
    private var isBottomRefreshing: Boolean = false
    private var isLoading: Boolean = false

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

        // Scroll listener untuk refresh saat mentok bawah
        setupScrollListener()

        // Muat awal
        refreshData()
        updateSaldoHeader()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
        updateSaldoHeader()
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

        viewModel.isLoading.observe(this) { /* no-op: using local isLoading */ }
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

    private fun setupScrollListener() {
        binding.nestedScroll.setOnScrollChangeListener(
            androidx.core.widget.NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                val nested = v as? androidx.core.widget.NestedScrollView ?: return@OnScrollChangeListener
                val contentHeight = nested.getChildAt(0)?.measuredHeight ?: return@OnScrollChangeListener
                val containerHeight = nested.measuredHeight
                val atBottom = scrollY >= (contentHeight - containerHeight)
                if (atBottom && !isLoading && !isBottomRefreshing) {
                    isBottomRefreshing = true
                    refreshData()
                }
            }
        )
    }

    private fun refreshData() {
        lifecycleScope.launch {
            isLoading = true
            try {
                val repo = ApiRepository(this@MutasiSaldoActivity)
                val result = repo.getSaldoTransactions(null)
                result.onSuccess { list ->
                    val mapped = list.map { tx ->
                        val isCredit = tx.type.equals("credit", ignoreCase = true)
                        val nominalDisp = Formatters.formatRupiah(tx.amount, isCredit)
                        MutasiSaldoItem(
                            tanggal = Formatters.formatTanggalWaktu(tx.createdAt),
                            judul = if (isCredit) "Saldo Masuk" else "Saldo Keluar",
                            keterangan = tx.description ?: "",
                            nominal = nominalDisp,
                            warnaNominal = if (isCredit) getColor(R.color.green) else getColor(R.color.red)
                        )
                    }
                    adapter.updateData(mapped)
                }.onFailure {
                    loadDummyData()
                }
            } finally {
                isLoading = false
                isBottomRefreshing = false
            }
        }
    }

    private fun updateSaldoHeader() {
        lifecycleScope.launch {
            val session = SessionManager(this@MutasiSaldoActivity)
            val localBalance = session.getUserBalance()
            // Tampilkan sementara dari session
            binding.tvSaldoTotal.text = Formatters.formatRupiah(localBalance)

            // Coba ambil saldo terbaru dari API
            runCatching {
                val repo = ApiRepository(this@MutasiSaldoActivity)
                repo.getRekening()
            }.onSuccess { result ->
                result.onSuccess { summary ->
                    summary?.let {
                        val balStr = it.balance ?: it.rekening.balance
                        val formatted = Formatters.formatRupiah(balStr)
                        binding.tvSaldoTotal.text = formatted
                    }
                }
            }
        }
    }
}
