package com.example.notefirebase.utils

import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// Class for user authentication via Google
@Suppress("DEPRECATION")
class GoogleAuth(private val fragment: Fragment) {

    private var auth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient

    private val tag = "GoogleActivity"
    private val rc_sign_in = 9001

    // Class Constructor
    init {
        // Creating a Google Sign In Options object with parameters for authentication via Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(fragment.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Creating a Google Sign In Client object for authentication via Google
        googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)

        // Getting the Fire base Auth object for authentication in Firebase
        auth = FirebaseAuth.getInstance()
    }

    // Method for starting authentication activity via Google
    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, rc_sign_in)
    }

    // Method for authenticating a user in Firebase using Google credentials
    private fun firebaseAuthWithGoogle(idToken: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(fragment.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Authentication was successful
                    Log.d(tag, "signInWithCredential:success")
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess()
                    } else {
                        onFailure()
                    }
                } else {
                    // Authentication failed
                    Log.w(tag, "signInWithCredential:failure", task.exception)
                    onFailure()
                }
            }
    }

    // Method for processing the authentication result via Google
    fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (requestCode == rc_sign_in) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Authentication via Google was successful, authentication in Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("firebaseAuthWithGoogle", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!, onSuccess, onFailure)
            } catch (e: ApiException) {
                // Authentication via Google failed
                Log.w(tag, "Google sign in failed", e)
                onFailure()
            }
        }
    }

    // Method for checking whether the user is authenticated in Firebase
//    fun checkCurrentUser(onSuccess: () -> Unit, onFailure: () -> Unit) {
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            onSuccess()
//        } else {
//            onFailure()
//        }
//    }
}
