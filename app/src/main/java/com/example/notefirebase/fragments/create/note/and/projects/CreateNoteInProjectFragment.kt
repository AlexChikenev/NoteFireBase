package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.R
import com.example.notefirebase.adapters.NoteAdapter
import com.example.notefirebase.databinding.FragmentCreateNoteInProjectBinding
import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCreateNoteInProjectBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBinding.nameOfDirectory.setText("Заметки / $directoryName / $projectName")
        setupRecyclerView()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        setupDatabase()
        setupClickListeners()
    }

    private fun setupDatabase() {
        // Get notes
        val userUid = auth.currentUser?.uid ?: ""
        val noteRef = databaseReference.child("Users").child(userUid).child("Directory")
            .child(directoryName!!).child("Projects").child(projectName!!).child("NotesInProject")
        noteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val noteList = mutableListOf<Note>()
                for (childSnapshot in dataSnapshot.children) {
                    val note = childSnapshot.getValue(FirebaseDirectory::class.java)
                    if (note != null) {
                        val n = Note(note.name)
                        noteList.add(n)
                    }
                }
                noteAdapter.setNotes(noteList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error", "Не удалось загрузить данные")
            }
        })
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter()
        fragmentBinding.rcOpenDirectoryNotes.adapter = noteAdapter
        fragmentBinding.rcOpenDirectoryNotes.layoutManager = LinearLayoutManager(requireContext())
    }

    // Set up Click Listeners
    private fun setupClickListeners() {

        // Create note
        fragmentBinding.btnCreateNote.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, CreateNoteFragment(null, directoryName, projectId, projectName))
                ?.commit()
        }

        // Go back
        fragmentBinding.btnGoBack.setOnClickListener{
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, OpenDirectoryFragment(directoryId, directoryName))
                ?.commit()
        }

        // Go to main
        fragmentBinding.btnToMain.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, MainFragment())
                ?.commit()
        }

        // Show settings
        fragmentBinding.btnToSettings.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, MainSettingsFragment())
                ?.commit()
        }
    }

//    companion object {
//        fun newInstance() = CreateNoteInProjectFragment()
//    }
}