package com.example.appcultural.data

import com.example.appcultural.entities.Comment
import com.example.appcultural.entities.User

class MockCommentRepository {
    private val data: ArrayList<Comment> = arrayListOf(
        Comment("1", "Comentario 1", 1),
        Comment("2", "Comentario 2", 2),
        Comment("3", "Comentario 3", 3),
        Comment("4", "Comentario 4", 4),
        Comment("5", "Comentario 5", 5),
        Comment("6", "Comentario 6", 6),
        Comment("7", "Comentario 7", 7),
        Comment("8", "Comentario 8", 8)
    )

    fun list(): ArrayList<Comment> {
        return data
    }
}