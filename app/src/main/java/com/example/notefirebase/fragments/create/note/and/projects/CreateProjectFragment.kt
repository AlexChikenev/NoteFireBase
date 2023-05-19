package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.notefirebase.fragments.create.note.and.projects.OpenDirectoryFragment
import com.example.notefirebase.databinding.FragmentCreateProjectBinding
import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.example.notefirebase.firebasemodel.FirebaseProject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.util.UUID

class CreateProjectFragment(
    private val directoryId: String?,
    private val directoryName: String?
) : DialogFragment() {
    private lateinit var binding: FragmentCreateProjectBinding
    private lateinit var databaseReference: DatabaseReference
    // private lateinit var userUid: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateProjectBinding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button for project creation
        binding.btnCommitProject.setOnClickListener {
            // Check data
            if (binding.projectName.text.isNotEmpty()) {
                val projectId = UUID.randomUUID().toString()
                val directoryId = directoryId
                val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val projectName = binding.projectName.text.toString()

                val email = FirebaseAuth.getInstance().currentUser?.email
                val parts = email?.split("@")
                val userEmail = parts!![0]

                writeProject(projectId, directoryId!!, userUid, projectName)
                dismiss()
            } else {
                Toast.makeText(context, "Введите название своего проекта", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun writeProject(
        projectId: String,
        directoryId: String,
        userUid: String,
        projectName: String
    ) {
        val project = FirebaseProject(projectId, directoryId, userUid, projectName)
        databaseReference.child("Users").child(userUid).child("Directory").child(directoryName!!)
            .child("Projects").child(projectName)
            .setValue(project)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1000)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }
}