package com.example.notefirebase.fragments.login.and.authorization

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentReAuthenticateBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.ChangeEmailFragment
import com.example.notefirebase.utils.EmailPasswordAuth
import com.example.notefirebase.utils.UserDataCheck
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ReAuthenticateFragment : Fragment() {
    private lateinit var fragmentBinding: FragmentReAuthenticateBinding
    private lateinit var userDataCheck: UserDataCheck


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentReAuthenticateBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(fragmentBinding) {
            btnEnter.setOnClickListener {
                val user = Firebase.auth.currentUser!!
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()
                userDataCheck = UserDataCheck("", email)
                if (!userDataCheck.isEmailValid()) {
                    labelEmail.setTextColor(Color.RED)
                    labelEmail.setText(R.string.email_error)
                } else if (password.isEmpty()) {
                    labelPassword.setTextColor(Color.RED)
                    labelPassword.setText(R.string.password_error)
                } else {
                    val credential = EmailAuthProvider.getCredential(email, password)
                    user.reauthenticate(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            navigate(ChangeEmailFragment())
                        } else {
                            Toast.makeText(requireContext(), "Данные не верны", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

            btnToMain.setOnClickListener {
                navigate(MainFragment())
            }
        }

    }

    private fun navigate(fragment: Fragment) {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragmentHolder, fragment)
            ?.commit()
    }

    companion object {
        fun newInstance() = ReAuthenticateFragment()
    }
}