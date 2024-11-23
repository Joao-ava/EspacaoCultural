package com.example.appcultural.entities

data class Comment (
    var id: String = "", // ID único do comentário
    val content: String = "", // Texto do comentário
    var likes: Int = 0, // Número de curtidas
    val userId: String = "", // Identifica o usuário que fez o comentário
    val username: String = "", // Username do autor do comentário"
    val artId: String = "", // Identifica a arte associada ao comentário
)