package com.example.appcultural.views

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appcultural.R
import com.example.appcultural.data.FirebaseAuthProvider
import com.example.appcultural.databinding.ActivityAccountBinding
import kotlinx.coroutines.launch

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.topAppBar);
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            val authProvider = FirebaseAuthProvider()
            val user = authProvider.getCurrentUser()
            binding.textView8.text = "Nome: " + user.name
            binding.textView11.text = "Id: " + user.id
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.home -> {
            finish()
            true
        }

        else -> {
            finish()
            true
        }
    }
}