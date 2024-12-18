package com.example.appcultural.views

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch

import com.example.appcultural.R
import com.example.appcultural.adapters.ScheduleListAdapter
import com.example.appcultural.data.FirebaseSchedulesRepository
import com.example.appcultural.databinding.ActivityScheduleListBinding

class ScheduleListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScheduleListBinding.inflate(layoutInflater)
        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = FirebaseSchedulesRepository()
        lifecycleScope.launch {
            val data = repository.list()
            val adapter = ScheduleListAdapter(data)
            binding.recycleView.adapter = adapter
        }
        binding.recycleView.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(binding.topAppBar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
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
