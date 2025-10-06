package com.main.cipta_muri_mobile.ui.saldo.tarik

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.databinding.ActivityTarikSaldoBinding
import com.main.cipta_muri_mobile.ui.saldo.konfirmasi.KonfirmasiPenarikanActivity
import com.main.cipta_muri_mobile.ui.saldo.petunjuk.PetunjukPenarikanActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class TarikSaldoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTarikSaldoBinding
    private var isEditing = false

    companion object {
        const val COLOR_SELECTED_BG = "#6366F1"
        const val COLOR_SELECTED_TEXT = "#FFFFFF"
        const val COLOR_DEFAULT_TEXT = "#64748B"
        const val COLOR_DEFAULT_BORDER = "#E2E8F0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTarikSaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNominalButtons()
        setupEditText()
        setupButtonActions()
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // 🔹 Navigasi tombol Lihat Petunjuk & Lanjutkan
    private fun setupButtonActions() {
        binding.tvSeeInstructions.setOnClickListener {
            val intent = Intent(this, PetunjukPenarikanActivity::class.java)
            startActivity(intent)
        }

        binding.btnContinue.setOnClickListener {
            val intent = Intent(this, KonfirmasiPenarikanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupEditText() {
        val etAmount = binding.etAmount

        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isEditing) return
                isEditing = true

                try {
                    var text = editable.toString()
                        .replace("Rp", "")
                        .replace(".", "")
                        .replace(" ", "")
                        .trim()

                    if (text.isNotEmpty()) {
                        val number = text.toLongOrNull() ?: 0L
                        val formatter = DecimalFormat("#,###", DecimalFormatSymbols().apply {
                            groupingSeparator = '.'
                        })
                        val formatted = "Rp ${formatter.format(number)}"
                        etAmount.setText(formatted)
                        etAmount.setSelection(formatted.length)
                    } else {
                        etAmount.setText("Rp ")
                        etAmount.setSelection(etAmount.text.length)
                    }
                } catch (_: Exception) {
                    etAmount.setText("Rp ")
                    etAmount.setSelection(etAmount.text.length)
                }

                isEditing = false
            }
        })
    }

    private fun setupNominalButtons() {
        val buttons = listOf(
            binding.btnAmount10k,
            binding.btnAmount20k,
            binding.btnAmount30k,
            binding.btnAmount40k,
            binding.btnAmount50k,
            binding.btnAmount100k,
            binding.btnAmount150k,
            binding.btnAmount200k
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                // Reset semua tombol jadi tidak dipilih
                buttons.forEach { it.isSelected = false }
                button.isSelected = true
                updateButtonStyles(buttons)

                // Ambil nominal dari text tombol dan bersihkan format
                var nominal = button.text.toString()
                    .replace("Rp", "")
                    .replace(" ", "")
                    .trim()

                // Ambil hanya bagian sebelum koma (kalau ada)
                if (nominal.contains(",")) {
                    nominal = nominal.substringBefore(",")
                }

                // Hilangkan titik ribuan (.)
                nominal = nominal.replace(".", "")

                // Parsing jadi angka
                val number = nominal.toLongOrNull() ?: 0L

                // Format ulang ke tampilan yang rapi
                val formatter = DecimalFormat("#,###", DecimalFormatSymbols().apply {
                    groupingSeparator = '.'
                })
                val formatted = "Rp ${formatter.format(number)}"

                // Tampilkan ke EditText
                binding.etAmount.setText(formatted)
                binding.etAmount.setSelection(binding.etAmount.text.length)
            }
        }
    }



    private fun updateButtonStyles(buttons: List<MaterialButton>) {
        buttons.forEach { button ->
            if (button.isSelected) {
                button.setBackgroundColor(Color.parseColor(COLOR_SELECTED_BG))
                button.setTextColor(Color.parseColor(COLOR_SELECTED_TEXT))
                button.strokeWidth = 0
                button.elevation = 6f
                button.translationZ = 2f
            } else {
                button.setBackgroundColor(Color.WHITE)
                button.setTextColor(Color.parseColor(COLOR_DEFAULT_TEXT))
                button.strokeWidth = 2
                button.strokeColor = ContextCompat.getColorStateList(this, android.R.color.transparent)
                button.elevation = 0f
                button.translationZ = 0f
                button.setBackgroundResource(R.drawable.bg_amount_button)
            }
        }
    }
}
