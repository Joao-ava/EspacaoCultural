
package com.example.appcultural.data

import android.content.Context
import android.content.SharedPreferences
import com.example.appcultural.data.MockAuthProvider.Companion
import com.example.appcultural.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthProvider {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("admins")

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_IS_ADMIN = "is_admin"
    }

    // criar usuário com email e senha:)
    fun create(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }
    suspend fun login(username: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(username, password).await()
        if (result.user == null) throw Exception("Usuário não encontrado")
        return User(result.user!!.uid, result.user!!.displayName ?: "Anonimo")
    }
    fun getCurrentUser(): User {
        return User(auth.currentUser?.uid ?: "", auth.currentUser?.displayName ?: "Anonimo")
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
