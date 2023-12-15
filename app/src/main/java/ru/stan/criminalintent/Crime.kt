package ru.stan.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity    // 12.10 Make the Crime class an entity for our database
/* @Entity is applied at the class level, it indicates that the class defines the structure of a table/set of tables */
data class Crime(
    // These will be our table's columns (4 columns, one for each property)
    @PrimaryKey val id: UUID,          // @PrimaryKey annotation will make UUID the primary key for the Crime entity
    val title: String,
    val date: Date,
    val isSolved: Boolean
)
