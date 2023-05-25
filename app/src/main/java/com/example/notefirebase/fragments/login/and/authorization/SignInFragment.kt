package com.example.notefirebase.fragments.login.and.authorization

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentSigInBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.InputYourEmailForResetFragment
import com.example.notefirebase.utils.EmailPasswordAuth
import com.example.notefirebase.utils.GoogleAuth
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Suppress("DEPRECATION")
class SignInFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentSigInBinding
    lateinit var auth: FirebaseAuth
    private lateinit var googleAuthenticator: GoogleAuth
    private lateinit var emailPasswordAuthenticator: EmailPasswordAuth
    private lateinit var helper: Helper

    override fun onStart() {
        super.onStart()
        checkCurrentUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentSigInBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper = Helper(requireActivity())
        auth = FirebaseAuth.getInstance()
        googleAuthenticator = GoogleAuth(this)
        emailPasswordAuthenticator = EmailPasswordAuth(requireContext())
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Authentication via Google
            btnSignInWithGoogle.setOnClickListener {
                googleAuthenticator.signIn()
            }

            // Registration via email and password
            btnRegistration.setOnClickListener {
                helper.navigate(RegistrationInputFragment())
            }

            // Authentication via email and password
            btnEnter.setOnClickListener {
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    emailPasswordAuthenticator.signIn(
                        email,
                        password,
                        onSuccess = {
                            checkCurrentUser()
                        },
                        onFailure = {
                            updateUI(null)
                        }
                    )
                }
            }

            btnForgotPassword.setOnClickListener {
                 helper.navigate(InputYourEmailForResetFragment())
            }
        }

    }

    // Processing the authentication result via Google
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleAuthenticator.handleSignInResult(requestCode, resultCode, data,
            onSuccess = {
                updateUI(auth.currentUser)
            },
            onFailure = {
                updateUI(null)
            }
        )
    }

    // Checking the current user
    private fun checkCurrentUser() {
        updateUI(auth.currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        val isEmailVerified = auth.currentUser?.isEmailVerified

        if (user != null) {
            if (isEmailVerified == false) {
                showEmailVerificationDialog()
            } else if (isEmailVerified == true) {
                helper.navigate(MainFragment())
            }
        }
    }

    private fun showEmailVerificationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.alert_dialog_title)
        builder.setMessage(R.string.alert_dialog_message)
        builder.setPositiveButton(R.string.alert_dialog_positive) { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}

