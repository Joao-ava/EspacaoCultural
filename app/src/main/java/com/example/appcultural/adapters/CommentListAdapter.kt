package com.example.appcultural.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcultural.R
import com.example.appcultural.entities.CommentItem

class CommentListAdapter(
    val data: List<CommentItem>,
    val isAdmin: Boolean = false,
    val onLikeClick: (CommentItem) -> Unit, // Callback para curtir o comentário
    val deleteComment: (CommentItem) -> Unit
): RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {
    class CommentViewHolder(
        itemView: View,
        val isAdmin: Boolean,
        val onLikeClick: (CommentItem) -> Unit,
        val deleteComment: (CommentItem) -> Unit
    ): RecyclerView.ViewHolder(itemView) {
        private val commentAuthor: TextView = itemView.findViewById(R.id.comment_author_name)
        private val commentContent: TextView = itemView.findViewById(R.id.comment_content_text)
        private val likeCount: TextView = itemView.findViewById(R.id.like_count_text)
        private val likeButton: ImageButton = itemView.findViewById(R.id.like_button)
        private val deleteButton: Button = itemView.findViewById(R.id.button)

        fun bind(comment: CommentItem) {
            commentAuthor.text = comment.username
            commentContent.text = comment.content
            likeCount.text = comment.likes.toString()
            deleteButton.visibility = if (isAdmin) View.VISIBLE else View.GONE

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

            deleteButton.setOnClickListener {
                deleteComment(comment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_comment_item, parent, false)

        return CommentViewHolder(view, isAdmin, onLikeClick, deleteComment)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = data[position]
        holder.bind(comment)
    }
}