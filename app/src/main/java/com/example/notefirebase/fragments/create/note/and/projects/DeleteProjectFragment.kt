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
import com.example.notefirebase.databinding.FragmentDeleteProjectBinding
import com.example.notefirebase.firebasemodel.Project
import com.example.notefirebase.utils.Helper

class DeleteProjectFragment(private val selectedProject: Project) : DialogFragment() {

    private lateinit var fragmentBinding: FragmentDeleteProjectBinding
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentDeleteProjectBinding.inflate(inflater, container, false)
        helper = Helper(requireActivity())
        val decorView = dialog?.window!!.decorView
        helper.uiControls(decorView)
        return fragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBinding.dialogBackground.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dismiss()
                    false
                }

                else -> false
            }
        }
        fragmentBinding.textProject.text = selectedProject.name
        setUpClickListeners()
    }

    // Set up click listeners
    private fun setUpClickListeners() {
        with(fragmentBinding) {
            // Commit project
            btnCommit.setOnClickListener {
                val userUid = helper.getUid()
                val databaseReference = helper.getDatabaseReference()
                databaseReference.child("Users").child(userUid).child("Directories")
                    .child(selectedProject.directoryUid).child("Projects").child(selectedProject.projectUid)
                    .removeValue()
                dismiss()
            }

            // Decline deletion
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