package com.example.appcultural.views

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcultural.R
import com.example.appcultural.databinding.ActivitySaveArtBinding

class SaveArtActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveArtBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySaveArtBinding.inflate(layoutInflater)
        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val genders = listOf(
            "Impressionismo",
            "Expressionismo",
            "Cubismo",
            "Surrealismo",
            "Barroco",
            "Rococó",
            "Renascimento",
            "Neoclassicismo",
            "Romantismo",
            "Realismo",
            "Futurismo",
            "Dadaísmo",
            "Pop Art",
            "Minimalismo",
            "Arte Abstrata"
        )
        val genderTextView = binding.artGendersField
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)
        genderTextView.setAdapter(genderAdapter)

        val artists = listOf(
            "Leonardo da Vinci",
            "Vincent van Gogh",
            "Pablo Picasso",
            "Claude Monet",
            "Michelangelo",
            "Rembrandt",
            "Salvador Dalí",
            "Frida Kahlo",
            "Jackson Pollock",
            "Georgia O'Keeffe",
            "Henri Matisse",
            "Edvard Munch",
            "Paul Cézanne",
            "Gustav Klimt",
            "Andy Warhol"
        )
        val artistTextView = binding.artArtistField
        val artistAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, artists)
        artistTextView.setAdapter(artistAdapter)

        binding.artLocationField.setOnClickListener {
            startActivity(Intent(this, SaveArtLocationActivity::class.java))
        }
        binding.saveArtButton.setOnClickListener {
            finish()
        }

        setSupportActionBar(binding.topAppBar);
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
}