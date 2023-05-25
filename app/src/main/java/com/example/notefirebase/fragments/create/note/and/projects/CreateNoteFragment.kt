package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateNoteBinding
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth

class CreateNoteFragment(
    private val directoryId: String?,
    private val directoryName: String?,
    private val projectId: String?,
    private val projectName: String?
) : Fragment() {

    private lateinit var fragmentBinding: FragmentCreateNoteBinding
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateNoteBinding.inflate(inflater, container, false)
        firebaseManager = FirebaseManager()
        helper = Helper(requireActivity())
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    // Set up Click Listeners
    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Go back
            btnGoBack.setOnClickListener {
                if (projectId == null) {
                    helper.navigate(OpenDirectoryFragment(directoryId, directoryName))
                } else {
                    helper.navigate(
                        CreateNoteInProjectFragment(
                            directoryId, directoryName, projectId, projectName
                        )
                    )
                }
            }

            // Go to main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }

            // Show settings
            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }

            // Button for project creation
            btnCommitNote.setOnClickListener {
                val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                var noteName = noteName.text.toString()
                val noteContent = noteContent.text.toString()

                // If directoryId is not null
                if (directoryId != null) {
                    // Check data
                    if (noteContent.isNotEmpty()) {
                        if (noteName.isEmpty()) {
                            val newName = noteContent.split(" ")
                            noteName = newName[0]
                            firebaseManager.writeNoteIntoDir(
                                directoryId, userUid, directoryName!!, noteName, noteContent
                            )
                        } else {
                            firebaseManager.writeNoteIntoDir(
                                directoryId, userUid, directoryName!!, noteName, noteContent
                            )
                        }
                        helper.navigate(OpenDirectoryFragment(directoryId, directoryName))
                    }
                }// If projectId is not null
                else {
                    if (noteContent.isNotEmpty()) {
                        if (projectId != null) {
                            if (noteName.isEmpty()) {
                                val newName = noteContent.split(" ")
                                noteName = newName[0]
                                firebaseManager.writeNoteIntoProject(
                                    projectId,
                                    userUid,
                                    directoryName,
                                    projectName,
                                    noteName,
                                    noteContent
                                )
                            } else {
                                directoryName?.let { it1 ->
                                    firebaseManager.writeNoteIntoDir(
                                        projectId, userUid, it1, noteName, noteContent
                                    )
                                }
                            }
                        }
                        helper.navigate(
                            CreateNoteInProjectFragment(
                                projectId, directoryName, projectId, projectName
                            )
                        )
                    } else {
                        Toast.makeText(
                            context, R.string.enter_project_name, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


//    companion object {
//        fun newInstance() = CreateNoteFragment(null)
//    }
}