package org.umoja4life.fatashibackend

private const val DEBUG = false

// NOTE: defaultCode expected to be blank for new objects to indicate default hasn't been set up yet
class LanguageControl {
    var defaultCode: String = ""  // default language code; SEE NOTE ABOVE
    var defaultFlag: String = "" // default flag code will go here
    var myLangs = mutableMapOf<String, LangData>()
    private var meEmpty: Boolean = true
    private var accumerrors : String = ""  // will accumulate errors

    // isNotViable  -- returns true if system doesn't have viable language data
    // a variety of factors could cause this: problems in reading files, etc
    fun isNotViable() : Boolean {
        return meEmpty
    }

    // warnStatus  -- print a warning if DEBUG; accum the status
    fun warnStatus( myerr : String ) {
        if (DEBUG)  MyEnvironment.printWarnIfDebug( myerr )
        accumerrors += (myerr + "\n")
    }

    // prompt -- returns a formatted prompt string from the default lang
    fun prompt() : String = getDefaultLanguage().prompt()

    // myStatus  -- returns a displayble string of inner status of all key parts
    fun myStatus() : String {
        var s = ""

        myLangs.forEach {
            s += it.value.myStatus()  // it.value is the next langData obj
        }
        return s + accumerrors
    }

    // language-tree processing abilities   ***********************************
    // EXCEPTION: if the defaultCode key isn't in myLangs
    // map.getValue(key) generates exception if the key isn't found
    // ************************************************************************
    // concepts:
    //   default -- is the given chosen language tree
    //   alternate -- is the next lang from list of other language trees
    //   toggle  -- flips default <> alternate trees
    // ************************************************************************

    // setnewCodes -- will set default values based on a given language tree
    // args:  -- aLang: a language tree
    private fun setnewCodes( aLang : LangData ) : LangData {
        defaultCode = aLang.langCode
        defaultFlag = aLang.flagCode
        meEmpty = false
        return aLang
    }

    // toggleLanguage  -- flips alternate & default language trees;
    // returns new default
    fun toggleLanguage() : LangData {
         // get an alternate language; reset the defaults
        return setnewCodes( getAlternateLanguage() )
    }

    // getAlternateLanguage  -- returns the alternate language tree
    //   idea is find the position of default within the keys list,
    //   increment it, and return the language tree that indexes.
    //   need to reset the index if already at end of list
    //      to force the wrap around
    fun getAlternateLanguage() : LangData {
        // find keys index to defaultCode location; incr for next key
        // note: if not found, indexOf returns -1
        var ndex  = 1 + myLangs.keys.indexOf( defaultCode )

        // wrap the index around to first element if it was at the end
        ndex = if ( ndex <= 0  ||  ndex >= myLangs.keys.size )  0 else ndex

        return myLangs.getValue( myLangs.keys.elementAt( ndex ))
    }

    // getDefaultLanguage -- returns the default language tree
    fun getDefaultLanguage() = myLangs.getValue(defaultCode)

    // getLanguageByKey  -- returns the given language tree by the key
    fun getLanguageByKey(key : String) : LangData {
        return if ( myLangs.containsKey(key)) myLangs.getValue(key) else getDefaultLanguage()
    }

    // setDefaultByKey  -- finds a language tree by the key,
    // sets it as default & returns it
    fun setDefaultByKey(key : String) : LangData {
        return setnewCodes( getLanguageByKey( key ))
    }

    // ==========================================================================
    companion object {

        // setupLanguages  -- constructor for LanguageControl object
        // MUST BE CALLED to get language trees input and setup
        // args:
        //   langList: stack list of filenames for language config files
        //   dummyDict: internal dummy dictionary in case of setup errors
        //              ensures that we'll always have valid language tree
        // returns the newly created object itself
        suspend fun setupLanguages( langList: Stack<String>, dummyDict : String ) : LanguageControl {
            val mySelf = initLanguages( langList, LanguageControl() )

            // just make sure we're totally valid always; else do a dummy lang tree setup
            if ( mySelf.meEmpty || mySelf.defaultCode.isEmpty() ) nofilework(mySelf, dummyDict)
            return mySelf
        }  // setupLanguages

        // initLanguages  -- recursively set up each language tree in configuration list
        // args:
        //   langList: stack list of filenames for language config files
        //   mySelf: this object LanguageControl
        // returns:  mySelf
        private suspend fun initLanguages(langList: Stack<String>, mySelf : LanguageControl ) : LanguageControl {
            val fn = langList.pop() ?: return mySelf // pop an element; unless end of list, return -->>>>

            if( fn.isNotEmpty() )  {  // only handle nonEmpty filenames
                val aLang = LangData.langDataSetup(fn)  // get/process a language tree

                // check for duplicate langCode; WARNING if found
                if ( mySelf.myLangs.containsKey(aLang.langCode) )  {
                    mySelf.warnStatus( "LangControl: duplicate language code [$aLang.langCod]; overwriting previous language" )
                }

                mySelf.myLangs[aLang.langCode] = aLang   // add Map for new language
                     // set defaultLang to first language tree
                if (mySelf.defaultCode.isBlank()) mySelf.setnewCodes( aLang )
            }
            return initLanguages(langList, mySelf)  // recurse to handle rest of list
            // fall thru to return  <<<<<<<<<<<<---
        } // initLanguages

        // nofilework  -- does the work of the nofile situation setup
        //   returns the LanguageControl object
        private fun nofilework( mySelf: LanguageControl, dummyDict: String ) : LanguageControl {
            val dummyLD = LangData.nofilesetup(dummyDict) // set up the dummy dictionary

            mySelf.setnewCodes(dummyLD)   // 1st setup the newcodes;
            // must happen before the lvalue key evaluation pickup below
            mySelf.myLangs[ mySelf.defaultCode ] = dummyLD   // add to myLangs map[langCode,lang]

            return mySelf
        }

        // nofilesetup  -- sets up the dummy dictionary & language;
        // returns new LanguageControl object
        fun nofilesetup( dummyDict : String ) : LanguageControl {
            return nofilework( LanguageControl(), dummyDict )
        }  // nofilesetup

    }  // companion object

}  // LanguageControl