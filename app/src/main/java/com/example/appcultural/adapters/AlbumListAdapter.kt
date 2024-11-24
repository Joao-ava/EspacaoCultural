package com.example.appcultural.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appcultural.R
import com.example.appcultural.entities.Album
import com.example.appcultural.views.AlbumDetailActivity

class AlbumListAdapter(
    private val context: Context,
    private var albumList: MutableList<Album>
) : RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageAlbum: ImageView = itemView.findViewById(R.id.image_album)
        val textAlbumName: TextView = itemView.findViewById(R.id.text_album_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_album_item, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumList[position]
        holder.textAlbumName.text = album.name
        Glide.with(context).load(album.imageUrl).into(holder.imageAlbum)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AlbumDetailActivity::class.java)
            intent.putExtra("albumId", album.id)
            intent.putExtra("albumName", album.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = albumList.size

    fun updateList(newItems: List<Album>) {
        albumList.clear()
        albumList.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItem(item: Album) {
        albumList.add(item)
        notifyItemInserted(albumList.size - 1)
    }
}
