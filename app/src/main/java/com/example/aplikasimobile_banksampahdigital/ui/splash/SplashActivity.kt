package com.example.aplikasimobile_banksampahdigital.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.aplikasimobile_banksampahdigital.R
import com.example.aplikasimobile_banksampahdigital.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel.startSplashTimer()

        viewModel.navigateToMain.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        })
    }
}
