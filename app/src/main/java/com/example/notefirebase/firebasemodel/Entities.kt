package com.example.notefirebase.firebasemodel

import com.google.firebase.database.PropertyName

data class FirebaseDirectory(
    val directoryId: String = "",
    val userUid: String? = null,
    val name: String = ""
) {
    constructor() : this("", null, "")
}

data class Directory(
    val id: String,
    val name: String
)

data class FirebaseProject(
    val projectId: String = "",
    val directoryId: String = "",
    val userUid: String? = null,
    val name: String = ""
) {
    constructor() : this("", "", null, "")
}

data class Project(
    val id: String,
    val name: String
)

data class FirebaseNoteInDir(
    val directoryId: String,
    val userUid: String?,
    val name: String?,
    val content: String?
) {
    constructor() : this("", null, "", "")
}

data class Note(
    val name: String?,
)

data class FirebaseIncomes(
    val incomeName: String,
    val incomeAmount: Double?,
    val incomeDate: String
) {
    constructor() : this ("", null, "")
}

data class Income(
    val incomeName: String,
    val incomeAmount: Double,
    val incomeDate: String
)

data class FirebaseOutcomes(
    val outcomeName: String,
    val outcomeAmount: Double?,
    val incomeDate: String
) {
    constructor() : this ("", null, "")
}

data class Outcome(
    val outcomeName: String,
    val outcomeAmount: Double,
    val outcomeDate: String
)

data class IncomeAndOutcome(
    val IncomeAmount: Double?,
    val IncomeDate: String?,
    val OutcomeAmount: Double?,
    val OutcomeDate: String?
//    val incomeAmount: Double,
    //val outcomeAmount: Double,
//    val date: String
)

data class FirebasePillows(
    val pillowAmount: Double?
) {
    constructor() : this ( null)
}

data class Pillow(
    val pillowAmount: Double
)


