package org.umoja4life.fatashibackend

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

private const val DEBUG = false

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
        private var wasnotValid = true  // assume issue with config_properties
        private var filename : String = ""

    init {
        // force sanity on some values
        setLineCount( listLineCount )
        if (appName.isBlank()) appName = APP_NAME
        if (DEBUG)  printWarn(">>>> Config INIT <<<<<")
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
    fun isNotViable() : Boolean = wasnotValid

    fun hasInvalidKFLists() : Boolean = (kamusiList.isEmpty() && testList.isEmpty())

    fun myStatus() : String = "configurations $filename (${isNotViable().toChar()})\n"

    companion object {

        suspend fun readJsonConfigurations(f: String, v: Boolean = false): ConfigProperties {
            var myProperties = ConfigProperties()
            val myPropertiesType = object : TypeToken<ConfigProperties>() {}.type

            if (DEBUG) if (v) printWarn("Reading ConfigProperties file: $f")

            try {
                val text = MyEnvironment.myPlatform.getFile(f)
                if( text.isNotBlank() ) {
                    with( Gson().fromJson(text, myPropertiesType) as ConfigProperties ) {
                        wasnotValid = hasInvalidKFLists()  // validity depends on valid KF Lists
                        filename = f
                        myProperties = this
                    }
                }
            }
            catch (ex: Exception) {
                if (DEBUG) printError(ex.toString())
            }

            if (DEBUG) printWarn(">>>>> post ConfigProperties: $f, ${myProperties.filename}; ${myProperties.wasnotValid}; ${myProperties.isNotViable()}")
            return myProperties
        }
    }  // companion object

} // class ConfigProperties