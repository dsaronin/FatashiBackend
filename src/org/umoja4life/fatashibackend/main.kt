package org.umoja4life.fatashibackend

// kotlin conventional starting point, kicks off everything

fun main(args: Array<String>) {
    MyEnvironment.setup(args, LinuxPlatform() )   // initialize app environment
    FatashiWork.setupWork()
    FatashiWork.work()      // do the work of Fatashi
    FatashiWork.closedownWork()
}
