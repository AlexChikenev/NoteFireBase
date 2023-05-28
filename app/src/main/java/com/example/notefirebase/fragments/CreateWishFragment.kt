package com.example.notefirebase.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.databinding.FragmentCreateWishBinding

class CreateWishFragment(private val wishListFragment: WishListFragment) : DialogFragment() {

    private lateinit var binding: FragmentCreateWishBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateWishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create new wish
        binding.btnCommitWish.setOnClickListener {
            if (binding.editTextWishName.text.toString() != "") {
            }
        }
    }

    // Parameters for this fragment
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1000)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }
}