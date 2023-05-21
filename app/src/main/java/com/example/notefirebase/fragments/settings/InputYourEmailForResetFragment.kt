package com.example.notefirebase.fragments.settings

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentForgotPasswordBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.utils.UserDataCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class InputYourEmailForResetFragment : Fragment() {

    lateinit var binding: FragmentForgotPasswordBinding
    lateinit var userDataCheck: UserDataCheck

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnResetPassword.setOnClickListener {
            with(binding) {
                val email = binding.inputEmail.text.toString()
                userDataCheck = UserDataCheck("", email)
                if (!userDataCheck.isEmailValid()) {
                    labelEmail.setTextColor(Color.RED)
                    labelEmail.setText(R.string.email_error)
                }else{
                    Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            Log.d("SendPasswordReset", "Message send")
                        }
                    }
                }
            }
        }

        binding.btnToMain.setOnClickListener {

            val user = Firebase.auth.currentUser
            if(user == null){
                return@setOnClickListener
            }else{
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragmentHolder, MainFragment())
                    ?.commit()
            }
        }
    }
}