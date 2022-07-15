package org.umoja4life.fatashibackend

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val DEBUG = false

const val PROMPT_NAME = "fatashi"    // app name used in prompt
const val LANG_NAME = "swahili"    // app name used in prompt
const val LANG_CODE = "sw"    // app name used in prompt
const val FLAG_CODE = "tz"    // app name used in prompt

private const val ERR_PROMPT_NAME = "x_prompt"    // value if error detected
private const val ERR_LANG_NAME = "x_lang"    // value if error detected
private const val ERR_LANG_CODE = "xx"    // value if error detected
private const val ERR_FLAG_CODE = "xx"    // value if error detected

class LangData(
        var promptStr: String = PROMPT_NAME,     // app name used in command-line prompt
        var langName: String = LANG_NAME,   // human-readable language name
        var langCode: String = LANG_CODE,  // two-character standard language abbreviation
        var flagCode: String = FLAG_CODE,  // two-character standard flag abbreviation
        var kamusiList: Stack<String> = mutableListOf<String>(),  // ordered filenames for dictionaries
        var methaliList: Stack<String> = mutableListOf<String>(),  // ordered filenames for methali dicts
        var testList: Stack<String> = mutableListOf<String>()   // ordered filenames for test dicts
)  {
    // TODO : kamusi/methali/testHeads become part of a langData object
    var kamusiHead:  Kamusi? = null
    var methaliHead: Kamusi? = null
    var testHead:    Kamusi? = null
    private var wasnotValid = false  // assume no issue with langData_properties
    private var filename : String = DUMMYSTUB  // will be replaced with actual filename
    private var accumerrors : String = ""  // will accumulate errors

    // isNotViable -- returns true if there are no LanguageProperty lists
    fun isNotViable() : Boolean = wasnotValid

    // hasInvalidLangList -- returns true if the LangList was invalid
    fun hasInvalidLangList() : Boolean = (kamusiList.isEmpty() && testList.isEmpty())

    // prompt -- returns a formatted prompt string from the given lang
    //   example:  "sözlük [tr]"
    fun prompt() : String = "$promptStr [$langCode]"

    // myStatus  -- returns a formmated string for the status of this language tree
    fun myStatus() : String {
        return "$langName [$langCode/$flagCode]: " +
            (kamusiHead?.myStatus() ?: "") +
            (testHead?.myStatus() ?: "") +
            (methaliHead?.myStatus() ?: "") +
            "\n" + accumerrors
    }

    // warnStatus  -- print a warning if DEBUG; accum the status
    fun warnStatus( myerr : String ) {
        if (DEBUG)  MyEnvironment.printWarnIfDebug( myerr )
        accumerrors += (myerr + "\n")
    }

    // isValidCode  -- returns true a string is nonBlank && only has latin alphanumeric
    fun isValidCode( code : String ) : Boolean {
        return ( code.isNotBlank() && Regex("^\\w+$").matches(code))
    }

    // validateCodes  -- check all codes, prompt, and langname for validity
    // force to an error value if found to be invalid
    fun validateCodes() {
        if( !isValidCode( langCode ) ) langCode = ERR_LANG_CODE
        if( !isValidCode( flagCode ) ) flagCode = ERR_FLAG_CODE
        if( promptStr.isBlank() ) promptStr = ERR_PROMPT_NAME
        if( langName.isBlank() ) langName = ERR_LANG_NAME
    }

    // ========================================================================
    companion object {

        private suspend fun readJsonConfigurations(f: String, v: Boolean = false): LangData {
            var myProperties = LangData()
            val myPropertiesType = object : TypeToken<LangData>() {}.type

            if (DEBUG) if (v) printWarn("Reading LangData properties file: $f")

            try {
                val text = MyEnvironment.myPlatform.getFile(f)
                if( text.isNotBlank() ) {
                    with( Gson().fromJson(text, myPropertiesType) as LangData ) {
                        wasnotValid = hasInvalidLangList()  // validity depends on valid dictionary Lists
                        filename = f
                        myProperties = this
                    }
                }
            }
            catch (ex: Exception) {
                if (DEBUG) printError(ex.toString())
            }

            if (DEBUG) printWarn(">>>>> post LangData: $f, ${myProperties.filename}; ${myProperties.wasnotValid}; ${myProperties.isNotViable()}")
            return myProperties
        }  // readJsonConfiguration

        suspend fun langDataSetup(f: String, v: Boolean = false): LangData {
            val myProps  : LangData = readJsonConfigurations(f)

            val kamusiFormatList: Stack<KamusiFormat> = mutableListOf<KamusiFormat>()
            val methaliFormatList: Stack<KamusiFormat> = mutableListOf<KamusiFormat>()
            val testFormatList: Stack<KamusiFormat> = mutableListOf<KamusiFormat>()

            // get the various KamusiFormat files for each kamusi
            KamusiFormat.kamusiFormatSetup(myProps.kamusiList, kamusiFormatList)
            KamusiFormat.kamusiFormatSetup(myProps.methaliList, methaliFormatList)
            KamusiFormat.kamusiFormatSetup(myProps.testList, testFormatList)

            // process the KamusiFormat files to create kamusi objects
            myProps.kamusiHead = Kamusi.kamusiSetup(kamusiFormatList)
            myProps.methaliHead = Kamusi.kamusiSetup(methaliFormatList)
            myProps.testHead = Kamusi.kamusiSetup(testFormatList)

            myProps.validateCodes()  // check & change lang/flag CODES to be valid

            return myProps
        } // languageSetup

        fun nofilesetup( dummyDict : String ) : LangData {
            val myProps  = LangData()
            myProps.testHead = KamusiFormat.nofilesetup( dummyDict )
            return myProps
        }

    }  // companion object

}