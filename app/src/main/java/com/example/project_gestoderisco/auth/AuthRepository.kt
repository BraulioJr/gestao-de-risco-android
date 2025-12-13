package com.example.project_gestoderisco.auth

import com.example.project_gestoderisco.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun currentUser(): FirebaseUser? = auth.currentUser

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        profile: UserProfile
    ): Result<UserProfile> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user!!.uid
            val profileWithUid = profile.copy(uid = uid, email = email)
            firestore.collection("users").document(uid).set(profileWithUid).await()
            Result.success(profileWithUid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchUserProfile(uid: String): Result<UserProfile> {
        return try {
            val snap = firestore.collection("users").document(uid).get().await()
            val profile = snap.toObject(UserProfile::class.java)
            if (profile != null) Result.success(profile) else Result.failure(Exception("Perfil não encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}