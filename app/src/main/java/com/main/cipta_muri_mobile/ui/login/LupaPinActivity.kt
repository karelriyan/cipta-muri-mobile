package com.main.cipta_muri_mobile.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.databinding.ActivityLupaPinBinding
import com.main.cipta_muri_mobile.ui.login.LoginActivity
import com.main.cipta_muri_mobile.ui.login.LupaPinViewModel

class LupaPinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLupaPinBinding
    private val viewModel: LupaPinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLupaPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvPetunjuk.text = getString(com.main.cipta_muri_mobile.R.string.petunjuk_login)

        binding.tvKembali.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
