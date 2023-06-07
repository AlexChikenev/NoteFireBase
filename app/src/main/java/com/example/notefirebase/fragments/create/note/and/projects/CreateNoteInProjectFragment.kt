package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.adapters.NoteAdapter
import com.example.notefirebase.databinding.FragmentCreateNoteInProjectBinding
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateNoteInProjectFragment(
    private val directoryUid: String?,
    private val directoryName: String?,
    private val projectUid: String?,
    private val projectName: String?
) : Fragment() {

    private lateinit var fragmentBinding: FragmentCreateNoteInProjectBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var helper: Helper
    private lateinit var firebaseManager: FirebaseManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateNoteInProjectBinding.inflate(inflater, container, false)
        helper = Helper(requireActivity())
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseManager = FirebaseManager()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBinding.nameOfDirectory.setText("Заметки / $directoryName / $projectName")
        setupRecyclerView()
        setupDatabase()
        setupClickListeners()
    }

    private fun setupDatabase() {
        // Get notes in project
        if (directoryUid != null) {
            firebaseManager.getNote(1, helper.getUid(), projectUid, directoryUid, {
                Log.d("it", "$it")
                noteAdapter.setNotes(it)
            }, {
                Log.d("Error", "Не удалось загрузить данные")
            })
        }
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(requireActivity(), 1)
        fragmentBinding.rcOpenDirectoryNotes.adapter = noteAdapter
        fragmentBinding.rcOpenDirectoryNotes.layoutManager = LinearLayoutManager(requireContext())
    }

    // Set up Click Listeners
    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Create note
            btnCreateNote.setOnClickListener {
                helper.navigate(
                    CreateNoteFragment(
                        1,
                        directoryUid,
                        directoryName,
                        projectUid,
                        projectName,
                        null
                    )
                )
            }

            // Go back
            btnGoBack.setOnClickListener {
                helper.navigate(OpenDirectoryFragment(directoryUid, directoryName))
            }

            // Go to main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }

            // Show settings
            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }

            noteAdapter.setOnClickListener(object : NoteAdapter.OnClickListener {
                override fun onClick(note: Note) {
                    helper.navigate(
                        CreateNoteFragment(
                            1,
                            note.directoryUid,
                            directoryName,
                            note.projectUid,
                            projectName,
                            note.noteUid
                        )
                    )
                }
            })
        }
    }
}