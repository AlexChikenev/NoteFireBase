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
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateNoteInProjectFragment(
    private val directoryId: String?,
    private val directoryName: String?,
    private val projectId: String?,
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
        val userUid = auth.currentUser?.uid ?: ""
        firebaseManager.getNotesInProject(userUid, directoryName!!, projectName!!, { noteList ->
            noteAdapter.setNotes(noteList)
        }, {
            Log.d("Error", "Не удалось загрузить данные")
        })
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter()
        fragmentBinding.rcOpenDirectoryNotes.adapter = noteAdapter
        fragmentBinding.rcOpenDirectoryNotes.layoutManager = LinearLayoutManager(requireContext())
    }

    // Set up Click Listeners
    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Create note
            btnCreateNote.setOnClickListener {
                helper.navigate(CreateNoteFragment(null, directoryName, projectId, projectName))
            }

            // Go back
            btnGoBack.setOnClickListener {
                helper.navigate(OpenDirectoryFragment(directoryId, directoryName))
            }

            // Go to main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }

            // Show settings
            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }
        }
    }

//    companion object {
//        fun newInstance() = CreateNoteInProjectFragment()
//    }
}