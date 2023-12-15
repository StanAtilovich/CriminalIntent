package ru.stan.criminalintent.database

import androidx.room.TypeConverter
import java.util.Date

// 12.12 Create CrimeTypeConverter class and add TypeConverter functions
class CrimeTypeConverters {

    // This function tells Room how to convert the type to store it in the database
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    // This function tells Room how to convert from the database representation back to the original type
    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}
