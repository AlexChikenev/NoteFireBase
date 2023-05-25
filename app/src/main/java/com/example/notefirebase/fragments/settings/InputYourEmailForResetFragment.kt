package com.example.notefirebase.fragments.settings

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentForgotPasswordBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.utils.Helper
import com.example.notefirebase.utils.UserDataCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class InputYourEmailForResetFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentForgotPasswordBinding
    private lateinit var userDataCheck: UserDataCheck
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        helper = Helper(requireActivity())
    }

    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Reset password
            btnResetPassword.setOnClickListener {
                val email = inputEmail.text.toString()
                userDataCheck = UserDataCheck("", email)
                if (!userDataCheck.isEmailValid()) {
                    labelEmail.setTextColor(Color.RED)
                    labelEmail.setText(R.string.email_error)
                } else {
                    Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Сообщение не было отправлено",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            // Go to main
            btnToMain.setOnClickListener {
                val user = Firebase.auth.currentUser
                if (user == null) {
                    return@setOnClickListener
                } else {
                    helper.navigate(MainFragment())
                }
            }
        }
    }
}