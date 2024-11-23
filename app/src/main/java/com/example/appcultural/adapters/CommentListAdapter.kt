package com.example.appcultural.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcultural.R
import com.example.appcultural.entities.CommentItem

class CommentListAdapter(
    val data: List<CommentItem>,
    val onLikeClick: (CommentItem) -> Unit // Callback para curtir o comentário
): RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {
    class CommentViewHolder(
        itemView: View,
        val onLikeClick: (CommentItem) -> Unit
    ): RecyclerView.ViewHolder(itemView) {
        private val commentAuthor: TextView = itemView.findViewById(R.id.comment_author_name)
        private val commentContent: TextView = itemView.findViewById(R.id.comment_content_text)
        private val likeCount: TextView = itemView.findViewById(R.id.like_count_text)
        private val likeButton: ImageButton = itemView.findViewById(R.id.like_button)

        fun bind(comment: CommentItem) {
            commentAuthor.text = comment.username
            commentContent.text = comment.content
            likeCount.text = comment.likes.toString()

            // Define o ícone do botão de curtir
            if (comment.likedByUser) {
                likeButton.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                likeButton.setImageResource(R.drawable.outline_favorite_24)
            }

            // Callback para curtir/descurtir
            likeButton.setOnClickListener {
                onLikeClick(comment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_comment_item, parent, false)

        return CommentViewHolder(view, onLikeClick)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = data[position]
        holder.bind(comment)
    }
}