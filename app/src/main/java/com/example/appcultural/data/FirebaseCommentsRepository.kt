package com.example.appcultural.data

import com.example.appcultural.entities.Comment
import com.example.appcultural.entities.CommentItem
import com.example.appcultural.entities.CommentLike
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseCommentsRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("comments")
    private val likeCollection = firestore.collection("comment_likes")
    private val authProvider = FirebaseAuthProvider()

    suspend fun listByArt(artId: String): List<CommentItem> {
        val result = collection.whereEqualTo("artId", artId).get().await()
        val data = result.documents.mapNotNull { it.toObject(Comment::class.java) }
        val commentIds = data.mapNotNull { it.id }
        val user = authProvider.getCurrentUser()
        var likesResult: List<CommentLike> = listOf()
        if (data.isNotEmpty()) {
            likesResult = likeCollection
                .whereIn("commentId", commentIds)
                .whereEqualTo("userId", user.id)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(CommentLike::class.java) }
        }
        return data.map {
            val likedByUser = likesResult.any { like -> like.commentId == it.id }
            CommentItem(
                id = it.id,
                content = it.content,
                likes = it.likes,
                userId = it.userId,
                username = it.username,
                artId = it.artId,
                likedByUser = likedByUser
            )
        }
    }

    suspend fun add(comment: Comment): CommentItem {
        val result = collection.add(comment).await()
        comment.id = result.id
        result.set(comment).await()
        val user = authProvider.getCurrentUser()
        val likesResult = likeCollection
            .whereEqualTo("commentId", comment.id)
            .whereEqualTo("userId", user.id)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(CommentLike::class.java)?.commentId }
        return CommentItem(
            id = comment.id,
            content = comment.content,
            likes = comment.likes,
            userId = comment.userId,
            username = comment.username,
            artId = comment.artId,
            likedByUser = likesResult.contains(comment.id)
        )
    }

    suspend fun updateLikes(commentId: String, increment: Int): CommentItem {
        val commentRef = collection.document(commentId)
        val currentLikes = commentRef.get().await().getLong("likes") ?: 0
        commentRef.update("likes", currentLikes + increment).await()
        val user = authProvider.getCurrentUser()
        if (increment > 0) {
            likeCollection.add(CommentLike(userId = user.id, commentId = commentId)).await()
        } else {
            likeCollection
                .whereEqualTo("userId", user.id)
                .whereEqualTo("commentId", commentId)
                .get()
                .await()
                .documents
                .forEach {
                    it.reference.delete()
                }
        }
        val updatedComment = commentRef.get().await().toObject(Comment::class.java)
        return CommentItem(
            id = updatedComment!!.id,
            content = updatedComment.content,
            likes = updatedComment.likes,
            userId = updatedComment.userId,
            username = updatedComment.username,
            artId = updatedComment.artId,
            likedByUser = increment > 0
        )
    }

    suspend fun delete(commentId: String) {
        collection.document(commentId).delete().await()
    }
}
