package com.main.cipta_muri_mobile.ui.setor

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.main.cipta_muri_mobile.R

class SetorSampahActivity : AppCompatActivity() {

    private val viewModel: SetorSampahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setor_sampah)
    }
}
