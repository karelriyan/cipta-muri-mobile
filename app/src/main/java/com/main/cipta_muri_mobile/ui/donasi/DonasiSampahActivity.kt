package com.main.cipta_muri_mobile.ui.donasi

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.databinding.ActivityDonasiSampahBinding
import com.google.android.material.button.MaterialButton
import com.main.cipta_muri_mobile.R
import java.text.NumberFormat
import java.util.*

class DonasiSampahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonasiSampahBinding
    private var selectedButton: MaterialButton? = null
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonasiSampahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupNominalButtons()
        setupNominalInput()
    }

    private fun setupNavigation() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupNominalButtons() {
        val buttons = listOf(
            binding.btnAmount10k to 10000.0,
            binding.btnAmount20k to 20000.0,
            binding.btnAmount30k to 30000.0,
            binding.btnAmount50k to 50000.0
        )

        buttons.forEach { (button, amount) ->
            button.setOnClickListener {
                // Reset semua tombol
                selectedButton?.isSelected = false
                selectedButton?.setTextColor(getColor(R.color.gray))

                // Pilih tombol ini
                button.isSelected = true
                button.setTextColor(getColor(android.R.color.white))
                selectedButton = button

                // Update EditText
                val formatted = NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)
                binding.etNominalDonasi.setText("Rp $formatted")
                binding.etNominalDonasi.setSelection(binding.etNominalDonasi.text.length)
            }
        }
    }

    private fun setupNominalInput() {
        binding.etNominalDonasi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                val text = s.toString().replace("[^0-9]".toRegex(), "")
                val value = text.toDoubleOrNull() ?: 0.0

                val formatted = if (value > 0)
                    "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(value)
                else
                    "Rp"

                binding.etNominalDonasi.setText(formatted)
                binding.etNominalDonasi.setSelection(binding.etNominalDonasi.text.length)
                isEditing = false
            }
        })
    }
}
