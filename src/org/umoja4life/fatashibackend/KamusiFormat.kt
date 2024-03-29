package org.umoja4life.fatashibackend

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val DEBUG = false
        const val DUMMYSTUB = "sample-kamusi"

data class KamusiFormat(
        var filename: String,
        val kamusiType: String,
        val kamusiLang: String,  // currently not used, referenced
        val fieldDelimiters: String,
        val wrapKeyHead: String,
        val wrapKeyTail: String,
        val wrapDefHead: String,
        val wrapDefTail: String,
        val wrapUsgHead: String,
        val wrapUsgTail: String,
        val hashtagOn: Boolean,
        val hashtagWrapPattern: String,
        val percentOn: Boolean,
        val ampersandOn: Boolean,
        val atsignOn: Boolean,
        val atsignWrapPattern: String,
        private val meEmpty: Boolean = false
) {
    // secondary constructor for instantiating empty object
        constructor() : this(
            "", "", "",
                "",
            "","",
                "","",
                "","",
            false, "",
            false,false,
                false, "",
                true  // flag this is an empty object
        )

    companion object {

        // kamusiFormatSetup -- recursive: works backwards on a list of KamusiFormat filenames
        // to open the file, read the JSON and return an object
        // constructs a FIFO of the results
        suspend fun kamusiFormatSetup(fnList: Stack<String>, fkList: Stack<KamusiFormat>) {
            val fn = fnList.pop() ?: return  // pop an element; unless end of list, return -->>>>

            if( fn.isNotEmpty() )  {  // only handle nonEmpty filenames
                    // push to result list our KamusiFormat
                fkList.push(readJsonKamusiFormats(fn))
            }
            kamusiFormatSetup(fnList, fkList)  // recurse if more in list
            // fall thru to return  <<<<<<<<<<<<---
        }

        // readJsonKamusiFormats -- opens a single KamusiFormat JSON and
        // returns it as a KamusiFormat object
        // if exception encountered, returns an empty KamusiFormat object
        suspend fun readJsonKamusiFormats(f: String) : KamusiFormat {
            var kamusiFormat = KamusiFormat()
            val kamusiFormatType = object : TypeToken<KamusiFormat>() {}.type

            if (DEBUG) printWarn("KamusiFormat -- Reading: $f")
            kamusiFormat.filename = f  // remember json filename in case of failure

            try {
                val text = MyEnvironment.myPlatform.getFile(f)
                if( text.isNotBlank() ) {
                    kamusiFormat = Gson().fromJson(text, kamusiFormatType)
                }
            }
            catch(ex: Exception){
                printError(ex.toString())
            }

            return kamusiFormat
        }

        // nofilesetup  -- preps the internal dummyDictionary
        // returns kamusi object for it
        fun nofilesetup( dummyDict : String, myLang : LangData ) : Kamusi {
            val myKF = KamusiFormat(
                    DUMMYSTUB, "kamusi", "kiswahili",
                    "(\\s+--\\s+)|(\\t__[ \\t\\x0B\\f]+)",
                    "^[^\\t]*","[^\\t]*\\t",
                    "^[^\\t]+\\t[^\\t]*","[^\\t]*\\t",
                    "^[^\\t]+\\t[^\\t]+\\t[^\\t]*","[^\\t]*\$",
                    true, "(#)",
                    true,true,
                    true, "{@}",
                    true  // flag this is an empty object
            )
            return Kamusi.nofilesetup( myKF, dummyDict, myLang )
        }

    }  // companion object


}  // class KamusiFormat
