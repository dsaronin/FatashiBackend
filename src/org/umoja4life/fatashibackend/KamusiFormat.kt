package org.umoja4life.fatashibackend

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

data class KamusiFormat(
        val filename: String,
        val kamusiType: String,
        val kamusiLang: String,
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
                "","","",
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
    fun kamusiFormatSetup(fnList: Stack<String>, fkList: Stack<KamusiFormat>) {
        val fn = fnList.pop() ?: return  // pop an element; unless end of list, return

        if( fn.isNotEmpty() )  {  // only handle nonEmpty filenames
                // push to result list our KamusiFormat
            fkList.push(readJsonKamusiFormats(fn))
        }
        kamusiFormatSetup(fnList, fkList)  // recurse if more in list

        // fall thru to return
    }

    // readJsonKamusiFormats -- opens a single KamusiFormat JSON and
    // returns it as a KamusiFormat object
    // if exception encountered, returns an empty KamusiFormat object
    fun readJsonKamusiFormats(f: String) : KamusiFormat {
        val kamusiFormat : KamusiFormat
        val kamusiFormatType = object : TypeToken<KamusiFormat>() {}.type
        val gson = Gson()

        MyEnvironment.printWarnIfDebug("Reading KamusiFormat file: $f")

        try {
            kamusiFormat = gson.fromJson( File(f).readText(), kamusiFormatType)
            MyEnvironment.printWarnIfDebug("$kamusiFormat")
        }
        catch(ex: Exception){
            printError(ex.toString())
            return KamusiFormat()
        }

        return kamusiFormat
    }
}

}  // class
