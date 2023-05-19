package com.example.notefirebase.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class EmailPasswordAuth(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    onFailure()
                }
            }
    }
}
