package com.example.notefirebase.firebasemodel

import com.github.mikephil.charting.data.PieEntry

data class FirebaseDirectory(
    val directoryId: String = "", val userUid: String? = null, val name: String = ""
) {
    constructor() : this("", null, "")
}

data class Directory(
    val id: String, val name: String
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
    val id: String, val name: String
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


