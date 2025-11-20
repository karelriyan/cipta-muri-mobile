package com.main.cipta_muri_mobile.ui.setor.riwayat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.data.ApiRepository
import com.main.cipta_muri_mobile.data.SaldoTransaction
import com.main.cipta_muri_mobile.data.SetoranSampahResponse
import com.main.cipta_muri_mobile.util.Formatters
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class RiwayatSetoranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApiRepository(application.applicationContext)
    private val initialLimit = 10
    private val loadStep = 5
    private var allItems: List<RiwayatSetoran> = emptyList()
    private var visibleCount = 0

    private val _riwayatList = MutableLiveData<List<RiwayatSetoran>>()
    val riwayatList: LiveData<List<RiwayatSetoran>> = _riwayatList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadRiwayatSetoran(userId: String? = null) {
        _isLoading.postValue(true)
        _errorMessage.postValue("")

        viewModelScope.launch {
            val saldoTransaksi = repository.getSaldoTransactions("credit").getOrElse { emptyList() }

            repository.getRiwayatSetoran()
                .onSuccess { items ->
                    allItems = items.map { it.toUiModel(saldoTransaksi) }
                    visibleCount = minOf(initialLimit, allItems.size)
                    _riwayatList.postValue(allItems.take(visibleCount))
                    if (allItems.isEmpty()) {
                        _errorMessage.postValue("Belum ada data penyetoran.")
                    }
                }
                .onFailure { throwable ->
                    allItems = emptyList()
                    visibleCount = 0
                    _riwayatList.postValue(emptyList())
                    _errorMessage.postValue(throwable.message ?: "Gagal memuat riwayat setoran")
                }

            _isLoading.postValue(false)
        }
    }

    fun loadMore() {
        if (allItems.isEmpty()) return
        val newCount = minOf(allItems.size, visibleCount + loadStep)
        if (newCount != visibleCount) {
            visibleCount = newCount
            _riwayatList.postValue(allItems.take(visibleCount))
        }
    }

    fun hasMore(): Boolean = visibleCount < allItems.size

    private fun SetoranSampahResponse.toUiModel(saldoTransaksi: List<SaldoTransaction>): RiwayatSetoran {
        val detailItem = detail?.firstOrNull()
        val jenis = detailItem?.namaSampah
            ?: detailItem?.kategori
            ?: jenisSetoran
            ?: "Setoran Sampah"

        val totalBeratKg = totalBerat.toBigDecimalNormalized()
            ?: sumDetailKg(detail)
        val beratFormatted = formatKg(totalBeratKg)

        val nominalRaw = totalHarga.toBigDecimalNormalized()
            ?: sumDetailHarga(detail)
        val nominalFormatted = findSaldoForSetoran(this, saldoTransaksi, nominalRaw)?.let {
            Formatters.formatRupiah(it.amount, it.type.equals("credit", true))
        } ?: nominalRaw?.let { Formatters.formatRupiah(it.toPlainString()) } ?: "Rp 0"

        val tanggalFormatted = Formatters.formatTanggalIndo(tanggal).ifBlank { tanggal ?: "" }

        return RiwayatSetoran(
            tanggal = tanggalFormatted,
            jenisSetoran = jenis,
            beratFormatted = beratFormatted,
            totalSaldoFormatted = nominalFormatted
        )
    }

    private fun formatKg(value: BigDecimal?): String {
        val safeValue = value ?: BigDecimal.ZERO
        val stripped = safeValue.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros()
        return "${stripped.toPlainString()} Kg"
    }

    private fun sumDetailKg(detail: List<com.main.cipta_muri_mobile.data.SetoranDetailResponse>?): BigDecimal? {
        val values = detail?.mapNotNull { it.jumlahKg.toBigDecimalNormalized() }?.filter { it >= BigDecimal.ZERO }
        if (values.isNullOrEmpty()) return null
        return values.reduce(BigDecimal::add)
    }

    private fun sumDetailHarga(detail: List<com.main.cipta_muri_mobile.data.SetoranDetailResponse>?): BigDecimal? {
        val values = detail?.mapNotNull { it.hargaTotal.toBigDecimalNormalized() }?.filter { it >= BigDecimal.ZERO }
        if (values.isNullOrEmpty()) return null
        return values.reduce(BigDecimal::add)
    }

    private fun findSaldoForSetoran(
        setoran: SetoranSampahResponse,
        saldoTransaksi: List<SaldoTransaction>,
        nominal: BigDecimal?
    ): SaldoTransaction? {
        val creditOnly = saldoTransaksi.filter { it.type.equals("credit", true) }
        if (creditOnly.isEmpty()) return null

        val matchByAmount = nominal?.let { target ->
            creditOnly.firstOrNull { tx -> tx.amount.toBigDecimalNormalized()?.compareTo(target) == 0 }
        }
        if (matchByAmount != null) return matchByAmount

        val setoranId = setoran.id?.trim()
        if (!setoranId.isNullOrEmpty()) {
            creditOnly.firstOrNull { it.description?.contains(setoranId, true) == true }?.let { return it }
        }

        val tanggalPrefix = setoran.tanggal?.take(10)
        if (!tanggalPrefix.isNullOrEmpty()) {
            creditOnly.firstOrNull { it.createdAt?.startsWith(tanggalPrefix) == true }?.let { return it }
        }

        return creditOnly.firstOrNull { it.description?.contains("setor", true) == true }
    }

    private fun String?.toBigDecimalNormalized(): BigDecimal? {
        if (this.isNullOrBlank()) return null
        val sanitized = this.trim()
        val isNegative = sanitized.contains('-')
        val digitsAndSeparators = sanitized.replace(Regex("[^0-9,\\.]"), "")
        if (digitsAndSeparators.isEmpty()) return null

        val lastComma = digitsAndSeparators.lastIndexOf(',')
        val lastDot = digitsAndSeparators.lastIndexOf('.')
        val decimalIndex = maxOf(lastComma, lastDot)
        val decimalLength = if (decimalIndex != -1) digitsAndSeparators.length - decimalIndex - 1 else 0
        val treatAsDecimal = decimalIndex != -1 && decimalLength in 1..2

        val numberString = if (treatAsDecimal) {
            val integerPart = digitsAndSeparators.substring(0, decimalIndex).replace(Regex("[^0-9]"), "")
            val decimalPart = digitsAndSeparators.substring(decimalIndex + 1).replace(Regex("[^0-9]"), "")
            "$integerPart.$decimalPart"
        } else {
            digitsAndSeparators.replace(Regex("[^0-9]"), "")
        }

        val value = numberString.toBigDecimalOrNull() ?: return null
        return if (isNegative) value.negate() else value
    }
}
