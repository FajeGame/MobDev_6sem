package com.example.sem6lab6.profile

import android.util.Log
import com.example.sem6lab6.analytics.CrashReporter
import com.example.sem6lab6.firebase.FcmTokenStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ProfileRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val tokenStore: FcmTokenStore,
    private val crashReporter: CrashReporter
) {
    fun ensureProfile(
        name: String,
        onReady: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "User already authenticated: ${currentUser.uid}")
            saveProfile(currentUser.uid, name, onReady, onError)
            return
        }

        Log.d(TAG, "Starting anonymous auth...")
        auth.signInAnonymously()
            .addOnSuccessListener { result ->
                val userId = result.user?.uid
                Log.d(TAG, "Anonymous auth success, userId=$userId")
                if (userId == null) {
                    val error = IllegalStateException("Firebase userId is null")
                    Log.e(TAG, "userId is null after auth", error)
                    onError(error)
                } else {
                    saveProfile(userId, name, onReady, onError)
                }
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Anonymous auth failed", error)
                crashReporter.log("anonymous auth failed")
                crashReporter.recordException(error)
                onError(error)
            }
    }

    fun listenProfile(
        userId: String,
        onProfile: (UserProfile?) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        return firestore.collection(USERS_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    crashReporter.log("listenProfile snapshot error userId=$userId")
                    crashReporter.recordException(error)
                    onError(error)
                    return@addSnapshotListener
                }
                onProfile(snapshot?.toObject(UserProfile::class.java))
            }
    }

    fun updateFcmToken(userId: String) {
        val token = tokenStore.getToken()
        if (token.isBlank()) return

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .update(
                mapOf(
                    "fcmToken" to token,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to update FCM token", error)
                crashReporter.log("updateFcmToken failed userId=$userId")
                crashReporter.recordException(error)
            }
    }

    private fun saveProfile(
        userId: String,
        name: String,
        onReady: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val profile = UserProfile(
            userId = userId,
            name = name,
            email = "${userId.take(8)}@bigpencils.firebase",
            fcmToken = tokenStore.getToken(),
            updatedAt = System.currentTimeMillis()
        )

        Log.d(TAG, "Saving profile for userId=$userId")
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(profile)
            .addOnSuccessListener {
                Log.d(TAG, "Profile saved successfully")
                onReady(userId)
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to save profile", error)
                crashReporter.log("saveProfile failed userId=$userId")
                crashReporter.recordException(error)
                onError(error)
            }
    }

    private companion object {
        const val TAG = "ProfileRepository"
        const val USERS_COLLECTION = "users"
    }
}
