package com.example.notefirebase.fragments.login.and.authorization

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentRegistrationInputBinding
import com.example.notefirebase.utils.Helper
import com.example.notefirebase.utils.UserDataCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationInputFragment : Fragment() {
    private lateinit var fragmentBinding: FragmentRegistrationInputBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDataCheck: UserDataCheck
    private lateinit var helper: Helper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentRegistrationInputBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper = Helper(requireActivity())
        auth = Firebase.auth
        setUpClickListeners()
        setUpTouchListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTouchListeners() {
        with(fragmentBinding) {
            inputName.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        nameLabel.setText(R.string.input_name)
                        nameLabel.setTextColor(Color.WHITE)
                        false
                    }

                    else -> false
                }
            }

            inputEmail.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        emailLabel.setText(R.string.input_email)
                        emailLabel.setTextColor(Color.WHITE)
                        false
                    }

                    else -> false
                }
            }

            inputPassword.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        passwordLabel.setText(R.string.input_password)
                        passwordLabel.setTextColor(Color.WHITE)
                        false
                    }

                    else -> false
                }
            }
        }

    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Clicking on the account creation button
            btnCreateAcc.setOnClickListener {

                val userName = inputName.text.toString()
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()

                userDataCheck = UserDataCheck(password, email)

                // Checking userName
                if (userName.isEmpty()) {
                    nameLabel.setTextColor(Color.RED)
                    // Checking email
                } else if (!userDataCheck.isEmailValid()) {
                    emailLabel.setTextColor(Color.RED)
                    emailLabel.setText(R.string.email_error)
                    // Checking password length
                } else if (userDataCheck.checkPasswordLength()) {
                    passwordLabel.setTextColor(Color.RED)
                    passwordLabel.setText(R.string.password_length_error)
                    // Checking upper and lower cases
                } else if (userDataCheck.checkPasswordUpLow()) {
                    passwordLabel.setTextColor(Color.RED)
                    passwordLabel.setText(R.string.password_up_low_error)
                    // Checking eng chars
                } else if (!userDataCheck.checkPasswordEngCh()) {
                    passwordLabel.setTextColor(Color.RED)
                    passwordLabel.setText(R.string.password_lang_error)
                    // Create acc
                } else {
                    createAccount(
                        email, password, userName
                    )
                }
            }

            // Go back
            btnGoBack.setOnClickListener {
                helper.navigate(SignInFragment())
            }
        }
    }

    // Method for creating account using email and password
    private fun createAccount(email: String, password: String, userName: String) {
        // Checking email
        // Checking whether an account has been created with such an email or not
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                val result = it.result?.signInMethods ?: emptyList<String>()
                if (result.isNotEmpty()) {
                    fragmentBinding.emailLabel.setTextColor(Color.RED)
                    fragmentBinding.emailLabel.setText(R.string.email_already_exist)
                } else {
                    Log.d("MyLog", "Error")
                    // Creating a new user
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener() { task ->
                            //If the operation was successful, we write the user name to the database and send an email with email confirmation
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val database = Firebase.database.reference
                                val userId = user?.uid
                                if (userId != null) {
                                    database.child("users").child(userId).child("name")
                                        .setValue(userName)
                                    sendEmailVerification(user)
                                    helper.navigate(VerificationFragment())
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Registration failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener(requireActivity()) { task ->
            if (!task.isSuccessful) {
                Log.e("EmailPassword", "sendEmailVerification", task.exception)
                Toast.makeText(
                    context,
                    "Не удалось отправить подтверждение",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}