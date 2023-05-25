package com.example.notefirebase.utils

import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.example.notefirebase.firebasemodel.FirebaseNoteInDir
import com.example.notefirebase.firebasemodel.FirebaseProject
import com.example.notefirebase.firebasemodel.Note
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseManager {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Method for creating directory
    fun writeDirectory(directoryId: String, userUid: String, name: String) {
        val directory = FirebaseDirectory(directoryId, userUid, name)
        databaseReference.child("Users").child(userUid).child("Directory").child(name)
            .setValue(directory)
    }

    // Method for creating project
    fun writeProject(
        directoryName: String,
        projectId: String,
        directoryId: String,
        userUid: String,
        projectName: String
    ) {
        val project = FirebaseProject(projectId, directoryId, userUid, projectName)
        val projectRef =
            databaseReference.child("Users").child(userUid).child("Directory").child(directoryName)
                .child("Projects").child(projectName)
        projectRef.setValue(project)
    }

    // Get project
    fun getProjectList(
        userUid: String, directoryName: String?, listener: ValueEventListener
    ) {
        val projectRef = databaseReference.child("Users").child(userUid).child("Directory")
            .child(directoryName!!).child("Projects")
        projectRef.addValueEventListener(listener)
    }

    // Get note
    fun getNoteList(
        userUid: String, directoryName: String, listener: ValueEventListener
    ) {
        val noteRef =
            databaseReference.child("Users").child(userUid).child("Directory").child(directoryName)
                .child("NotesInDirectory")
        noteRef.addValueEventListener(listener)
    }

    // Method for writing data inside a folder
    fun writeNoteIntoDir(
        directoryId: String,
        userUid: String,
        directoryName: String,
        noteName: String,
        noteContent: String
    ) {
        val note = FirebaseNoteInDir(directoryId, userUid, noteName, noteContent)
        databaseReference.child("Users").child(userUid).child("Directory").child(directoryName)
            .child("NotesInDirectory").child(noteName).setValue(note)
    }

    // Method for writing data inside the project
    fun writeNoteIntoProject(
        projectId: String,
        userUid: String,
        directoryName: String?,
        projectName: String?,
        noteName: String,
        noteContent: String
    ) {
        val note = FirebaseNoteInDir(projectId, userUid, noteName, noteContent)
        directoryName?.let {
            projectName?.let { projectName ->
                databaseReference.child("Users").child(userUid).child("Directory").child(it)
                    .child("Projects").child(projectName).child("NotesInProject").child(noteName)
                    .setValue(note)
            }
        }
    }

    // Get notes
    fun getNotesInProject(
        userUid: String,
        directoryName: String,
        projectName: String,
        callback: (List<Note>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val noteRef =
            databaseReference.child("Users").child(userUid).child("Directory").child(directoryName)
                .child("Projects").child(projectName).child("NotesInProject")
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
                callback(noteList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                errorCallback()
            }
        })
    }
}