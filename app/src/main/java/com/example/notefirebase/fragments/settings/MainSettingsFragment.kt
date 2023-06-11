package com.example.notefirebase.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.databinding.FragmentMainSettingsBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.login.and.authorization.ReAuthenticateFragment
import com.example.notefirebase.fragments.login.and.authorization.SignInFragment
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainSettingsFragment : Fragment() {
    private lateinit var frameBinding: FragmentMainSettingsBinding
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        frameBinding = FragmentMainSettingsBinding.inflate(inflater, container, false)
        // Initializing user interface elements
        init()
        return frameBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        helper = Helper(requireActivity())
    }

    private fun init() {
        with(frameBinding) {
            // Getting the current user
            val user = Firebase.auth.currentUser
            // Getting a link to the Firebase database
            val database = Firebase.database.reference

            // Creating a list of provider IDs
            val providerIdsList: MutableList<String> = mutableListOf()

            // Check if there is a link to the database and the current user
            if (user != null && database != null) {
                userEmail.text = user.email
                // Getting a list of provider IDs that were used to authenticate the user
                for (userInfo in user.providerData) {
                    val providerId = userInfo.providerId
                    providerIdsList.add(providerId)
                }
                // If the user used Google to log in, then we display his name and photo
                if (providerIdsList.contains("google.com")) {
                    userName.text = user.displayName

                    Picasso.get().load(user.photoUrl).into(userImg)
                } else {
                    // If the user used another provider, then we get his name from the database
                    val userId = user.uid
                    database.child("users").child(userId).child("name").get()
                        .addOnSuccessListener { nameSnapshot ->
                            val name = nameSnapshot.getValue(String::class.java)
                            userName.text = name
                        }
                }
            }
        }
    }

    private fun setupClickListeners() {
        with(frameBinding) {
            // Log out from account
            btnLogOut.setOnClickListener {
                logOut()
            }

            // Change email
            btnChangeEmail.setOnClickListener {
                helper.navigate(ReAuthenticateFragment())
            }

            // Change password
            btnChangePassword.setOnClickListener {
                helper.navigate(InputYourEmailForResetFragment())
            }

            // Go to main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }
        }
    }

    private fun logOut() {
        Firebase.auth.signOut()
        updateUI(null)
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            return
        } else {
            helper.navigate(SignInFragment())
        }
    }
}