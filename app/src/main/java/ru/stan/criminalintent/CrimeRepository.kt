package ru.stan.criminalintent

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import ru.stan.criminalintent.database.CrimeDatabase
import java.util.UUID

/*  A repository class encapsulates the logic for accessing data from a single
    source or a set of sources. It determines how to fetch and store a particular set of data.
    The UI code will request all the data from the repository

    A repository encapsulates the logic for adding data so it's all in one spot */

// 12.20 create a constant to store database name
private const val DATABASE_NAME = "crime-database"

// 12.17 Implement a repository
/* CrimeRepository is a singleton, there will only be one instance of it in the app process
*   Marking the constructor as private ensures no components can "go rogue and create their own instance" */
class CrimeRepository private constructor(context: Context) {

    // 12.20 Add a private property to store a reference to the database
    /*  Room.databaseBuilder() creates a concrete implementation of the abstract CrimeDatabase using
        three parameters:
            1. a Context object, since the database is accessing the filesystem
            2. the database class you want Room to create
            3. the name of the database file you want Room to create */
    private val database: CrimeDatabase = Room
        .databaseBuilder(
            context.applicationContext, // we pass applicationContext bc the singleton will live longer than any activity classes
            CrimeDatabase::class.java,
            DATABASE_NAME
        )
        /* createFromAsset(databaseFilePath) */
        .createFromAsset(DATABASE_NAME) // 12.22 when we open this folder, it creates the database from that asset
        .build()

    // 12.21 Add a function to the repository for each function in the DAO (there are two)
    // 12.25 Change getCrimes() to use Flow
    fun getCrimes(): Flow<List<Crime>> = database.crimeDao().getCrimes()
    suspend fun getCrime(id: UUID): Crime = database.crimeDao().getCrime(id)


    companion object {      // Create a companion object
        private var INSTANCE: CrimeRepository? = null

        // Make CrimeRepository a singleton by adding two functions to the companion object:
        // 1st function: Initializes a new instance of the repository
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }
        // 2nd function: Access the repository
        fun get(): CrimeRepository {
            return INSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}
