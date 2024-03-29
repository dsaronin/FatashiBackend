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
    @Transient var langProc : LangProcessing = Neutral  // default is nop pre/post processing
    @Transient var kamusiHead:  Kamusi? = null
    @Transient var methaliHead: Kamusi? = null
    @Transient var testHead:    Kamusi? = null
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

    // setLangProc -- determines and sets the language processing module
    // if none matching found, allows default Neutral (nop) module to remain in place
    fun setLangProc() {
        when (langCode) {
            "sw" -> langProc = Swahili
            "tr" -> langProc = Turkish
            else -> langProc = Neutral
        }
    }

    // ========================================================================
    companion object {

        private suspend fun readJsonConfigurations(f: String, v: Boolean = false): LangData {
            var myProperties = LangData()
            val myPropertiesType = object : TypeToken<LangData>() {}.type

            if (DEBUG) printWarn("LangData -- Reading: $f")

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
                if (DEBUG) printError("** LANGDATA **  " + ex.toString())
            }

            if (DEBUG) printWarn(">>>>> post LangData for: $f, f: ${myProperties.filename}; Valid-err: ${myProperties.wasnotValid}; Viable-err: ${myProperties.isNotViable()}")
            return myProperties
        }  // readJsonConfiguration

        suspend fun langDataSetup(f: String, v: Boolean = false): LangData {
            val myProps  : LangData = readJsonConfigurations(f)

            myProps.validateCodes()  // check & change lang/flag CODES to be valid
            myProps.setLangProc()    // setup the pre/post processor for search items

            val kamusiFormatList: Stack<KamusiFormat> = mutableListOf<KamusiFormat>()
            val methaliFormatList: Stack<KamusiFormat> = mutableListOf<KamusiFormat>()
            val testFormatList: Stack<KamusiFormat> = mutableListOf<KamusiFormat>()

            // get the various KamusiFormat files for each kamusi
            KamusiFormat.kamusiFormatSetup(myProps.kamusiList, kamusiFormatList)
            KamusiFormat.kamusiFormatSetup(myProps.methaliList, methaliFormatList)
            KamusiFormat.kamusiFormatSetup(myProps.testList, testFormatList)

            // process the KamusiFormat files to create kamusi objects
            myProps.kamusiHead = Kamusi.kamusiSetup(kamusiFormatList, myProps)
            myProps.methaliHead = Kamusi.kamusiSetup(methaliFormatList, myProps)
            myProps.testHead = Kamusi.kamusiSetup(testFormatList, myProps)

            return myProps
        } // languageSetup

        fun nofilesetup( dummyDict : String ) : LangData {
            val myProps  = LangData()
            myProps.testHead = KamusiFormat.nofilesetup( dummyDict, myProps )
            return myProps
        }

    }  // companion object

}
