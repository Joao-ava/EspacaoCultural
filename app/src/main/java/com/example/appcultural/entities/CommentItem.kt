package com.example.appcultural.entities

data class CommentItem (
    var id: String = "", // ID único do comentário
    val content: String = "", // Texto do comentário
    var likes: Int = 0, // Número de curtidas
    val userId: String = "", // Identifica o usuário que fez o comentário
    var username: String = "", // Autor do comentário
    val artId: String = "", // Identifica a arte associada ao comentário
    val likedByUser: Boolean = false // Indica se o usuário atual curtiu o comentário
) {
    fun toComment(): Comment {
        return Comment(
            id = this.id,
            content = this.content,
            likes = this.likes,
            userId = this.userId,
            artId = this.artId,
            username = this.username
        )
    }
}
