package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.R
import com.example.notefirebase.adapters.DirectoryAdapter
import com.example.notefirebase.adapters.NoteAdapter
import com.example.notefirebase.adapters.ProjectAdapter
import com.example.notefirebase.databinding.FragmentOpenDirectoryBinding
import com.example.notefirebase.firebasemodel.Directory
import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.firebasemodel.Project
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OpenDirectoryFragment(
    private val directoryId: String?,
    private val directoryName: String?
) : Fragment() {
    private lateinit var fragmentBinding: FragmentOpenDirectoryBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var projectAdapter: ProjectAdapter
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentOpenDirectoryBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBinding.nameOfDirectory.setText("Заметки / $directoryName")
        setupRecyclerView()
        val directoryKey = arguments?.getString("directoryKey")
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        setupDatabase()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        projectAdapter = ProjectAdapter()
        fragmentBinding.rcOpenDirectory.adapter = projectAdapter
        fragmentBinding.rcOpenDirectory.layoutManager = LinearLayoutManager(requireContext())

        noteAdapter = NoteAdapter()
        fragmentBinding.rcOpenDirectoryNotes.adapter = noteAdapter
        fragmentBinding.rcOpenDirectoryNotes.layoutManager = LinearLayoutManager(requireContext())
    }

    // Loading data from the database
    private fun setupDatabase() {
        // Get projects
        val userUid = auth.currentUser?.uid ?: ""
        val projectRef = databaseReference.child("Users").child(userUid).child("Directory")
            .child(directoryName!!).child("Projects")
        projectRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val projectList = mutableListOf<Project>()
                for (childSnapshot in dataSnapshot.children) {
                    val project = childSnapshot.getValue(FirebaseDirectory::class.java)
                    if (project != null) {
                        val proj = Project(project.directoryId, project.name)
                        projectList.add(proj)
                    }
                }
                projectAdapter.setProjects(projectList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error", "Не удалось загрузить данные")
            }
        })

        // Get notes
        val noteRef = databaseReference.child("Users").child(userUid).child("Directory")
            .child(directoryName).child("NotesInDirectory")
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

    // Set up Click Listeners
    private fun setupClickListeners() {
        // Show create project fragment
        fragmentBinding.btnAddProject.setOnClickListener {
            val createProject = CreateProjectFragment(directoryId, directoryName)
            createProject.show(
                requireActivity().supportFragmentManager,
                "CreateProjectFragment"
            )
        }

        // Show create note fragment
        fragmentBinding.btnCreateNote.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, CreateNoteFragment(directoryId, directoryName, null, null))
                ?.commit()
        }

        // Go back
        fragmentBinding.btnGoBack.setOnClickListener {
            activity
                ?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragmentHolder, DirectoryFragment())
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

        // Show project
        projectAdapter.setOnClickListener(object : ProjectAdapter.OnClickListener {
            override fun onClick(project: Project) {
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragmentHolder, CreateNoteInProjectFragment(directoryId, directoryName, project.id, project.name))
                    ?.commit()
            }
        })
    }
}

//    companion object {
//        fun newInstance() = OpenDirectoryFragment()
//    }