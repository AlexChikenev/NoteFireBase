package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.R
import com.example.notefirebase.adapters.NoteAdapter
import com.example.notefirebase.adapters.ProjectAdapter
import com.example.notefirebase.databinding.FragmentOpenDirectoryBinding
import com.example.notefirebase.firebasemodel.FirebaseNoteInDir
import com.example.notefirebase.firebasemodel.FirebaseProject
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.firebasemodel.Project
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class OpenDirectoryFragment(
    private val directoryId: String?, private val directoryName: String?
) : Fragment() {
    private lateinit var fragmentBinding: FragmentOpenDirectoryBinding
    private lateinit var projectAdapter: ProjectAdapter
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var helper: Helper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentOpenDirectoryBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helper = Helper(requireActivity())
        firebaseManager = FirebaseManager()
        fragmentBinding.nameOfDirectory.setText("Заметки / $directoryName")
        setupRecyclerView()
        val directoryKey = arguments?.getString("directoryKey")
        auth = FirebaseAuth.getInstance()
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
        firebaseManager.getProjectList(userUid, directoryName, object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val projectList = mutableListOf<Project>()
                for (childSnapshot in dataSnapshot.children) {
                    val project = childSnapshot.getValue(FirebaseProject::class.java)
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
        firebaseManager.getNoteList(userUid, directoryName!!, object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val noteList = mutableListOf<Note>()
                for (childSnapshot in dataSnapshot.children) {
                    val note = childSnapshot.getValue(FirebaseNoteInDir::class.java)
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
        with(fragmentBinding) {
            // Show create project fragment
            btnAddProject.setOnClickListener {
                val createProject = CreateProjectFragment(directoryId, directoryName)
                createProject.show(
                    requireActivity().supportFragmentManager, "CreateProjectFragment"
                )
            }

            // Show create note fragment
            btnCreateNote.setOnClickListener {
                helper.navigate(CreateNoteFragment(directoryId, directoryName, null, null))
            }

            // Go back
            btnGoBack.setOnClickListener {
                helper.navigate(DirectoryFragment())
            }

            // Go to main
            btnToMain.setOnClickListener {
                helper.navigate(MainFragment())
            }

            // Show settings
            btnToSettings.setOnClickListener {
                helper.navigate(MainSettingsFragment())
            }

            // Show project
            projectAdapter.setOnClickListener(object : ProjectAdapter.OnClickListener {
                override fun onClick(project: Project) {
                    activity?.supportFragmentManager?.beginTransaction()?.replace(
                        R.id.fragmentHolder, CreateNoteInProjectFragment(
                            directoryId, directoryName, project.id, project.name
                        )
                    )?.commit()
                }
            })
        }
    }
}

//    companion object {
//        fun newInstance() = OpenDirectoryFragment()
//    }