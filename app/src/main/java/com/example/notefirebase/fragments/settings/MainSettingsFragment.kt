package com.example.notefirebase.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentMainSettingsBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.login.and.authorization.SignInFragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainSettingsFragment : Fragment() {
    lateinit var binding: FragmentMainSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainSettingsBinding.inflate(inflater, container, false)
        // Initializing user interface elements
        init()
        return binding.root
    }

    private fun init() {
        // Getting the current user
        val user = Firebase.auth.currentUser
        // Getting a link to the Firebase database
        val database = Firebase.database.reference

        // Creating a list of provider IDs
        val providerIdsList: MutableList<String> = mutableListOf()

        // Check if there is a link to the database and the current user
        if (user != null && database != null) {
            binding.userEmail.text = user.email
            // Getting a list of provider IDs that were used to authenticate the user
            for (userInfo in user.providerData) {
                val providerId = userInfo.providerId
                providerIdsList.add(providerId)
            }
            //Log.d("provider", "$providerIdsList")
            // If the user used Google to log in, then we display his name and photo
            if (providerIdsList.contains("google.com")) {
                binding.userName.text = user.displayName

                Picasso.get()
                    .load(user.photoUrl)
                    .into(binding.userImg)
            } else {
                // If the user used another provider, then we get his name from the database
                val userId = user.uid
                database.child("users").child(userId).child("name").get()
                    .addOnSuccessListener { nameSnapshot ->
                        val name = nameSnapshot.getValue(String::class.java)
                        binding.userName.text = name
                    }
            }
        } else {
            Log.d("User||DbRef", "Error")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sign out from account
        binding.btnLogOut.setOnClickListener {
            signOut()
        }

        binding.btnChangePassword.setOnClickListener{
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, InputYourEmailForResetFragment())
                ?.commit()
        }

        binding.btnToMain.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, MainFragment())
                ?.commit()
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        updateUI(null)
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            return
        } else {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, SignInFragment())
                ?.commit()
        }
    }

}