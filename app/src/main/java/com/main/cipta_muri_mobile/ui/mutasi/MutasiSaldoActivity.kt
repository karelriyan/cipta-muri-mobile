package com.main.cipta_muri_mobile.ui.mutasi

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.ApiRepository
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.databinding.ActivityMutasiSaldoBinding
import com.main.cipta_muri_mobile.util.Formatters
import kotlinx.coroutines.launch
import kotlin.math.min

class MutasiSaldoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMutasiSaldoBinding
    private val viewModel: MutasiSaldoViewModel by viewModels()
    private lateinit var adapter: MutasiSaldoAdapter
    private var isBottomRefreshing: Boolean = false
    private var isLoading: Boolean = false
    private val initialLimit = 10
    private val loadStep = 5
    private var allItems: List<MutasiSaldoItem> = emptyList()
    private var visibleCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMutasiSaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        observeViewModel()
        setupScrollListener()

        binding.tvTampilkanLebihBanyak.setOnClickListener { loadMore() }

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
            if (!list.isNullOrEmpty()) {
                applyData(convertToItems(list))
            } else {
                applyData(loadDummyData())
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                binding.tvTampilkanLebihBanyak.text = error
            }
        }

        viewModel.isLoading.observe(this) { /* handled by local flag */ }
    }

    private fun convertToItems(list: List<com.main.cipta_muri_mobile.data.MutasiSaldo>): List<MutasiSaldoItem> {
        return list.map { data ->
            val nominalStr = data.nominal.toString()
            MutasiSaldoItem(
                tanggal = data.tanggal,
                judul = if (nominalStr.startsWith("+")) "Saldo Masuk" else "Saldo Keluar",
                keterangan = data.keterangan ?: "",
                nominal = if (nominalStr.startsWith("+") || nominalStr.startsWith("-"))
                    "Rp ${nominalStr.replace("+", "").replace("-", "")}"
                else
                    "Rp $nominalStr",
                warnaNominal = if (nominalStr.startsWith("+")) getColor(R.color.green) else getColor(R.color.red)
            )
        }
    }

    private fun loadDummyData(): List<MutasiSaldoItem> {
        return listOf(
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
                    if (allItems.size > visibleCount) {
                        loadMore()
                        isBottomRefreshing = false
                    } else {
                        refreshData()
                    }
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
                    applyData(mapped)
                }.onFailure {
                    applyData(loadDummyData())
                }
            } finally {
                isLoading = false
                isBottomRefreshing = false
            }
        }
    }

    private fun applyData(items: List<MutasiSaldoItem>) {
        allItems = items
        visibleCount = min(initialLimit, allItems.size)
        renderVisible()
    }

    private fun loadMore() {
        if (allItems.isEmpty()) return
        val newCount = min(allItems.size, visibleCount + loadStep)
        if (newCount != visibleCount) {
            visibleCount = newCount
            renderVisible()
        }
    }

    private fun renderVisible() {
        val visible = allItems.take(visibleCount)
        adapter.updateData(visible)
        val hasMore = allItems.size > visibleCount
        binding.tvTampilkanLebihBanyak.isVisible = allItems.isNotEmpty()
        binding.tvTampilkanLebihBanyak.text = when {
            allItems.isEmpty() -> "Belum ada data"
            hasMore -> "Tampilkan Lebih Banyak"
            else -> "Tidak ada data lagi"
        }
    }

    private fun updateSaldoHeader() {
        lifecycleScope.launch {
            val session = SessionManager(this@MutasiSaldoActivity)
            val localBalance = session.getUserBalance()
            binding.tvSaldoTotal.text = Formatters.formatRupiah(localBalance)

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
