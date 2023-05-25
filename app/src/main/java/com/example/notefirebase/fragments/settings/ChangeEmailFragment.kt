package com.example.notefirebase.fragments.settings

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentChangeEmailBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.login.and.authorization.ReAuthenticateFragment
import com.example.notefirebase.fragments.login.and.authorization.VerificationFragment
import com.example.notefirebase.utils.Helper
import com.example.notefirebase.utils.UserDataCheck
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangeEmailFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentChangeEmailBinding
    private lateinit var userDataCheck: UserDataCheck
    private lateinit var auth: FirebaseAuth
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentChangeEmailBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        helper = Helper(requireActivity())
    }

    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Reset email
            btnResetEmail.setOnClickListener {
                val user = auth.currentUser
                val newEmail = inputNewEmail.text.toString()
                if (newEmail.isNotEmpty()) {
                    userDataCheck = UserDataCheck("", newEmail)
                    if (!userDataCheck.isEmailValid()) {
                        labelNewEmail.setTextColor(Color.RED)
                        labelNewEmail.setText(R.string.email_error)
                    } else {
                        auth.fetchSignInMethodsForEmail(newEmail).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val result = it.result?.signInMethods ?: emptyList<String>()
                                if (result.isNotEmpty()) {
                                    labelNewEmail.setTextColor(Color.RED)
                                    labelNewEmail.setText(R.string.email_already_exist)
                                } else {
                                    updateEmail(newEmail, requireContext())
                                    user!!.updateEmail(newEmail)
                                    helper.navigate(VerificationEmailFragment())
                                }
                            }
                        }
                    }
                }
            }

            // Go to main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }
        }
    }

    // Updating email
    private fun updateEmail(newEmail: String, context: Context) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            user.updateEmail(newEmail).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("User", "User email address updated.")
                    val email = Firebase.auth.currentUser?.email
                    Log.d("Email", "$email")
                    val currentUser = Firebase.auth.currentUser

                    if (currentUser != null) {
                        currentUser.sendEmailVerification().addOnCompleteListener { task ->
                            if(!task.isSuccessful){
                                Toast.makeText(context, "Не удалось отправить подтверждение", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Log.d("currentUser", "error")
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = ChangeEmailFragment()
    }
}