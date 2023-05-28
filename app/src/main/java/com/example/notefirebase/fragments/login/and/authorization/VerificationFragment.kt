package com.example.notefirebase.fragments.login.and.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentMessageBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class VerificationFragment : Fragment() {
    private lateinit var fragmentBinding: FragmentMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentMessageBinding.inflate(inflater, container, false)
        Firebase.auth.signOut()
        Firebase.auth.currentUser?.isEmailVerified
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBinding.btnCheckVerification.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, SignInFragment())
                ?.commit()

        }
    }
}