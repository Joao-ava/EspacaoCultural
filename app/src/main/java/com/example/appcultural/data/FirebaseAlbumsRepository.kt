package com.example.appcultural.data

import com.example.appcultural.entities.Album
import com.example.appcultural.entities.Art
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAlbumsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("albums")

    suspend fun list(userId: String, name: String = ""): List<Album> {
        val query = collection.whereEqualTo("userId", userId)
        if (name.isNotEmpty()) {
            query.whereEqualTo("name", userId)
        }
        val result = query.get().await()
        val albums = result.documents.mapNotNull { it.toObject(Album::class.java) }
        return albums
    }

    suspend fun findById(albumId: String): Album? {
        val album = collection.document(albumId).get().await().toObject(Album::class.java)
        return album
    }

    suspend fun create(album: Album): Album {
        val result = collection.add(album).await()
        album.id = result.id
        result.set(album).await()
        return album
    }

    suspend fun appendArt(albumId: String, artId: String) {
        collection.document(albumId)
            .update("artIds", com.google.firebase.firestore.FieldValue.arrayUnion(artId))
            .await()
    }

    suspend fun update(albumId: String, updatedAlbum: Album) {
        collection.document(albumId).set(updatedAlbum).await()
    }
}
