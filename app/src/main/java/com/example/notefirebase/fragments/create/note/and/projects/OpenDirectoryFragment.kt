package com.example.notefirebase.fragments.create.note.and.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notefirebase.adapters.NoteAdapter
import com.example.notefirebase.adapters.ProjectAdapter
import com.example.notefirebase.databinding.FragmentOpenDirectoryBinding
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.firebasemodel.Project
import com.example.notefirebase.fragments.MainFragment
import com.example.notefirebase.fragments.settings.MainSettingsFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.firebase.auth.FirebaseAuth

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
        auth = FirebaseAuth.getInstance()
        setupDatabase()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        projectAdapter = ProjectAdapter(requireActivity())
        fragmentBinding.rcOpenDirectory.adapter = projectAdapter
        fragmentBinding.rcOpenDirectory.layoutManager = LinearLayoutManager(requireContext())

        noteAdapter = NoteAdapter(requireActivity(), 0)
        fragmentBinding.rcOpenDirectoryNotes.adapter = noteAdapter
        fragmentBinding.rcOpenDirectoryNotes.layoutManager = LinearLayoutManager(requireContext())
    }

    // Loading data from the database
    private fun setupDatabase() {
        if (directoryId != null) {
            // Get projects
            firebaseManager.getProject(helper.getUid(), directoryId, {
                projectAdapter.setProjects(it)
            }, {
                Log.d("Error", "Не удалось загрузить данные")
            })

            // Get notes
            firebaseManager.getNote(0, helper.getUid(), null, directoryId, {
                Log.d("Note", "$it")
                noteAdapter.setNotes(it)
            }, {
                Log.d("Error", "Не удалось загрузить данные")
            })
        }
    }

    // Set up Click Listeners
    private fun setupClickListeners() {
        with(fragmentBinding) {
            // Show create project fragment
            btnAddProject.setOnClickListener {
                val createProject = CreateProjectFragment(directoryId)
                createProject.show(
                    requireActivity().supportFragmentManager, "CreateProjectFragment"
                )
            }

            // Show create note fragment
            btnCreateNote.setOnClickListener {
                helper.navigate(CreateNoteFragment(0, directoryId, directoryName, null, null, null))
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
                    helper.navigate(
                        CreateNoteInProjectFragment(
                            directoryId, directoryName, project.projectUid, project.name
                        )
                    )
                }
            })

            // Show note
            noteAdapter.setOnClickListener(object : NoteAdapter.OnClickListener {
                override fun onClick(note: Note) {
                    helper.navigate(CreateNoteFragment(0, note.directoryUid, directoryName, note.projectUid, null, note.noteUid))
                }
            })
        }
    }
}