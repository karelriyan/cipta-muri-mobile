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

class LoginActivity : AppCompatActivity() {

    // 1. DECLARE the binding object and viewModel
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup Session Manager for initial check
        val sessionManager = SessionManager(this)

        // OPTIONAL: Pre-check if the user is already logged in (Recommended for smooth flow)
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

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

            // The login function requires 3 arguments in the callback: isSuccess, message, and user
            viewModel.login(nik, pinTanggalLahir) { isSuccess, message, user ->
                if (isSuccess) {
                    // We assume session is saved in ViewModel, now navigate
                    if (user != null) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
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
}