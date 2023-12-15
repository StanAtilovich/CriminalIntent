package ru.stan.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.stan.criminalintent.Crime

// 12.11 Add CrimeDatabase class; @Database annotation tells Room that this class represents a database in the app
/* @Database has two parameters:
*   1. A list of entity classes, which tells Room which entity classes to use for the database
*   2. The version of the database, the first time you create a database, it should be 1 (version=1)*/
@Database(entities = [Crime::class], version=1)

// 12.13 Enable the database to use the converter functions
@TypeConverters(CrimeTypeConverters::class)

abstract class CrimeDatabase : RoomDatabase() {     // CrimeDatabase extends from RoomDatabase; abstract classes can be overriden down the line
    /* Abstract functions have to be in an abstract class */

    // 12.16 Create a function that will create an instance of the DAO, use CrimeDao as return type
    abstract fun crimeDao(): CrimeDao
}
