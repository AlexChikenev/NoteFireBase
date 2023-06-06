package com.example.notefirebase.fragments.create.note.and.projects

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
import com.example.notefirebase.databinding.FragmentDeleteTaskBinding
import com.example.notefirebase.firebasemodel.Task
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DeleteTaskFragment(
    private val task: Task?,
    private val id: Int
) : DialogFragment() {

    private lateinit var fragmentBinding: FragmentDeleteTaskBinding
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentDeleteTaskBinding.inflate(inflater, container, false)
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
            fragmentBinding.textTask.text = task!!.taskName
            fragmentBinding.textDelete.setText(R.string.text_delete_personal)
        } else {
            fragmentBinding.textTask.text = task!!.taskName
            fragmentBinding.textDelete.setText(R.string.text_delete_work)
        }
    }

    private fun setUpClickListeners() {
        with(fragmentBinding) {
            btnCommit.setOnClickListener {
                val userUid = FirebaseAuth.getInstance().currentUser?.uid
                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
                // Delete personal task
                if (id == 0) {
                    userUid?.let { it1 ->
                        task?.uniqueId?.let { it2 ->
                            databaseReference.child("Users").child(it1).child("Personal")
                                .child(it2)
                                .removeValue()
                        }
                    }
                    dismiss()
                }// Delete work task
                else {
                    userUid?.let { it1 ->
                        task?.uniqueId?.let { it2 ->
                            databaseReference.child("Users").child(it1).child("Work").child(it2)
                                .removeValue()
                        }
                    }
                }
                dismiss()
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