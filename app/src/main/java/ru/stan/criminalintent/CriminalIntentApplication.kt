package ru.stan.criminalintent

import android.app.Application

/*  We want to initialize the app to run and to tell it what the application is going to do.
*   Application (subclass) allows you to access lifecycle info about the app itself */

// 12.18 Create an application subclass
class CriminalIntentApplication : Application() {
    /*  Application.onCreate() is called by the system when the app is first loaded into memory.
    *   CriminalIntentApplication is not re-created on configuration changes, it is created when
    *     the app launches and is destroyed when the app process is destroyed
    *       - This makes it a good place to do one-time initialization operations */
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}