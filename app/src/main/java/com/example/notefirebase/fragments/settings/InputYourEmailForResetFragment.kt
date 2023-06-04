package com.example.notefirebase.fragments.settings

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentForgotPasswordBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.login.and.authorization.ResetPasswordMsgFragment
import com.example.notefirebase.fragments.login.and.authorization.SignInFragment
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        val user = Firebase.auth.currentUser
        if (user == null) {
            fragmentBinding.btnToSettings.setText(R.string.btn_text_go_back)
        } else {
            val email = user.email
            fragmentBinding.inputEmail.setText(email)
        }
        setUpTouchListeners()
        helper = Helper(requireActivity())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTouchListeners() {
        with(fragmentBinding) {
            inputEmail.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        labelEmail.setText(R.string.label_input_email)
                        labelEmail.setTextColor(Color.WHITE)
                        false
                    }

                    else -> false
                }
            }
        }
    }

    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Reset password
            btnResetPassword.setOnClickListener {
                val email = inputEmail.text.toString()
                userDataCheck = UserDataCheck("", email)
                if (email != "") {
                    if (!userDataCheck.isEmailValid()) {
                        labelEmail.setTextColor(Color.RED)
                        labelEmail.setText(R.string.email_error)
                    } else {
                        // Check is email exists
                        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val signInMethods = it.result?.signInMethods
                                    // Doesn't exist
                                    if (signInMethods == null || signInMethods.isEmpty()) {
                                        helper.customToast(
                                            requireContext(), R.string.email_doesnt_exist
                                        )
                                        // Exist
                                    } else {
                                        val message = ResetPasswordMsgFragment(email)
                                        message.show(
                                            requireActivity().supportFragmentManager,
                                            "ResetPasswordMsg"
                                        )
                                    }

                                }
                            }
                    }
                } else {
                    helper.customToast(requireContext(), R.string.input_email)
                }
            }

            // Go to main
            btnToSettings.setOnClickListener {
                val user = Firebase.auth.currentUser
                if (user == null) {
                    helper.navigate(SignInFragment())
                } else {
                    helper.navigate(MainSettingsFragment())
                }
            }
        }
    }
}