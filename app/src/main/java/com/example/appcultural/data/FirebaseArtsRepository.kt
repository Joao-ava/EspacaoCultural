package com.example.appcultural.data

import com.example.appcultural.entities.Art
import com.example.appcultural.entities.ArtLike
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseArtsRepository {
    private val db = Firebase.firestore
    private val collection = db.collection("arts")
    private val likesCollection = db.collection("art_likes")

    suspend fun list(name: String = "", artist: String = "", gender: String = ""): List<Art> {
        var query = collection.whereNotEqualTo("id", null)
        if (name != "") {
            query = query.whereEqualTo("name", name)
        }
        if (artist != "") {
            query = query.whereEqualTo("artist", artist)
        }
        if (gender != "") {
            query = query.whereArrayContains("genders", gender)
        }
        val result = query.get().await()
        return result.documents.mapNotNull { it.toObject(Art::class.java) }
    }

    suspend fun findById(id: String): Art? {
        val result = collection.document(id).get().await()
        return result.toObject(Art::class.java)
    }

    suspend fun add(art: Art): Art {
        val result = collection.add(art).await()
        art.id = result.id
        result.set(art).await()
        return art
    }

    suspend fun fetchByIds(ids: List<String>): List<Art> {
        val chunks = ids.chunked(10)
        val arts = mutableListOf<Art>()
        for (chunk in chunks) {
            val result = collection.whereIn(FieldPath.documentId(), chunk).get().await()
            arts.addAll(result.documents.mapNotNull {
                val art = it.toObject(Art::class.java)
                art?.id = it.id
                art
            })
        }
        return arts
    }

    suspend fun update(art: Art): Art {
        collection.document(art.id).set(art).await()
        return art
    }

    suspend fun hasLike(artId: String, userId: String): Boolean {
        return likesCollection.whereEqualTo("artId", artId).whereEqualTo("userId", userId).get().await().size() != 0
    }

    suspend fun addLike(artId: String, userId: String) {
        likesCollection.add(ArtLike(artId, userId)).await()
    }

    suspend fun removeLike(artId: String, userId: String) {
        likesCollection.whereEqualTo("userId", userId)
            .whereEqualTo("artId", artId)
            .get()
            .await()
            .documents
            .forEach {
                it.reference.delete()
            }
    }
}
