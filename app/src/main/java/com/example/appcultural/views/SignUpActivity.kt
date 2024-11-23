package com.example.appcultural.views

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appcultural.data.FirebaseAuthProvider
import com.example.appcultural.databinding.ActivitySignupEmployeeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupEmployeeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura o view binding
        binding = ActivitySignupEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.main)

        // Inicializa o FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configura o botão de cancelamento
        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.textInputLayoutConfirmPasswordEdit.setOnEditorActionListener { _, actionId, _ ->  run {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val name = binding.textInputLayoutName.editText?.text.toString()
                val registration = binding.textInputLayoutRegistration.editText?.text.toString()
                val password = binding.textInputLayoutPassword.editText?.text.toString()
                val confirmPassword = binding.textInputLayoutConfirmPassword.editText?.text.toString()

                if (password == confirmPassword) {
                    registerEmployee(name, registration, password)
                } else {
                    Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                }
            }
            false
        }}

        // Configura o botão de envio do cadastro
        binding.buttonSubmit.setOnClickListener {
            val name = binding.textInputLayoutName.editText?.text.toString()
            val registration = binding.textInputLayoutRegistration.editText?.text.toString()
            val password = binding.textInputLayoutPassword.editText?.text.toString()
            val confirmPassword = binding.textInputLayoutConfirmPassword.editText?.text.toString()

            if (password == confirmPassword) {
                registerEmployee(name, registration, password)
            } else {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerEmployee(name: String, registration: String, password: String) {
        val authProvider = FirebaseAuthProvider()
        lifecycleScope.launch {
            try {
                authProvider.createEmployee("$registration@empresa.com", password)
                Toast.makeText(
                    this@SignUpActivity,
                    "Cadastro de funcionário realizado com sucesso",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@SignUpActivity, "Falha no cadastro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
