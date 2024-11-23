package com.example.appcultural.views

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

import com.example.appcultural.R
import com.example.appcultural.adapters.ArtListAdapter
import com.example.appcultural.data.FirebaseArtsRepository
import com.example.appcultural.data.FirebaseAuthProvider
import com.example.appcultural.data.MockAuthProvider
import com.example.appcultural.databinding.ActivityArtDetailBinding
import com.example.appcultural.entities.Art

class ArtDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArtDetailBinding
    private var isLiked = false
    private lateinit var art: Art
    private val artsRepository = FirebaseArtsRepository()

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
        val id = intent.getStringExtra("id")
        if (id == null) {
            finish()
            return
        }

        val context = this
        lifecycleScope.launch {
            val item = repository.findById(id)
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

        val authProvider = MockAuthProvider(this)
        val visibilityState = if (authProvider.isAdmin) View.VISIBLE else View.GONE
        binding.fabEdit.setOnClickListener {
            val editIntent = Intent(this, SaveArtActivity::class.java)
            editIntent.putExtra("id", id)
            startActivity(editIntent)
        }
        binding.fabEdit.visibility = visibilityState

        binding.btnMore.setOnClickListener {
            binding.tvArtDescription.text = art.description
        }
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
