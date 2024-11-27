package com.example.appcultural.views

import android.R
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcultural.adapters.CommentListAdapter
import com.example.appcultural.data.FirebaseAuthProvider
import com.example.appcultural.data.FirebaseCommentsRepository
import com.example.appcultural.databinding.ActivityCommentsBinding
import com.example.appcultural.entities.Comment
import com.example.appcultural.entities.CommentItem
import kotlinx.coroutines.launch

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private lateinit var adapter: CommentListAdapter
    private val comments = mutableListOf<CommentItem>()
    private val commentsRepository: FirebaseCommentsRepository = FirebaseCommentsRepository()

    private val artId by lazy { intent.getStringExtra("id") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.main)

        // Configuração do RecyclerView
        binding.recycleView.layoutManager = LinearLayoutManager(this)

        // Criando o adaptador
        val isAdmin = FirebaseAuthProvider().isAdmin(this)
        adapter = CommentListAdapter(
            comments, isAdmin, { comment ->
                incrementLikeCount(comment)
            },
            deleteComment = { comment -> deleteComment(comment) },
        )

        binding.recycleView.adapter = adapter

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Tratamento do botão de envio de comentário
        binding.sendCommentFab.setOnClickListener {
            val newCommentText = binding.commentInputText.text.toString()
            if (newCommentText.isBlank()) {
                Toast.makeText(this, "O comentário não pode estar vazio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val authProvider = FirebaseAuthProvider()
                val user = authProvider.getCurrentUser()
                if (user == null) {
                    Toast.makeText(this@CommentsActivity, "Você precisa estar logado para comentar", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val username = user.name ?: "Usuário Desconhecido"
                val userId = user.id
                // Adiciona o comentário ao Firestore
                val comment = Comment(
                    id = "",
                    content = newCommentText,
                    likes = 0,
                    userId = userId,
                    username = username,
                    artId = artId
                )
                addComment(comment)
            }
        }

        // Carregar os comentários ao iniciar a activity
        loadComments()
    }

    private fun loadComments() {
        lifecycleScope.launch {
            try {
                val loadedComments = commentsRepository.listByArt(artId)
                comments.clear()
                comments.addAll(loadedComments)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@CommentsActivity, "Erro ao carregar comentários: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun addComment(comment: Comment) {
        lifecycleScope.launch {
            try {
                val addedComment = commentsRepository.add(comment)
                comments.add(addedComment)
                adapter.notifyDataSetChanged()
                binding.commentInputText.text.clear()
                Toast.makeText(this@CommentsActivity, "Comentário adicionado!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@CommentsActivity, "Erro ao adicionar comentário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteComment(comment: CommentItem) {
        lifecycleScope.launch {
            try {
                commentsRepository.delete(comment.id)
                loadComments()
                Toast.makeText(this@CommentsActivity, "Comentário excluido!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@CommentsActivity, "Erro ao adicionar comentário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun incrementLikeCount(comment: CommentItem) {
        lifecycleScope.launch {
            try {
                val increment = if (comment.likedByUser) -1 else 1
                val updated = commentsRepository.updateLikes(comment.id, increment)
                val index = comments.indexOfFirst { it.id == comment.id }
                if (index == -1) return@launch
                val updatedComment =
                    comment.copy(likes = updated.likes, likedByUser = !comment.likedByUser)
                comments[index] = updatedComment
                adapter.notifyItemChanged(index)
            } catch (e: Exception) {
                Toast.makeText(this@CommentsActivity, "Erro ao atualizar curtida: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
