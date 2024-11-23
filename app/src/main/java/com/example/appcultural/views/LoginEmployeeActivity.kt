package com.example.appcultural.views

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appcultural.R
import com.example.appcultural.data.FirebaseAuthProvider
import com.example.appcultural.data.MockAuthProvider
import com.example.appcultural.databinding.ActivityLoginEmployeeBinding
import kotlinx.coroutines.launch

class LoginEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginEmployeeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.main)
        val authProvider = FirebaseAuthProvider()

        val intent = Intent(this, BottomActivity::class.java)
        binding.loginEmployeeButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    authProvider.login(binding.editTextTextEmail.editText?.text.toString(), binding.loginEmployeePassword.editText?.text.toString())
                    authProvider.getAdmin(context = this@LoginEmployeeActivity)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(baseContext, "Autenticação falhou: " + e.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}