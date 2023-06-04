package com.example.notefirebase.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.notefirebase.R
import com.google.firebase.auth.FirebaseAuth

class EmailPasswordAuth(private val context: Context, activity: FragmentActivity,) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val helper: Helper = Helper(activity)

    @SuppressLint("InflateParams")
    fun signIn(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                helper.customToast(context, R.string.auth_error)
                onFailure()
            }
        }
    }
}
