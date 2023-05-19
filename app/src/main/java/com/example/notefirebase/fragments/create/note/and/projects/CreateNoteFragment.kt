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

        fragmentBinding.btnGoBack.setOnClickListener {
            if (projectId == null) {

                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(
                        R.id.fragmentHolder,
                        OpenDirectoryFragment(directoryId, directoryName)
                    )
                    ?.commit()
            } else {
                activity
                    ?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(
                        R.id.fragmentHolder,
                        CreateNoteInProjectFragment(
                            directoryId,
                            directoryName,
                            projectId,
                            projectName
                        )
                    )
                    ?.commit()
            }
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

        // Button for project creation
        fragmentBinding.btnCommitNote.setOnClickListener {
            if (directoryId != null) {
                // Check data
                if (fragmentBinding.noteContent.text.isNotEmpty()) {
                    val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val noteName = fragmentBinding.noteName.text.toString()
                    val noteContent = fragmentBinding.noteContent.text.toString()

                    val email = FirebaseAuth.getInstance().currentUser?.email
                    val parts = email?.split("@")
                    val userEmail = parts!![0]

                    writeNoteIntoDir(directoryId, userUid, noteName, noteContent)

                    activity
                        ?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(
                            R.id.fragmentHolder,
                            OpenDirectoryFragment(directoryId, directoryName)
                        )
                        ?.commit()

                } else {
                    Toast.makeText(context, "Введите название своего проекта", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                if (fragmentBinding.noteContent.text.isNotEmpty()) {
                    val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val noteName = fragmentBinding.noteName.text.toString()
                    val noteContent = fragmentBinding.noteContent.text.toString()
                    if (projectId != null) {
                        writeNoteIntoProject(projectId, userUid, noteName, noteContent)
                    }

                    activity
                        ?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(
                            R.id.fragmentHolder,
                            CreateNoteInProjectFragment(
                                directoryId,
                                directoryName,
                                projectId,
                                projectName
                            )
                        )
                        ?.commit()
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

    fun getFirstTwoWords(input: String): String {
        val words = input.trim().split("\\s+".toRegex()) // Разделение строки на слова
        val firstTwoWords = words.take(2) // Получение первых двух слов
        return firstTwoWords.joinToString(" ") // Объединение первых двух слов в строку
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