package com.example.notefirebase.utils

import android.util.Log
import android.widget.Toast
import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.example.notefirebase.firebasemodel.FirebaseIncomes
import com.example.notefirebase.firebasemodel.FirebaseNoteInDir
import com.example.notefirebase.firebasemodel.FirebaseOutcomes
import com.example.notefirebase.firebasemodel.FirebaseProject
import com.example.notefirebase.firebasemodel.Income
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.firebasemodel.Outcome
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Date

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

    // Method for get project
    fun getProjectList(
        userUid: String, directoryName: String?, listener: ValueEventListener
    ) {
        val projectRef = databaseReference.child("Users").child(userUid).child("Directory")
            .child(directoryName!!).child("Projects")
        projectRef.addValueEventListener(listener)
    }

    // Method for get note inside directory
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

    // Method for get data from note inside the project
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

    // Method for get current data
    private fun getCurrentYearAndMonth(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        return Pair(currentYear, currentMonth)
    }

    // Method for writing income
    fun writeIncome(incomeAmount: Double, incomeName: String, userUid: String) {
        val (year, month) = getCurrentYearAndMonth()

        val income = FirebaseIncomes(incomeName, incomeAmount, "$year-$month")
        val uniqueId = databaseReference.push().key
        databaseReference.child("Users").child(userUid).child("Incomes").child(uniqueId!!)
            .setValue(income)
    }

    // Method for get income
    fun getIncome(
        userUid: String,
        callbackList: (List<Income>) -> Unit,
        callbackAmount: (Double) -> Unit,
        errorCallback: () -> Unit
    ) {
        val incomeRef = databaseReference.child("Users").child(userUid).child("Incomes")
        incomeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalIncomeAmount = 0.0
                val incomeList = mutableListOf<Income>()
                Log.d("MyLog", "$dataSnapshot")
                for (childSnapshot in dataSnapshot.children) {
                    val income = childSnapshot.getValue(FirebaseIncomes::class.java)
                    if (income != null) {
                        val inc = income.incomeAmount?.let { Income(income.incomeName, it) }
                        if (inc != null) {
                            incomeList.add(inc)
                            totalIncomeAmount += inc.incomeAmount
                        }
                    }
                }
                callbackList(incomeList)
                callbackAmount(totalIncomeAmount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
               errorCallback()
            }
        })
    }

    // Method for writing outcome
    fun writeOutcome(outcomeAmount: Double, outcomeName: String, userUid: String) {
        val (year, month) = getCurrentYearAndMonth()

        val outcome = FirebaseOutcomes(outcomeName, outcomeAmount, "$year-$month")
        val uniqueId = databaseReference.push().key
        databaseReference.child("Users").child(userUid).child("Outcomes").child(uniqueId!!)
            .setValue(outcome)
    }

    // Method for get outcome
    fun getOutcome(
        userUid: String,
        callbackList: (List<Outcome>) -> Unit,
        callbackAmount: (Double) -> Unit,
        errorCallback: () -> Unit
    ) {
        val incomeRef = databaseReference.child("Users").child(userUid).child("Outcomes")
        incomeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalOutcomeAmount = 0.0
                val outcomeList = mutableListOf<Outcome>()
                Log.d("MyLog", "$dataSnapshot")
                for (childSnapshot in dataSnapshot.children) {
                    val outcome = childSnapshot.getValue(FirebaseOutcomes::class.java)
                    if (outcome != null) {
                        val inc = outcome.outcomeAmount?.let { Outcome(outcome.outcomeName, it) }
                        if (inc != null) {
                            outcomeList.add(inc)
                            totalOutcomeAmount += inc.outcomeAmount
                        }
                    }
                }
                callbackList(outcomeList)
                callbackAmount(totalOutcomeAmount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                errorCallback()
            }
        })
    }
}
