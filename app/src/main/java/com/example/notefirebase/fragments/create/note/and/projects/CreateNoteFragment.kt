package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notefirebase.R
import com.example.notefirebase.databinding.FragmentCreateNoteBinding
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateNoteFragment(
    private val noteType: Int,
    private val directoryId: String?,
    private val directoryName: String?,
    private val projectUid: String?,
    private val projectName: String?,
    private val noteUid: String?,
) : Fragment() {

    private lateinit var fragmentBinding: FragmentCreateNoteBinding
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var helper: Helper
    private var note: List<Note> = emptyList()

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
        setUpDatabase()
    }

    private fun setUpDatabase() {
        when (noteType) {
            0 -> {
                if (directoryId != null) {
                    if (noteUid != null) {
                        firebaseManager.getNoteForEdit(
                            0,
                            noteUid,
                            helper.getUid(),
                            projectUid,
                            directoryId,
                            {notes ->
                                if (notes.isNotEmpty()) {
                                    note = notes
                                    setUpUi(note[0])
                                }
                            },
                            {
                                Log.d("Error", "Не удалось загрузить данные")
                            })
                    }
                }
            }

            1 -> {
                if (directoryId != null) {
                    if (noteUid != null) {
                        firebaseManager.getNoteForEdit(
                            1,
                            noteUid,
                            helper.getUid(),
                            projectUid,
                            directoryId,
                            {notes ->
                                if (notes.isNotEmpty()) {
                                    note = notes
                                    setUpUi(note[0])
                                }
                            },
                            {
                                Log.d("Error", "Не удалось загрузить данные")
                            })
                    }
                }
            }
        }
    }

    private fun setUpUi(note: Note) {
        with(fragmentBinding) {
            noteName.setText(note.noteName)
            noteContent.setText(note.noteContent)
        }
    }

    // Set up Click Listeners
    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Go back
            btnGoBack.setOnClickListener {
                if (noteType == 0) {
                    helper.navigate(OpenDirectoryFragment(directoryId, directoryName))
                } else {
                    helper.navigate(
                        CreateNoteInProjectFragment(
                            directoryId, directoryName, projectUid, projectName
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
                val userUid = helper.getUid()
                Log.d("userUid", userUid)
                var noteName = noteName.text.toString()
                val noteContent = noteContent.text.toString()
                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

                val uniqueId: String? = noteUid ?: databaseReference.push().key

                if (noteContent != "") {
                    // If note creates in directory
                    when (noteType) {
                        // Crete note in directory
                        0 -> {
                            if (noteName.isNotEmpty()) {
                                directoryId?.let { directoryId ->
                                    firebaseManager.writeNote(
                                        uniqueId!!,
                                        noteType,
                                        userUid,
                                        projectUid,
                                        directoryId,
                                        noteName,
                                        noteContent
                                    )
                                }
                            } else {
                                val newName = noteContent.split(" ")
                                noteName = newName[0]
                                directoryId?.let { directoryId ->
                                    firebaseManager.writeNote(
                                        uniqueId!!,
                                        noteType,
                                        userUid,
                                        projectUid,
                                        directoryId,
                                        noteName,
                                        noteContent
                                    )
                                }
                            }
                            helper.navigate(OpenDirectoryFragment(directoryId, directoryName))
                        }

                        // Create note in project
                        1 -> {
                            if (noteName.isNotEmpty()) {
                                directoryId?.let { directoryId ->
                                    if (projectUid != null) {
                                        firebaseManager.writeNote(
                                            uniqueId!!,
                                            noteType,
                                            userUid,
                                            projectUid,
                                            directoryId,
                                            noteName,
                                            noteContent
                                        )
                                    }
                                }
                            } else {
                                val newName = noteContent.split(" ")
                                noteName = newName[0]
                                directoryId?.let { directoryId ->
                                    firebaseManager.writeNote(
                                        uniqueId!!,
                                        noteType,
                                        userUid,
                                        projectUid,
                                        directoryId,
                                        noteName,
                                        noteContent
                                    )
                                }
                            }
                            helper.navigate(
                                CreateNoteInProjectFragment(
                                    directoryId,
                                    directoryName,
                                    projectUid,
                                    projectName
                                )
                            )
                        }
                    }
                } else {
                    helper.customToast(requireContext(), R.string.input_note_content)
                }
            }
        }
    }
}