package com.main.cipta_muri_mobile.util

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale

object Formatters {
    private val localeID = Locale("id", "ID")

    fun formatRupiah(amountRaw: String?, isCredit: Boolean?): String {
        val sign = when (isCredit) {
            null -> ""
            true -> "+"
            false -> "-"
        }
        return formatRupiah(amountRaw, sign)
    }

    fun formatRupiah(amountRaw: String?, sign: String = ""): String {
        val amount = parseToBigDecimal(amountRaw).abs()
        val symbols = DecimalFormatSymbols(localeID).apply {
            currencySymbol = "Rp"
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val df = DecimalFormat("#,##0", symbols)
        val value = df.format(amount)
        return "${sign}Rp $value"
    }

    fun formatRupiah(amount: Number): String = formatRupiah(amount.toString(), "")

    private fun parseToBigDecimal(input: String?): BigDecimal {
        if (input.isNullOrBlank()) return BigDecimal.ZERO
        val s = input.trim()

        fun isThousandGrouping(str: String, sep: Char): Boolean {
            val parts = str.split(sep)
            if (parts.size <= 1) return false
            // First part can be 1-3 digits (may include sign)
            val first = parts.first().replace("-", "")
            if (first.isEmpty() || first.length > 3 || !first.all { it.isDigit() }) return false
            // All middle parts should be exactly 3 digits
            for (i in 1 until parts.size) {
                val p = parts[i]
                if (i == parts.lastIndex) {
                    // last part could be 3 digits (thousand sep) OR decimals (1-2 digits)
                    // We'll decide outside; here only validate thousand style
                    if (p.length != 3 || !p.all { it.isDigit() }) return false
                } else if (p.length != 3 || !p.all { it.isDigit() }) return false
            }
            return true
        }

        return try {
            val hasComma = s.contains(',')
            val hasDot = s.contains('.')
            val negative = s.startsWith('-')
            var cleaned = s
            when {
                hasComma && hasDot -> {
                    // Decide by last separator position: e.g. "1,234.56" vs "1.234,56"
                    if (s.lastIndexOf('.') > s.lastIndexOf(',')) {
                        // US style: comma thousand, dot decimal
                        cleaned = s.replace(",", "")
                    } else {
                        // Euro style: dot thousand, comma decimal
                        cleaned = s.replace(".", "").replace(',', '.')
                    }
                }
                hasComma -> {
                    // If looks like thousand grouping, remove commas; else treat as decimal
                    cleaned = if (isThousandGrouping(s.replace("-", ""), ',')) s.replace(",", "") else s.replace(',', '.')
                }
                hasDot -> {
                    cleaned = if (isThousandGrouping(s.replace("-", ""), '.')) s.replace(".", "") else s
                }
            }
            // Remove currency symbols/spaces
            cleaned = cleaned.replace(Regex("[^0-9.-]"), "")
            // Ensure only the first '-' kept (at start)
            if (negative && !cleaned.startsWith('-')) cleaned = "-$cleaned"
            cleaned.toBigDecimalOrNull() ?: BigDecimal.ZERO
        } catch (_: Exception) {
            // Fallback: digits only
            val fallback = s.replace(Regex("[^0-9-]"), "")
            fallback.toBigDecimalOrNull() ?: BigDecimal.ZERO
        }
    }

    fun formatTanggalIndo(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return ""
        val outFmt = SimpleDateFormat("dd MMMM yyyy", localeID)
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (p in patterns) {
            try {
                val inFmt = SimpleDateFormat(p, Locale.US)
                val date = inFmt.parse(dateStr)
                if (date != null) return outFmt.format(date)
            } catch (_: Exception) { /* try next */ }
        }
        return dateStr
    }

    fun formatWaktu(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return ""
        val outFmt = SimpleDateFormat("HH:mm", localeID)
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (p in patterns) {
            try {
                val inFmt = SimpleDateFormat(p, Locale.US)
                val date = inFmt.parse(dateStr)
                if (date != null) return outFmt.format(date)
            } catch (_: Exception) { /* try next */ }
        }
        return ""
    }

    fun formatTanggalWaktu(dateStr: String?): String {
        val tgl = formatTanggalIndo(dateStr)
        val jam = formatWaktu(dateStr)
        if (tgl.isEmpty()) return jam
        if (jam.isEmpty()) return tgl
        return "$tgl • $jam"
    }
}
