package com.example.notefirebase.utils

import android.util.Log
import com.example.notefirebase.firebasemodel.Directory
import com.example.notefirebase.firebasemodel.FirebaseDirectory
import com.example.notefirebase.firebasemodel.FirebaseIncomes
import com.example.notefirebase.firebasemodel.FirebaseNoteInDir
import com.example.notefirebase.firebasemodel.FirebaseOutcomes
import com.example.notefirebase.firebasemodel.FirebasePillows
import com.example.notefirebase.firebasemodel.FirebaseProject
import com.example.notefirebase.firebasemodel.FirebaseTask
import com.example.notefirebase.firebasemodel.Income
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.firebasemodel.Outcome
import com.example.notefirebase.firebasemodel.Project
import com.example.notefirebase.firebasemodel.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class FirebaseManager {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Method for creating directory
    fun writeDirectory(userUid: String, directoryName: String) {
        val uniqueId = databaseReference.push().key
        val directory = uniqueId?.let { FirebaseDirectory(it, directoryName) }
        if (uniqueId != null) {
            databaseReference.child("Users").child(userUid).child("Directories").child(uniqueId)
                .setValue(directory)
        }
    }

    // Method for getting directory
    fun getDirectory(
        userUid: String,
        callbackList: (List<Directory>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val directoryRef = databaseReference.child("Users").child(userUid).child("Directories")
        directoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val directoryList = mutableListOf<Directory>()
                for (childSnapshot in dataSnapshot.children) {
                    val directory = childSnapshot.getValue(FirebaseDirectory::class.java)
                    if (directory != null) {
                        val dir = Directory(directory.directoryUniqueId, directory.directoryName)
                        directoryList.add(dir)
                    }
                }
                callbackList(directoryList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                errorCallback()
            }
        })
    }

    // Method for creating project
    fun writeProject(
        directoryUid: String,
        userUid: String,
        projectName: String
    ) {
        val uniqueId = databaseReference.push().key
        val project = uniqueId?.let { FirebaseProject(it, directoryUid, projectName) }
        uniqueId?.let {
            databaseReference.child("Users").child(userUid).child("Directories").child(directoryUid)
                .child("Projects").child(it).setValue(project)
        }
    }


    // Method for getting project
    fun getProject(
        userUid: String,
        directoryUid: String,
        callbackList: (List<Project>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val projectRef =
            databaseReference.child("Users").child(userUid).child("Directories").child(directoryUid)
                .child("Projects")

        projectRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val projectList = mutableListOf<Project>()
                for (childSnapshot in dataSnapshot.children) {
                    val project = childSnapshot.getValue(FirebaseProject::class.java)
                    if (project != null) {
                        val dir = Project(project.projectUid, project.directoryUid, project.name)
                        projectList.add(dir)
                    }
                }
                callbackList(projectList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                errorCallback()
            }
        })
    }

    // Method for get note
    fun getNote(
        noteType: Int,
        userUid: String,
        projectUid: String?,
        directoryUid: String,
        callbackList: (List<Note>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val ref =
            databaseReference.child("Users").child(userUid).child("Directories").child(directoryUid)

        val noteRef: DatabaseReference = if (noteType == 0) {
            ref.child("NotesInDirectory")
        } else {
            ref.child("Projects").child(projectUid!!).child("NoteInProject")
        }
        noteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val noteList = mutableListOf<Note>()
                for (childSnapshot in dataSnapshot.children) {
                    val noteData = childSnapshot.getValue(FirebaseNoteInDir::class.java)
                    if (noteData != null) {
                        val note = noteData.name?.let { noteName ->
                            noteData.content?.let { noteContent ->
                                Note(
                                    noteData.directoryUid, projectUid, noteData.noteUid,
                                    noteName, noteContent
                                )
                            }
                        }
                        if (note != null) {
                            noteList.add(note)
                        }
                    }
                }
                callbackList(noteList)
            }

            override fun onCancelled(error: DatabaseError) {
                errorCallback()
            }
        })
    }

    fun getNoteForEdit(
        noteType: Int,
        noteUid: String,
        userUid: String,
        projectUid: String?,
        directoryUid: String,
        callbackList: (List<Note>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val ref =
            databaseReference.child("Users").child(userUid).child("Directories").child(directoryUid)

        val noteRef: DatabaseReference = if (noteType == 0) {
            ref.child("NotesInDirectory").child(noteUid)
        } else {
            ref.child("Projects").child(projectUid!!).child("NoteInProject").child(noteUid)
        }

        noteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val noteList = mutableListOf<Note>()
                val noteData = dataSnapshot.getValue(FirebaseNoteInDir::class.java)
                if (noteData != null) {
                    val note = noteData.name?.let { noteName ->
                        noteData.content?.let { noteContent ->
                            Note(
                                noteData.directoryUid, projectUid, noteData.noteUid,
                                noteName, noteContent
                            )
                        }
                    }
                    if (note != null) {
                        noteList.add(note)
                    }
                }
                callbackList(noteList)
            }

            override fun onCancelled(error: DatabaseError) {
                errorCallback()
            }
        })
    }


    // Write note
    fun writeNote(
        uniqueId: String,
        noteType: Int,
        userUid: String,
        projectUid: String? = "",
        directoryUid: String,
        noteName: String,
        noteContent: String
    ) {
        val ref =
            databaseReference.child("Users").child(userUid).child("Directories").child(directoryUid)
        val note = FirebaseNoteInDir(directoryUid, uniqueId, noteName, noteContent)
        if (noteType == 0) {
            ref.child("NotesInDirectory").child(uniqueId).setValue(note)
        } else {
            // Write note in project
            ref.child("Projects").child(projectUid!!).child("NoteInProject").child(uniqueId)
                .setValue(note)
        }
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
        val uniqueId = databaseReference.push().key
        val income = uniqueId?.let { FirebaseIncomes(it, incomeName, incomeAmount, "$year-$month") }
        databaseReference.child("Users").child(userUid).child("Incomes").child(uniqueId!!)
            .setValue(income)
    }

    // Method for get income
    fun getIncome(
        userUid: String, callbackList: (List<Income>) -> Unit, errorCallback: () -> Unit
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
                                income.uniqueId, income.incomeName, it, income.incomeDate
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

        val uniqueId = databaseReference.push().key
        val outcome =
            uniqueId?.let { FirebaseOutcomes(it, outcomeName, outcomeAmount, "$year-$month") }

        databaseReference.child("Users").child(userUid).child("Outcomes").child(uniqueId!!)
            .setValue(outcome)
    }

    // Method for get outcome
    fun getOutcome(
        userUid: String, callbackList: (List<Outcome>) -> Unit, errorCallback: () -> Unit
    ) {
        val outcomeRef = databaseReference.child("Users").child(userUid).child("Outcomes")
        outcomeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalOutcomeAmount = 0.0
                val outcomeList = mutableListOf<Outcome>()
                Log.d("MyLog", "$dataSnapshot")
                for (childSnapshot in dataSnapshot.children) {
                    val outcome = childSnapshot.getValue(FirebaseOutcomes::class.java)
                    if (outcome != null) {
                        val out = outcome.outcomeAmount?.let {
                            Outcome(
                                outcome.uniqueId, outcome.outcomeName, it, outcome.incomeDate
                            )
                        }
                        if (out != null) {
                            outcomeList.add(out)
                            totalOutcomeAmount += out.outcomeAmount
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

    // Write aim
    fun writeAim(
        aimAmount: Double, userUid: String
    ) {
        val (year, month) = getCurrentYearAndMonth()
        val formattedDate = "$year-$month"
        val aim = FirebasePillows(aimAmount, formattedDate)
        databaseReference.child("Users").child(userUid).child("Aim").setValue(aim)
    }

    // Get aim
    fun getAim(
        userUid: String,
        callbackAim: (Double) -> Unit,
        callbackZero: (Double) -> Unit,
        errorCallback: () -> Unit
    ) {
        val (year, month) = getCurrentYearAndMonth()
        val formattedDate = "$year-$month"
        val pillowRef = databaseReference.child("Users").child(userUid).child("Aim")
        pillowRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var aim = 0.0
                for (childSnapshot in dataSnapshot.children) {
                    val aimData = dataSnapshot.getValue(FirebasePillows::class.java)
                    if (aimData != null) {
                        if (aimData.pillowAmount != null && aimData.date == formattedDate) aim =
                            aimData.pillowAmount
                        else callbackZero(0.0)
                    }
                }
                callbackAim(aim)
            }

            override fun onCancelled(error: DatabaseError) {
                errorCallback()
            }
        })
    }

    // Write task
    fun writeTask(
        uniqueId: String,
        taskType: Int,
        userUid: String,
        taskName: String,
        taskContent: String,
        taskDate: String,
        taskRepeat: Int,
        taskNotification: Boolean,
        taskPriority: Int,
        taskIsCompleted: Boolean

    ) {

        val task =
            FirebaseTask(
                uniqueId,
                taskType,
                taskName,
                taskContent,
                taskDate,
                taskRepeat,
                taskNotification,
                taskPriority,
                taskIsCompleted
            )

        if (taskType == 0) {
            databaseReference.child("Users").child(userUid).child("Personal").child(uniqueId)
                .setValue(task)
        } else {
            databaseReference.child("Users").child(userUid).child("Work").child(uniqueId)
                .setValue(task)
        }

    }


    // Writing data to Task
    private fun writeDataIntoTask(affairData: FirebaseTask): Task {
        return Task(
            affairData.uniqueId,
            affairData.taskType,
            affairData.taskName,
            affairData.taskContent,
            affairData.taskDate,
            affairData.taskRepeat,
            affairData.taskNotification,
            affairData.taskPriority,
            affairData.taskIsCompleted
        )
    }

    // Get personal task
    fun getPersonalTask(
        userUid: String,
        callbackList: (List<Task>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val personalTaskRef = databaseReference.child("Users").child(userUid).child("Personal")
        personalTaskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val personalTaskList = mutableListOf<Task>()
                for (childSnapshot in dataSnapshot.children) {
                    val taskData = childSnapshot.getValue(FirebaseTask::class.java)
                    if (taskData != null) {
                        val task = writeDataIntoTask(taskData)
                        if (task != null) {
                            personalTaskList.add(task)
                        }
                    }
                }
                callbackList(personalTaskList)
            }

            override fun onCancelled(error: DatabaseError) {
                errorCallback()
            }

        })
    }

    // Get personal task for editing
    fun getPersonalTaskForEdit(
        userUid: String,
        taskUid: String,
        callbackList: (List<Task>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val personalTaskRef =
            databaseReference.child("Users").child(userUid).child("Personal").child(taskUid)
        personalTaskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val personalTaskList = mutableListOf<Task>()
                val taskData = dataSnapshot.getValue(FirebaseTask::class.java)
                if (taskData != null) {
                    val task = writeDataIntoTask(taskData)
                    if (task != null) {
                        personalTaskList.add(task)
                    }
                }
                callbackList(personalTaskList)
            }

            override fun onCancelled(error: DatabaseError) {
                errorCallback()
            }

        })
    }

    // Get work task for editing
    fun getWorkTaskForEdit(
        userUid: String,
        taskUid: String,
        callbackList: (List<Task>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val workTaskRef =
            databaseReference.child("Users").child(userUid).child("Work").child(taskUid)
        workTaskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val workTaskList = mutableListOf<Task>()
                val affairData = dataSnapshot.getValue(FirebaseTask::class.java)
                if (affairData != null) {
                    val task = writeDataIntoTask(affairData)
                    if (task != null) {
                        workTaskList.add(task)
                    }
                }
                callbackList(workTaskList)
            }

            override fun onCancelled(error: DatabaseError) {
                errorCallback()
            }

        })
    }

    // Get work task
    fun getWorkTask(
        userUid: String,
        callbackList: (List<Task>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val workTaskRef = databaseReference.child("Users").child(userUid).child("Work")
        workTaskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val workTaskList = mutableListOf<Task>()
                for (childSnapshot in dataSnapshot.children) {
                    val taskData = childSnapshot.getValue(FirebaseTask::class.java)
                    if (taskData != null) {
                        val task = writeDataIntoTask(taskData)
                        if (task != null) {
                            workTaskList.add(task)
                        }
                    }
                }
                callbackList(workTaskList)
            }

            override fun onCancelled(error: DatabaseError) {
                errorCallback()
            }

        })
    }
}