package com.example.appcultural.views

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appcultural.data.FirebaseAuthProvider
import com.example.appcultural.databinding.ActivityLoginEmployeeBinding
import kotlinx.coroutines.launch

class LoginEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginEmployeeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.main)

        binding.loginEmployeeButton.setOnClickListener {
            login()
        }
        binding.loginEmployeePassword.editText?.setOnEditorActionListener { v, actionId, event ->  run {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
            }
            false
        }}

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val intent = Intent(this, BottomActivity::class.java)
        lifecycleScope.launch {
            try {
                val authProvider = FirebaseAuthProvider()
                authProvider.login(binding.editTextTextEmail.editText?.text.toString() + "@empresa.com", binding.loginEmployeePassword.editText?.text.toString())
                val isAdmin = authProvider.getAdmin(context = this@LoginEmployeeActivity)
                if (!isAdmin) throw Exception("Usuário não é administrador")
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(baseContext, "Autenticação falhou: " + e.message,
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}