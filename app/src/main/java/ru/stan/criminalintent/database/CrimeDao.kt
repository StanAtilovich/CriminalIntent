package ru.stan.criminalintent.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.stan.criminalintent.Crime
import java.util.*

// 12.14 Create a DAO (Data Access Object) called CrimeDao
@Dao        // @Dao tells Room that CrimeDao is a data access object
interface CrimeDao {

    /*  @Query annotation means that getCrimes() and getCrime() are meant to PULL info from database (not insert, update, or delete).
    *   @Query expects a string containing a SQL command as input */

    // Returns all crimes in the crime database table
    @Query("SELECT * FROM crime")
    // 12.24 Create a Flow from the database (and remove suspend in front of the function)
    fun getCrimes(): Flow<List<Crime>>

    // Returns only a crime that matches a specified id
    @Query("SELECT * FROM crime WHERE id=(:id)")
    suspend fun getCrime(id: UUID): Crime // Adding "suspend" allows you to asynchronously call a function within a coroutine

}