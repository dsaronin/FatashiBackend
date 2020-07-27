package org.umoja4life.fatashibackend

import kotlinx.coroutines.*

// kotlin conventional starting point, kicks off everything

fun main(args: Array<String>) {
    runBlocking {
        launch {
            val myPlatform = LinuxPlatform()
            MyEnvironment.setup(args, myPlatform)   // initialize app environment
                // if any type of I/O error or empty config info,
                // then setup for running from sample data as a default kamusi
            if (MyEnvironment.isNotViable() ) MyEnvironment.nofileSetup(args, myPlatform)
        }
    }
    FatashiWork.setupWork()
    FatashiWork.work()      // do the work of Fatashi
    FatashiWork.closedownWork()
}
