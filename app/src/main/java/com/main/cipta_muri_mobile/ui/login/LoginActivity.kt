package com.main.cipta_muri_mobile.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.databinding.ActivityLoginBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.data.User
// Import SessionManager for the initial check (recommended)
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.ui.registrasi.RegistrasiActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // 1. DECLARE the binding object and viewModel
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var isFormattingDate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Jangan auto-forward ke Main; selalu tampilkan form login.

        // 2. INITIALIZE the binding object and set content view
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDateInputFormat()
        setupListeners()
    }

    private fun setupDateInputFormat() {
        binding.etPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormattingDate) return

                val digits = s?.filter { it.isDigit() }?.take(8) ?: ""
                val formatted = StringBuilder()

                digits.forEachIndexed { index, c ->
                    formatted.append(c)
                    val shouldInsertDaySeparator = index == 1 && digits.length > 2
                    val shouldInsertMonthSeparator = index == 3 && digits.length > 4
                    if (shouldInsertDaySeparator || shouldInsertMonthSeparator) {
                        formatted.append('-')
                    }
                }

                val displayText = formatted.toString()

                isFormattingDate = true
                binding.etPin.setText(displayText)
                binding.etPin.setSelection(displayText.length)
                isFormattingDate = false
            }
        })
    }

    private fun setupListeners() {
        binding.btnMasuk.setOnClickListener {
            val nik = binding.etNik.text.toString().trim()
            val pinTanggalLahir = binding.etPin.text.toString().trim()

            if (nik.isEmpty() || pinTanggalLahir.isEmpty()) {
                Toast.makeText(this, "NIK dan Tanggal Lahir wajib diisi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.etPin.error = null

            // Prefer API v2 (Bearer token) using tanggal_lahir as YYYY-MM-DD
            val normalizedTtl = normalizeDate(pinTanggalLahir)
            viewModel.loginV2(nik, normalizedTtl) { ok, msg ->
                if (ok) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    val message = msg ?: "Login gagal"
                    binding.etPin.error = message
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvLupaPin.setOnClickListener {
            startActivity(Intent(this, RegistrasiActivity::class.java))
        }
    }

    private fun normalizeDate(input: String): String {
        // Accept already-correct format
        val trimmed = input.trim()
        // Remove unexpected trailing chars (e.g., accidental 'S')
        val sanitized = trimmed.filter { it.isDigit() || it == '-' || it == '/' || it == '.' }
        val backendRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        if (backendRegex.matches(sanitized)) return sanitized

        val displayRegex = Regex("^\\d{2}-\\d{2}-\\d{4}$")
        if (displayRegex.matches(sanitized)) {
            val parts = sanitized.split("-")
            return "${parts[2]}-${parts[1]}-${parts[0]}"
        }

        // Try dd-MM-yyyy or dd/MM/yyyy or ddMMyyyy
        val digits = sanitized.replace("/", "-").replace(".", "-")
        val parts = digits.split("-")
        if (parts.size == 3 && parts[0].length == 2 && parts[1].length == 2 && parts[2].length == 4) {
            return "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}"
        }
        if (sanitized.length == 8 && sanitized.all { it.isDigit() }) {
            val dd = sanitized.substring(0, 2)
            val mm = sanitized.substring(2, 4)
            val yyyy = sanitized.substring(4, 8)
            return "$yyyy-$mm-$dd"
        }
        // Fallback: return as-is; backend will validate
        return sanitized
    }
}
