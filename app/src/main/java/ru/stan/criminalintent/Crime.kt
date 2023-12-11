package ru.stan.criminalintent

import java.util.Date
import java.util.UUID

data class Crime(
    val id : UUID,
    val title: String,
    val data: Date,
    val isSolved: Boolean
)
