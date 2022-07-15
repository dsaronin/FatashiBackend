package org.umoja4life.fatashibackend

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val DEBUG = false

const val APP_NAME = "fatashi"    // app name used in prompt
const val LIST_LINE_COUNT = 20   // how many lines of dict to list

class FatashiProperties(

    var appName: String = APP_NAME,     // app name used in prompt
    var listLineCount: Int = LIST_LINE_COUNT,   // how many lines of dict to list
    var verboseFlag: Boolean = false,  // true if verbose status information
    var debugFlag: Boolean = false,  // true if debug traces
    var prodFlag: Boolean = false,  // true if production (not test) mode
    var langList: Stack<String> = mutableListOf<String>()  // ordered filenames for language properties

) {
    var clampResults = LIST_LINE_COUNT + LIST_LINE_COUNT  // used to clamp max results for any single search
    private var wasnotValid = true  // assume issue with fatashi_properties
    private var filename : String = ""

    init {
        // force sanity on some values
        setLineCount( listLineCount )
        if (appName.isBlank()) appName = APP_NAME
        if (DEBUG)  printWarn(">>>> FatashiProp INIT <<<<<")
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

    // isNotViable -- returns true if there are no LanguageProperty lists
    fun isNotViable() : Boolean = wasnotValid

    fun hasInvalidLangLists() : Boolean = langList.isEmpty()

    fun myStatus() : String = "configurations $filename (${isNotViable().toChar()})\n"

    companion object {

        private suspend fun readJsonConfigurations(f: String, v: Boolean = false): FatashiProperties {
            var myProperties = FatashiProperties()
            val myPropertiesType = object : TypeToken<FatashiProperties>() {}.type

            if (DEBUG) if (v) printWarn("Reading ConfigProperties file: $f")

            try {
                val text = MyEnvironment.myPlatform.getFile(f)
                if( text.isNotBlank() ) {
                    with( Gson().fromJson(text, myPropertiesType) as FatashiProperties ) {
                        wasnotValid = hasInvalidLangLists()  // validity depends on valid langProp Lists
                        filename = f
                        myProperties = this
                    }
                }
            }
            catch (ex: Exception) {
                if (DEBUG) printError(ex.toString())
            }

            if (DEBUG) printWarn(">>>>> post FatashiProperties: $f, ${myProperties.filename}; ${myProperties.wasnotValid}; ${myProperties.isNotViable()}")
            return myProperties
        }  // readJsonConfiguration

        suspend fun fatashiDataSetup(f: String, v: Boolean = false): FatashiProperties {
            var myProps  : FatashiProperties = readJsonConfigurations(f)
                // future expansion?
            return myProps
        } // fatashiDataSetup

    }  // companion object

}   // class FatashiProperties