package com.example.notefirebase.firebasemodel

data class FirebaseDirectory(
    val directoryUniqueId: String,
    val directoryName: String
) {
    constructor() : this("", "")
}

data class Directory(
    val directoryUniqueId: String,
    val directoryName: String
)

data class FirebaseProject(
    val projectUid: String,
    val directoryUid: String,
    val name: String
) {
    constructor() : this("", "", "")
}

data class Project(
    val projectUid: String,
    val directoryUid: String,
    val name: String
)

data class FirebaseNoteInDir(
    val directoryUid: String,
    val noteUid: String,
    val name: String?,
    val content: String?
) {
    constructor() : this("", "","", "")
}

data class Note(
    val directoryUid: String,
    val projectUid: String?,
    val noteUid: String,
    var noteName: String,
    var noteContent: String
)

data class FirebaseIncomes(
    val uniqueId: String,
    val incomeName: String,
    val incomeAmount: Double?,
    val incomeDate: String
) {
    constructor() : this("", "", null, "")
}

data class Income(
    val uniqueId: String,
    val incomeName: String,
    val incomeAmount: Double,
    val incomeDate: String
)

data class FirebaseOutcomes(
    val uniqueId: String,
    val outcomeName: String,
    val outcomeAmount: Double?,
    val incomeDate: String
) {
    constructor() : this("", "", null, "")
}

data class Outcome(
    val uniqueId: String,
    val outcomeName: String,
    val outcomeAmount: Double,
    val outcomeDate: String
)

data class IncomeAndOutcome(
    val IncomeAmount: Double?,
    val IncomeDate: String?,
    val OutcomeAmount: Double?,
    val OutcomeDate: String?
)

data class FirebasePillows(
    val pillowAmount: Double, val date: String
) {
    constructor() : this(0.0, "")
}

data class Chart(
    val incomePercent: Double?,
    val incomeDate: String?,
    val outcomePercent: Double?,
    val outcomeDate: String?
)

data class FirebaseTask(
    val uniqueId: String,
    val taskType: Int,
    val taskName: String,
    val taskContent: String,
    val taskDate: String,
    val taskRepeat: Int,
    val taskNotification: Boolean,
    val taskPriority: Int,
    val taskIsCompleted: Boolean?
) {
    constructor() : this("", 0,"", "", "", 0, false, 0, null)
}

data class Task(
    val uniqueId: String,
    val taskType: Int,
    var taskName: String?,
    var taskContent: String?,
    val taskDate: String,
    var taskRepeat: Int,
    var taskNotification: Boolean,
    var taskPriority: Int?,
    var taskIsCompleted: Boolean?
)



