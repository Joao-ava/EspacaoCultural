package com.example.appcultural.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.appcultural.data.FirebaseAuthProvider
import com.example.appcultural.databinding.ActivitySettingBinding

class SettingActivity : Fragment() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivitySettingBinding.inflate(inflater, container, false)
        return binding.main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonAccount.setOnClickListener {
            startActivity(Intent(requireContext(), AccountActivity::class.java))
        }

        binding.buttonAddArt.setOnClickListener {
            startActivity(Intent(requireContext(), SaveArtActivity::class.java))
        }
        val isAdmin = FirebaseAuthProvider().isAdmin(requireContext())
        binding.buttonAddArt.visibility = if (isAdmin) View.VISIBLE else View.GONE

        binding.buttonSchedule.setOnClickListener {
            startActivity(Intent(requireContext(), ScheduleListActivity::class.java))
        }
    }
}