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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Suppress("DEPRECATION")
class SignInFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentSigInBinding
    lateinit var auth: FirebaseAuth
    private lateinit var googleAuthenticator: GoogleAuth
    private lateinit var emailPasswordAuthenticator: EmailPasswordAuth

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
        initialize()
        return fragmentBinding.root
    }

    // Initialization
    private fun initialize() {
        auth = FirebaseAuth.getInstance()
        googleAuthenticator = GoogleAuth(this)
        emailPasswordAuthenticator = EmailPasswordAuth(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Authentication via Google
        fragmentBinding.btnSignInWithGoogle.setOnClickListener {
            googleAuthenticator.signIn()
        }

        // Registration via email and password
        fragmentBinding.btnRegistration.setOnClickListener {
            navigate(RegistrationInputFragment())
        }

        // Authentication via email and password
        fragmentBinding.btnEnter.setOnClickListener {
            val email = fragmentBinding.inputEmail.text.toString()
            val password = fragmentBinding.inputPassword.text.toString()

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

        fragmentBinding.btnForgotPassword.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, InputYourEmailForResetFragment())
                ?.commit()
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
                navigate(MainFragment())
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

    private fun navigate(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, fragment).commit()
    }
}

