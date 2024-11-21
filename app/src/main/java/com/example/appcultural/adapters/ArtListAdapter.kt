package com.example.appcultural.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcultural.R
import com.example.appcultural.entities.Art
import com.example.appcultural.views.ArtDetailActivity

class ArtListAdapter(private val context: Context, private val data: List<Art>): RecyclerView.Adapter<ArtListAdapter.ArtViewHolder>() {
    class ArtViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.art_item, parent, false)

        return ArtViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        val content = data[position]

        val image = holder.itemView.findViewById<ImageView>(R.id.art_image)
        Glide.with(context).load(content.imageUrl).into(image)
        image.requestLayout()

        image.setOnClickListener {
            val intent = Intent(context, ArtDetailActivity::class.java)
            intent.putExtra("id", content.id)
            intent.putExtra("url", content.imageUrl)
            context.startActivity(intent)
        }
    }
}