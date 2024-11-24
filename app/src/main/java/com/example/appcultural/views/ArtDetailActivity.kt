package com.example.appcultural.views

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.util.Locale

import com.example.appcultural.R
import com.example.appcultural.adapters.ArtListAdapter
import com.example.appcultural.data.FirebaseAlbumsRepository
import com.example.appcultural.data.FirebaseArtsRepository
import com.example.appcultural.data.FirebaseAuthProvider
import com.example.appcultural.data.MockAuthProvider
import com.example.appcultural.databinding.ActivityArtDetailBinding
import com.example.appcultural.entities.Art

class ArtDetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityArtDetailBinding
    private var isLiked = false
    private lateinit var art: Art
    private val artsRepository = FirebaseArtsRepository()
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityArtDetailBinding.inflate(layoutInflater)
        setContentView(binding.constraintLayoutMain)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = FirebaseArtsRepository()
        val artId = intent.getStringExtra("id") ?: run {
            finish()
            return
        }

        val context = this
        lifecycleScope.launch {
            val item = repository.findById(artId)
            if (item == null) {
                finish()
                return@launch
            }
            art = item

            updateImage(art.imageUrl)

            var description = art.description
            if (art.description.length > 80) {
                description = art.description.slice(0..80)
            }
            binding.tvArtDescription.text = description

            binding.recyclerRecommended.adapter = ArtListAdapter(context, repository.list())

            val likeButton = binding.btnLike
            likeButton.setOnClickListener {
                isLiked = !isLiked
                updateLikeButton()
                saveLikeStatus(art.id, isLiked)
            }

            loadLikeStatus(art.id)

            val buttonComment = binding.btnComment
            buttonComment.setOnClickListener {
                val intent = Intent(context, CommentsActivity::class.java)
                intent.putExtra("id", art.id)
                startActivity(intent)
            }

            binding.btnLocation.setOnClickListener {
                val currentLocationIntent = Intent(context, CurrentArtLocationActivity::class.java)
                currentLocationIntent.putExtra("currentX", art.location.x)
                currentLocationIntent.putExtra("currentY", art.location.y)
                startActivity(currentLocationIntent)
            }
        }

        setSupportActionBar(binding.toolbarTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        viewManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding.recyclerRecommended.layoutManager = viewManager

        binding.fabEdit.setOnClickListener {
            val editIntent = Intent(this, SaveArtActivity::class.java)
            editIntent.putExtra("id", artId)
            startActivity(editIntent)
        }
        binding.fabPlay.setOnClickListener {
            speakArtDescription()
        }
        val authProvider = MockAuthProvider(this)
        val visibilityState = if (authProvider.isAdmin) View.VISIBLE else View.GONE
        binding.fabEdit.visibility = visibilityState

        binding.btnMore.setOnClickListener {
            binding.tvArtDescription.text = art.description
        }

        binding.fabFavorite.setOnClickListener {
            showSelectDialog(artId)
        }

        tts = TextToSpeech(this, this)
    }

    private fun updateImage(imageUrl: String) {
        val image = binding.imageArtDetail
        Glide.with(this).load(imageUrl).into(image)
        image.requestLayout()
    }

    private fun updateLikeButton() {
        val icon = if (isLiked) {
            R.drawable.ic_favorite_filled
        } else {
            R.drawable.outline_favorite_24
        }
        binding.btnLike.setImageResource(icon)
    }

    private fun saveLikeStatus(artId: String, liked: Boolean) {
        lifecycleScope.launch {
            val user = FirebaseAuthProvider().getCurrentUser()
            if (liked) {
                artsRepository.addLike(artId, user.id)
            } else {
                artsRepository.removeLike(artId, user.id)
            }
        }
    }

    private fun loadLikeStatus(artId: String) {
        lifecycleScope.launch {
            isLiked = artsRepository.hasLike(artId, FirebaseAuthProvider().getCurrentUser().id)
            updateLikeButton()
        }
    }

    private fun speakArtDescription() {
        if (art.description.isNotEmpty()) {
            tts.speak(art.description, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Toast.makeText(this, "Sem descrição disponivel", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("pt", "BR"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    override fun onDestroy() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    private fun showSelectDialog(artId: String) {
        val albumsRepository = FirebaseAlbumsRepository()

        lifecycleScope.launch {
            val albums = albumsRepository.fetchAll()
            val albumNames = albums.map { it.name }.toTypedArray()

            AlertDialog.Builder(this@ArtDetailActivity)
                .setTitle("Selecione um Álbum")
                .setItems(albumNames) { _, which ->
                    val selectedAlbum = albums[which]
                    appendArtToAlbum(selectedAlbum.id, artId)
                }
                .setPositiveButton("Criar Novo Álbum") { _, _ ->
                    showCreateDialog(artId)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun showCreateDialog(artId: String) {
        val albumsRepository = FirebaseAlbumsRepository()
        val input = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("Nome do Novo Álbum")
            .setView(input)
            .setPositiveButton("Criar") { _, _ ->
                val albumName = input.text.toString()
                if (albumName.isNotEmpty()) {
                    lifecycleScope.launch {
                        try {
                            val newAlbum = albumsRepository.create(albumName)
                            appendArtToAlbum(newAlbum.id, artId)
                        } catch (e: Exception) {
                            Toast.makeText(this@ArtDetailActivity, "Erro ao criar álbum", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "O nome do álbum não pode ser vazio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun appendArtToAlbum(albumId: String, artId: String) {
        lifecycleScope.launch {
            try {
                FirebaseAlbumsRepository().appendArt(albumId, artId)
                Toast.makeText(this@ArtDetailActivity, "Arte adicionada ao álbum", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ArtDetailActivity, "Erro ao adicionar arte ao álbum", Toast.LENGTH_SHORT).show()
            }
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
