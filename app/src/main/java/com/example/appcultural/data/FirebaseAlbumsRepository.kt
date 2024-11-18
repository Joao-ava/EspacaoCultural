package com.example.appcultural.data

import com.example.appcultural.entities.Album
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAlbumsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("albums")

    suspend fun listAlbums(): List<Album> {
        val result = collection.get().await()
        return result.documents.mapNotNull {
            val album = it.toObject(Album::class.java)
            album?.id = it.id
            album
        }
    }

    suspend fun findById(albumId: String): Album? {
        val document = collection.document(albumId).get().await()
        val album = document.toObject(Album::class.java)
        album?.id = document.id
        return album
    }


    suspend fun createAlbum(name: String): Album {
        val album = Album(name = name)
        val result = collection.add(album).await()
        album.id = result.id
        return album
    }

    suspend fun addArtToAlbum(albumId: String, artId: String) {
        collection.document(albumId)
            .update("artIds", com.google.firebase.firestore.FieldValue.arrayUnion(artId))
            .await()
    }

    // Método adicionado para atualizar o álbum
    suspend fun updateAlbum(albumId: String, updatedAlbum: Album) {
        collection.document(albumId).set(updatedAlbum).await()
    }
}
