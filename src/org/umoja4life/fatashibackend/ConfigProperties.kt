package org.umoja4life.fatashibackend

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

const val APP_NAME = "fatashi"    // app name used in prompt
const val LIST_LINE_COUNT = 20   // how many lines of dict to list

class ConfigProperties(
        var appName: String = APP_NAME,     // app name used in prompt
        var listLineCount: Int = LIST_LINE_COUNT,   // how many lines of dict to list
        var verboseFlag: Boolean = false,  // true if verbose status information
        var debugFlag: Boolean = false,  // true if debug traces
        var prodFlag: Boolean = false,  // true if production (not test) mode
        var kamusiList: Stack<String> = mutableListOf<String>(),  // ordered filenames for dictionaries
        var methaliList: Stack<String> = mutableListOf<String>(),  // ordered filenames for methali dicts
        var testList: Stack<String> = mutableListOf<String>()   // ordered filenames for test dicts
) {
        var clampResults = LIST_LINE_COUNT + LIST_LINE_COUNT  // used to clamp max results for any single search

    init {
        // force sanity on some values
        setLineCount( listLineCount )
        if (appName.isBlank()) appName = APP_NAME
    }  // init block

        // clamps output results to 2x listline count to avoid 100s of lines of output
    fun setClampResults() {
        clampResults = listLineCount + listLineCount
    }

        // setLineCount -- sets the linecount and performs sanity checks
    fun setLineCount(n: Int) {
        listLineCount = n
        if (n <= 3) listLineCount = LIST_LINE_COUNT
        setClampResults()
    }

        // isNotViable -- returns true if there are no Kamusi Format lists
    fun isNotViable() : Boolean {
        return (kamusiList.isEmpty() && methaliList.isEmpty() && testList.isEmpty())
    }

    companion object {

        suspend fun readJsonConfigurations(f: String, v: Boolean = false): ConfigProperties {
            val myProperties: ConfigProperties
            val myPropertiesType = object : TypeToken<ConfigProperties>() {}.type

            if (v) printWarn("Reading ConfigProperties file: $f")

            try {
                myProperties = Gson()
                    .fromJson(MyEnvironment.myPlatform.getFile(f), myPropertiesType)
            }
            catch (ex: Exception) {
                printError(ex.toString())
                return ConfigProperties()
            }

            return myProperties
        }
    }  // companion object

} // class ConfigProperties