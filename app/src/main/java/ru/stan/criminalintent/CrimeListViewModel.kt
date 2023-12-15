package ru.stan.criminalintent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


// 12.3 Delaying work: create a TAG to see how delaying work works
private const val TAG = "CrimeListViewModel"

class CrimeListViewModel : ViewModel() {

    // 12.23 create a crimeRepository variable that grabs the getter out of the CrimeRepository
    private val crimeRepository = CrimeRepository.get()

    // 12.26 use the crimes variable to get the crimes from crimeRepository's getCrimes() function
    //val crimes = crimeRepository.getCrimes()

    // 12.28 Use StateFlow to cache database results
    private val _crimes: MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())
    val crimes: StateFlow<List<Crime>>
        get() = _crimes.asStateFlow()

    init {
        //(12.26) Log.d(TAG, "init starting")     // 12.3 Delaying work: Create a log that'll show that the previous init line ran
        viewModelScope.launch {     // 12.2 Launch a coroutine
            // 12.28
            crimeRepository.getCrimes().collect {
                _crimes.value = it

            /*(12.26)Log.d(TAG, "coroutine launched")        // 12.3 Delaying work: create a log that indicates the viewModelScope.launch { ran
            (12.26)crimes += loadCrimes()
            (12.26)Log.d(TAG, "Loading crimes finished")   // 12.3 Delaying work: log that the coroutine is finished*/
            }
        }
    }
    /* 12.26 Get rid of all this code because we're using Flow and don't need this code
    // 12.4 Define a suspending function called loadCrimes()
    suspend fun loadCrimes(): List<Crime> {
        /* 12.23 Remove code b/c now we have a database
        val result = mutableListOf<Crime>()
        delay(5000)// 12.3 Delaying work: delay for 5 seconds with delay(timeMillis:Long)
        for (i in 0 until 100) {
            val crime = Crime(
                id = UUID.randomUUID(),
                title = "Crime #$i",
                date = Date(),
                isSolved = i % 2 == 0
            )
            result += crime
        }
        return result*/
        // 12.23 Use the getCrimes() function to get all the crimes in the database
        return crimeRepository.getCrimes()
    }*/
}