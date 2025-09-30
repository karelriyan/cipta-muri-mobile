package com.example.aplikasimobile_banksampahdigital.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasimobile_banksampahdigital.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnMasuk.setOnClickListener {
            val nik = binding.etNik.text.toString()
            val pin = binding.etPin.text.toString()

            if (viewModel.login(nik, pin)) {
                // TODO: Arahkan ke halaman berikutnya
            } else {
                binding.etPin.error = "NIK atau PIN salah"
            }
        }
    }
}
