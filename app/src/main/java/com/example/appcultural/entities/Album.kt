package com.example.appcultural.entities

data class Album(
    var id: String = "",          // Atualizamos para String, já que o Firebase usa strings como IDs.
    var name: String = "",
    val imageUrl: String = "",    // Mantém o campo existente.
    val artIds: List<String> = listOf() // Adicionamos o campo para armazenar os IDs das artes.
)
