package com.example.appcultural.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appcultural.R
import com.example.appcultural.data.FirebaseArtsRepository
import com.example.appcultural.databinding.ActivitySaveArtBinding
import com.example.appcultural.entities.Art
import com.example.appcultural.entities.ArtLocation
import kotlinx.coroutines.launch

class SaveArtActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveArtBinding
    private val location = ArtLocation()
    private lateinit var art: Art

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val data: Intent = result.data!!
            val x = data.getFloatExtra("targetX", 0f)
            val y = data.getFloatExtra("targetY", 0f)
            location.x = x
            location.y = y
        }
    }

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

        val artId = intent.getStringExtra("id")
        val repository = FirebaseArtsRepository()
        lifecycleScope.launch {
            if (artId != null) {
                val item = repository.findById(artId)
                if (item != null) {
                    art = item
                    updateForm(art)
                }
            }
        }

        binding.artLocationField.setOnClickListener {
            startForResult.launch(Intent(this, SaveArtLocationActivity::class.java))
        }
        binding.saveArtButton.setOnClickListener {
            try {
                val name = binding.artTitleField.text.toString()
                val publishDate = binding.artPublishField.text.toString().toInt()
                val description = binding.artDescriptionField.text.toString()
                val author = binding.artArtistField.text.toString()
                val isActive = binding.artIsActiveField.isChecked
                val gender = listOf(binding.artGendersField.text.toString())
                val imageUri = binding.artImageUrlField.text.toString()
                val item = Art("", name, publishDate, description, author, isActive, imageUri, gender, location)
                lifecycleScope.launch {
                    if (artId == "") {
                        repository.add(item)
                    } else {
                        item.id = art.id
                        repository.update(item)
                    }
                    finish()
                }
            } catch (err: Exception) {
                Toast.makeText(this, "Erro ao salvar a arte: " + err.message, Toast.LENGTH_SHORT).show()
            }
        }

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateForm(item: Art) {
        binding.artImageUrlField.setText(item.imageUrl)
        binding.artTitleField.setText(item.name)
        binding.artPublishField.setText(item.publishDate.toString())
        binding.artDescriptionField.setText(item.description)
        binding.artIsActiveField.isActivated = item.isActive
        binding.artGendersField.setText(item.genders[0])
        binding.artArtistField.setText(item.author)
        location.x = art.location.x
        location.y = art.location.y
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
