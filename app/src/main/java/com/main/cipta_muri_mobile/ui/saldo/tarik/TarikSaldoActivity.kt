package com.main.cipta_muri_mobile.ui.saldo.tarik


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.TextView
import androidx.activity.viewModels
import com.main.cipta_muri_mobile.databinding.ActivityTarikSaldoBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity

class TarikSaldoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTarikSaldoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTarikSaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        // Back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}
