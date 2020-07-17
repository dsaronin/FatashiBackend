package org.umoja4life.fatashibackend

// kotlin conventional starting point, kicks off everything

fun main(args: Array<String>) {
    FatashiWork.setupWork(args)
    FatashiWork.work()      // do the work of Fatashi
    FatashiWork.closedownWork()
}
