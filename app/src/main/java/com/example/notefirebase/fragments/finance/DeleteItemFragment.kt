package com.example.notefirebase.fragments.finance

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentDeleteItemBinding
import com.example.notefirebase.firebasemodel.Income
import com.example.notefirebase.firebasemodel.Outcome
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DeleteItemFragment(
    private val incomes: Income?,
    private val outcomes: Outcome?,
    private val id: Int
) : DialogFragment() {

    private lateinit var fragmentBinding: FragmentDeleteItemBinding
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentDeleteItemBinding.inflate(inflater, container, false)
        helper = Helper(requireActivity())
        val decorView = dialog?.window!!.decorView
        helper.uiControls(decorView)
        return fragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()

        fragmentBinding.dialogBackground.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dismiss()
                    false
                }

                else -> false
            }
        }

        if (id == 0) {
            fragmentBinding.textItem.text = incomes!!.incomeName
            fragmentBinding.textDelete.setText(R.string.text_delete_income)
        } else {
            fragmentBinding.textItem.text = outcomes!!.outcomeName
            fragmentBinding.textDelete.setText(R.string.text_delete_outcome)
        }
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            btnCommit.setOnClickListener {
                val userUid = FirebaseAuth.getInstance().currentUser?.uid
                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
                if (id == 0) {
                    userUid?.let { it1 ->
                        incomes?.let { it2 ->
                            databaseReference.child("Users").child(it1).child("Incomes")
                                .child(it2.uniqueId)
                                .removeValue()
                        }
                    }
                    dismiss()
                } else {
                    userUid?.let { it1 ->
                        outcomes?.let { it2 ->
                            databaseReference.child("Users").child(it1).child("Outcomes")
                                .child(it2.uniqueId).removeValue()
                        }
                    }
                    dismiss()
                }
            }

            btnDecline.setOnClickListener {
                dismiss()
            }
        }

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