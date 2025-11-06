package com.example.babiling.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

// ✅ KTX extension imports
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage
import com.google.firebase.database.ktx.database

object FirebaseUtils {

    val auth: FirebaseAuth
        get() = Firebase.auth

    val firestore: FirebaseFirestore
        get() = Firebase.firestore

    val storage: FirebaseStorage
        get() = Firebase.storage

    val database: FirebaseDatabase
        get() = Firebase.database

    object FirestoreCollections {
        const val USERS = "users"
        const val TOPICS = "topics"
        const val LESSONS = "lessons"
        const val PROGRESS = "progress"
    }

    fun getUsersCollection() = firestore.collection(FirestoreCollections.USERS)

    fun getUserDocument(userId: String) = getUsersCollection().document(userId)

    fun getAvatarStorageRef(userId: String) = storage.reference.child("avatars/$userId.jpg")
}
