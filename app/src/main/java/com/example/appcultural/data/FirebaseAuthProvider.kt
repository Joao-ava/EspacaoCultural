
package com.example.appcultural.data

import android.content.Context
import android.content.SharedPreferences
import com.example.appcultural.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthProvider {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("admins")
    private val usersCollection = firestore.collection("users")

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_IS_ADMIN = "is_admin"
    }

    // criar usuário com email e senha:)
    suspend fun create(name: String, email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val userId = result.user?.uid ?: throw Exception("Falha ao criar usuário")
        usersCollection.add(mapOf("userId" to userId, "username" to name))
    }

    suspend fun createEmployee(name: String, email: String, password: String): Boolean {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val userId = result.user?.uid
        if (userId != null) {
            collection.add(mapOf("userId" to userId)).await()
            usersCollection.add(mapOf("userId" to userId, "username" to name))
            return true
        }
        throw Exception("Falha ao criar usuário")
    }
    suspend fun login(username: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(username, password).await()
        if (result.user == null) throw Exception("Usuário não encontrado")
        val userData = usersCollection.whereEqualTo("userId", result.user!!.uid).get().await()
        if (userData.isEmpty) throw Exception("Usuário não encontrado")
        val userDataMap = userData.documents.first().data
        return User(result.user!!.uid, userDataMap!!.get("username") as String)
    }
    suspend fun getCurrentUser(): User {
        val userData = usersCollection.whereEqualTo("userId", auth.currentUser!!.uid).get().await()
        if (userData.isEmpty) throw Exception("Usuário não encontrado")
        val userDataMap = userData.documents.first().data
        return User(auth.currentUser?.uid ?: "", userDataMap!!.get("username") as String)
    }
    suspend fun getAdmin(context: Context): Boolean {
        val value = collection.whereEqualTo("userId", auth.currentUser?.uid ?: "").get().await().documents.isNotEmpty()
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_ADMIN, value).apply()
        return value
    }

    fun isAdmin(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_ADMIN, true)
    }
}
