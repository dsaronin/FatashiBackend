package org.umoja4life.fatashibackend

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.logging.Logger

private const val DEBUG = false

class LinuxPlatform : PlatformIO {

    private val Log: Logger = Logger.getLogger(LinuxPlatform::class.java.name)

    override val myPath: String
        get() = "data/"

    override fun lineoutInfo(s: String) {
        println(s)
    }

    override fun lineoutError(s: String) {
        // lineoutInfo(s)
        Log.severe(s)
    }

    override fun infoAlert(s: String) {
        println(s)
    }

    override fun infoAlert(l: List<String>) {
        l.forEach { println(it) }
    }

    override fun getCommandLine(): List<String> {
        return readLine()?.trim()?.split(' ') ?: listOf("exit")
    }

    override fun putPrompt(prompt: String) {
        print( prompt )
    }

    override fun listout(l: List<String>, clearBuffer:Boolean) {
        l.forEach { println(it) }
    }

        // getFile as readText()
        // done thread-safe
        // returns input string or "" if input failure
    override suspend fun getFile(f: String): String {
        var result = ""
        val file = File(f)

        if( f.isNotBlank() && file.exists() ) { // skip if nofilename or no file
            withContext(Dispatchers.IO) {
                try {
                    result = file.readText()
                } catch (ex: IOException) {
                    if (DEBUG) printError(">>>>> IOException: file: $f ex: $ex")
                } // catch
            } // Dispatchers.IO
        }
        return result
    }

    override fun getFrontVersion(): String {
        return "(c) 2022 David S. Anderson, All Rights Reserved"
    }

}  // class LinuxPlatform