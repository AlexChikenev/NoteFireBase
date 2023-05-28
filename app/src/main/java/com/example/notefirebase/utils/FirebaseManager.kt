package com.example.notefirebase.utils

import android.util.Log
import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.example.notefirebase.firebasemodel.FirebaseIncomes
import com.example.notefirebase.firebasemodel.FirebaseNoteInDir
import com.example.notefirebase.firebasemodel.FirebaseOutcomes
import com.example.notefirebase.firebasemodel.FirebasePillows
import com.example.notefirebase.firebasemodel.FirebaseProject
import com.example.notefirebase.firebasemodel.Income
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.firebasemodel.Outcome
import com.example.notefirebase.firebasemodel.Pillow
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

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
    fun getCurrentYearAndMonth(): Pair<Int, Int> {
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
                        val inc = income.incomeAmount?.let {
                            Income(
                                income.incomeName, it, income.incomeDate
                            )
                        }
                        if (inc != null) {
                            incomeList.add(inc)
                            totalIncomeAmount += inc.incomeAmount
                        }
                    }
                }
                callbackList(incomeList)
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
                        val inc = outcome.outcomeAmount?.let {
                            Outcome(
                                outcome.outcomeName, it, outcome.incomeDate
                            )
                        }
                        if (inc != null) {
                            outcomeList.add(inc)
                        }
                    }
                }
                callbackList(outcomeList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                errorCallback()
            }
        })
    }

    // Calculate incomes per month
    fun calculateMonthlyIncomes(incomes: List<Income>): Map<String, Double> {
        val monthlyIncomes = mutableMapOf<String, Double>()
        for (income in incomes) {
            val incomeDate = income.incomeDate
            if (monthlyIncomes.containsKey(incomeDate)) {
                val currentAmount = monthlyIncomes[incomeDate] ?: 0.0
                monthlyIncomes[incomeDate] = currentAmount + income.incomeAmount
            } else {
                monthlyIncomes[incomeDate] = income.incomeAmount
            }
        }
        return monthlyIncomes

    }

    // Calculate outcomes per month
    fun calculateMonthlyOutcomes(outcomes: List<Outcome>): Map<String, Double> {
        val monthlyOutcomes = mutableMapOf<String, Double>()
        for (outcome in outcomes) {
            val outcomeDate = outcome.outcomeDate
            if (monthlyOutcomes.containsKey(outcomeDate)) {
                val currentAmount = monthlyOutcomes[outcomeDate] ?: 0.0
                monthlyOutcomes[outcomeDate] = currentAmount + outcome.outcomeAmount
            } else {
                monthlyOutcomes[outcomeDate] = outcome.outcomeAmount
            }
        }
        return monthlyOutcomes
    }

    // Write pillow
    fun writePillow(
        pillowAmount: Double, userUid: String
    ) {
        val (year, month) = getCurrentYearAndMonth()
        val formattedDate = "$year-$month"
        val pillow = FirebasePillows(pillowAmount, formattedDate)
        databaseReference.child("Users").child(userUid).child("Pillow").setValue(pillow)

    }

    // Get pillow
    fun getPillow(
        userUid: String,
        callbackPillow: (Double) -> Unit,
        callbackZero: (Double) -> Unit,
        errorCallback: () -> Unit
    ) {
        val (year, month) = getCurrentYearAndMonth()
        val formattedDate = "$year-$month"
        val pillowRef = databaseReference.child("Users").child(userUid).child("Pillow")
        pillowRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var pillow = 0.0
                for (childSnapshot in dataSnapshot.children) {
                    val pillowData = dataSnapshot.getValue(FirebasePillows::class.java)
                    if (pillowData != null) {
                        if (pillowData.pillowAmount != null && pillowData.date == formattedDate) pillow =
                            pillowData.pillowAmount
                        else callbackZero(0.0)
                    }
                }
                callbackPillow(pillow)
            }

            override fun onCancelled(error: DatabaseError) {
                errorCallback()
            }
        })
    }
}