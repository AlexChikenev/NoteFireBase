package com.example.notefirebase.fragments.login.and.authorization

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.notefirebase.utils.Helper
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentMessagePasswordResetBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordMsgFragment(private val email: String) : DialogFragment() {

    lateinit var fragmentBinding: FragmentMessagePasswordResetBinding
    lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentMessagePasswordResetBinding.inflate(inflater, container, false)
        helper = Helper(requireActivity())
        val decorView: View = dialog?.window!!.decorView
        helper.uiControls(decorView)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = Firebase.auth.currentUser
        if (user != null) {
            initUi()
        }
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Commit reset
            btnCommit.setOnClickListener {
                Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        helper.customToast(
                            requireContext(), R.string.doesnt_send
                        )
                    } else {
                        dismiss()
                        Firebase.auth.signOut()
                        helper.navigate(SignInFragment())
                    }
                }
            }

            // Decline reset
            btnDismiss.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun initUi() {
        fragmentBinding.labelSure.visibility = View.VISIBLE
        fragmentBinding.btnDismiss.visibility = View.VISIBLE
        fragmentBinding.btnCommit.setText(R.string.btn_text_yes)
        fragmentBinding.inputIncome.setText(R.string.alert_dialog_password_message_reset)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val backgroundColor = ContextCompat.getColor(requireContext(), R.color.white_29)
            setBackgroundDrawable(ColorDrawable(backgroundColor))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}