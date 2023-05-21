package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateNoteBinding
import com.example.notefirebase.firebasemodel.FirebaseNoteInDir
import com.example.notefirebase.firebasemodel.FirebaseProject
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.util.UUID

class CreateNoteFragment(
    private val directoryId: String?,
    private val directoryName: String?,
    private val projectId: String?,
    private val projectName: String?
) :
    Fragment() {

    private lateinit var fragmentBinding: FragmentCreateNoteBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateNoteBinding.inflate(inflater, container, false)
        databaseReference = FirebaseDatabase.getInstance().reference
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    // Set up Click Listeners
    private fun setupClickListeners() {
        with(fragmentBinding) {
            btnGoBack.setOnClickListener {
                if (projectId == null) {
                    updateUI(OpenDirectoryFragment(directoryId, directoryName))
                } else {
                    updateUI(
                        CreateNoteInProjectFragment(
                            directoryId,
                            directoryName,
                            projectId,
                            projectName
                        )
                    )
                }
            }

            // Go to main
            btnToMain.setOnClickListener {
                updateUI(MainFragment())
            }

            // Show settings
            btnToSettings.setOnClickListener {
                updateUI(MainSettingsFragment())
            }

            // Button for project creation
            btnCommitNote.setOnClickListener {
                val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                var noteName = noteName.text.toString()
                val noteContent = noteContent.text.toString()

                if (directoryId != null) {
                    // Check data
                    if (noteContent.isNotEmpty()) {
                        if (noteName.isEmpty()) {
                            val newName = noteContent.split(" ")
                            noteName = newName[0]
                            writeNoteIntoDir(directoryId, userUid, noteName, noteContent)
                        } else {
                            writeNoteIntoDir(directoryId, userUid, noteName, noteContent)
                        }
                        updateUI(OpenDirectoryFragment(directoryId, directoryName))
                    } else {
                        Toast.makeText(
                            context,
                            "Введите название своего проекта",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    if (noteContent.isNotEmpty()) {
                        if (projectId != null) {
                            if (noteName.isEmpty()) {
                                val newName = noteContent.split(" ")
                                noteName = newName[0]
                                writeNoteIntoProject(projectId, userUid, noteName, noteContent)
                            } else {
                                writeNoteIntoDir(projectId, userUid, noteName, noteContent)
                            }
                        }
                        updateUI(
                            CreateNoteInProjectFragment(
                                projectId,
                                directoryName,
                                projectId,
                                projectName
                            )
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Введите название своего проекта",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }
    }

    private fun updateUI(fragment: Fragment) {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(
                R.id.fragmentHolder,
                fragment
            )
            ?.commit()
    }

    private fun writeNoteIntoDir(
        directoryId: String,
        userUid: String,
        noteName: String,
        noteContent: String
    ) {
        val note = FirebaseNoteInDir(directoryId, userUid, noteName, noteContent)
        databaseReference.child("Users").child(userUid).child("Directory").child(directoryName!!)
            .child("NotesInDirectory").child(noteName).setValue(note)
    }

    private fun writeNoteIntoProject(
        projectId: String,
        userUid: String,
        noteName: String,
        noteContent: String
    ) {
        val note = FirebaseNoteInDir(projectId, userUid, noteName, noteContent)
        if (projectName != null) {
            if (directoryName != null) {
                databaseReference.child("Users").child(userUid).child("Directory")
                    .child(directoryName)
                    .child("Projects").child(projectName).child("NotesInProject").child(noteName)
                    .setValue(note)
            }
        }
    }

//    companion object {
//        fun newInstance() = CreateNoteFragment(null)
//    }
}