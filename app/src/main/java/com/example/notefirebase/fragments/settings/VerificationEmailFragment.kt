package com.example.notefirebase.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.databinding.FragmentVerificationEmailBinding
import com.example.notefirebase.fragments.login.and.authorization.SignInFragment
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class VerificationEmailFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentVerificationEmailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentVerificationEmailBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper = Helper(requireActivity())
        fragmentBinding.btnToMain.setOnClickListener {
            Firebase.auth.signOut()
            helper.navigate(SignInFragment())
        }
    }

    companion object {
        fun newInstance() = VerificationEmailFragment()
    }
}