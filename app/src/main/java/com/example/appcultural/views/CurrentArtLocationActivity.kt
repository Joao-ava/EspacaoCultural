package com.example.appcultural.views

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcultural.R
import com.example.appcultural.databinding.ActivityCurrentArtLocationBinding

class CurrentArtLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrentArtLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCurrentArtLocationBinding.inflate(layoutInflater)
        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val currentX = intent.getFloatExtra("currentX", 0f)
        val currentY = intent.getFloatExtra("currentY", 0f)
        val imageView = binding.imageLocation
        val targetX = currentX + imageView.x
        val targetY = currentY + imageView.y
        addCircle(targetX, targetY)

        setSupportActionBar(binding.topAppBar);
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }

    private fun addCircle(x: Float, y: Float) {
        val circleView = CirclePointView(this, x, y)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        addContentView(circleView, layoutParams)
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