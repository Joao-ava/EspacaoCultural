package com.example.appcultural.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.appcultural.R
import com.example.appcultural.databinding.ActivityBottomBinding

class BottomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBottomBinding.inflate(layoutInflater)
        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        replaceFragment(HomeActivity())
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(HomeActivity())
                R.id.albums -> replaceFragment(AlbumsListActivity())
                R.id.schedule -> replaceFragment(ScheduleActivity())
                R.id.settings -> replaceFragment(SettingActivity())
                else -> replaceFragment(HomeActivity())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.frameLayout.id, fragment)
        fragmentTransaction.commit()
    }
}