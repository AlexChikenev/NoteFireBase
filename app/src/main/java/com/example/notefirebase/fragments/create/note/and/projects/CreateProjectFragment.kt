package com.example.notefirebase.fragments.create.note.and.projects

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateProjectBinding
import com.example.notefirebase.utils.FirebaseManager
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class CreateProjectFragment(
    private val directoryId: String?, private val directoryName: String?
) : DialogFragment() {
    private lateinit var fragmentBinding: FragmentCreateProjectBinding
    private lateinit var firebaseManager: FirebaseManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateProjectBinding.inflate(inflater, container, false)
        firebaseManager = FirebaseManager()
        return fragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        fragmentBinding.dialogBackground.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dismiss()
                    false
                }

                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Button for project creation
            btnCommitProject.setOnClickListener {
                // Check data
                if (inputProjectName.text.isNotEmpty()) {
                    val projectId = UUID.randomUUID().toString()
                    val directoryId = directoryId
                    val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val projectName = inputProjectName.text.toString()

                    firebaseManager.writeProject(
                        directoryName!!, projectId, directoryId!!, userUid, projectName
                    )
                    dismiss()
                } else {
                    Toast.makeText(context, "Введите название своего проекта", Toast.LENGTH_SHORT)
                        .show()
                }
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