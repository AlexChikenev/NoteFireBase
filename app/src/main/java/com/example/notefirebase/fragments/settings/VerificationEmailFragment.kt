package com.example.notefirebase.fragments.settings

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentVerificationEmailBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.login.and.authorization.SignInFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class VerificationEmailFragment : Fragment() {

    private lateinit var fragmentBinding: FragmentVerificationEmailBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentVerificationEmailBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBinding.btnToMain.setOnClickListener {
            Firebase.auth.signOut()
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, SignInFragment())
                ?.commit()
        }
    }

    companion object {
        fun newInstance() = VerificationEmailFragment()
    }
}