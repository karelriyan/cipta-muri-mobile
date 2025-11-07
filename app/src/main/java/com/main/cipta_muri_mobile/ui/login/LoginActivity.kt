package com.main.cipta_muri_mobile.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.databinding.ActivityLoginBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.data.User
// Import SessionManager for the initial check (recommended)
import com.main.cipta_muri_mobile.data.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // 1. DECLARE the binding object and viewModel
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Jangan auto-forward ke Main; selalu tampilkan form login.

        // 2. INITIALIZE the binding object and set content view
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
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
            val intent = Intent(this, LupaPinActivity::class.java)
            startActivity(intent)
        }
    }

    private fun normalizeDate(input: String): String {
        // Accept already-correct format
        val trimmed = input.trim()
        // Remove unexpected trailing chars (e.g., accidental 'S')
        val sanitized = trimmed.filter { it.isDigit() || it == '-' || it == '/' || it == '.' }
        val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        if (regex.matches(sanitized)) return sanitized

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
