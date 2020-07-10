package org.umoja4life.fatashiBackend

import java.io.File
// copyright

/***************************************************************************************
 *     explanation for itemRegex (below): syntax for "commands" to kamusi parser
 *     either completely escape the item:
 *     /aaaaaa/ escapes item 'aaaaaa' literally to allow inclusion of RegEx operators
 *
 *     or use following syntax:
 *     ; -- will be used to indicate non-word boundary anchor ( \W )
 *          either prefixing or suffixing the item: ;aaa, aaaa;, or ;aaaa;
 *     : -- future
 *     ;; - future: anchor to head of line
 *
 *     within the item itself:
 *     prefix - is used in kamusi to show a verb stem
 *     prefix ~ is used in kamusi to show an adjectival stem
 *     all _ (underscores) are replaced with spaces, to allow multiple words in search pattern
 *     ' is a normal swahili indicator for syllable ng' : -ng'ang'ania  -- grip sth tightly
 *     Future
 *          +  -- will be used to delimit a verb stem, followed by conjunction
 *                allowing swahili smart verb processing (see elsewhere)
 *                -kom+esha, from -koma (stem is -kom+a)
 *
 *     keyModifiers come after the item and act to constrain the search
 *          #abc  -- constrains matches only to records with (abc) qualifier in field 2: (tech)
 *          %n    -- constrains the search to only field n of each record
 *          &     -- invoke swahili smart processing (see elsewhere)
 *          @     -- future
 **********************************************************************/


// Dictionary handles everything re dictionary database, but has no language-specific logic
// requires a KamusiFormat object with kamusi-specific parameters
data class Kamusi( val myKamusiFormat: KamusiFormat)  {

    var nextKamusi: Kamusi? = null  // next in chain of kamusi's
    var lastBrowseIndex: Int = 0    // remember our last browse index
    private val dictionary: List<String>    // kamusi data

    private val fieldDelimiter: Regex   // used to massage field delimiters to a standard
    private val keyModifiers = "#%&@"  // symbols to constrain searching
    private val escapeLiteral = "/"    // escapes a search term to force as-is
        // itemRegex below values kamusi search item requests  (see above comments)
    private val itemRegex = "(^$escapeLiteral.+$escapeLiteral$|[-~;:]?[\\w'+]+[;:]?)(\\W|[$keyModifiers])?(\\w+)?"

    private val internalFields = "\t"    // our standard for field delimiters
    private val recordDelimiter = "\n"   // our standard for record delimiters
    private val showKeyDelim = "\t-- "   // our output standard to delimit fields
    private val spaceReplace = "_"       // underscores in keys are replaced by space

// initialize by opening file, reading in raw dict, parsing fields, and splitting into records
    init {
            // open file
        val kamusiFile = File(myKamusiFormat.filename)
    MyEnvironment.printWarnIfDebug("Opening Kamusi: $kamusiFile")
            // make regex pattern for replacing with std field delimiters
        fieldDelimiter = Regex( myKamusiFormat.fieldDelimiters )
            // read entire dict, replace all field delims with tab
            // then split into list of individual lines
        dictionary = fieldDelimiter.replace(
                kamusiFile.readText(),
                internalFields
        ).split(recordDelimiter)

    }  // end init class

//************************************************************************************
//****** class-level methods         *****************************************************
//************************************************************************************
companion object {
    
    // kamusiSetup -- recursively set up a list of kamusi's
    // return null if no more in list; else return the kamusi set up
    fun kamusiSetup( kfList: Stack<KamusiFormat>): Kamusi? {
        
        val myFormat = kfList.pop() ?: return null  // end recursion at list end

        val myKamusi = Kamusi(myFormat)  // load and setup this kamusi

            // setup remainder of list returning my child kamusi
        myKamusi.nextKamusi = kamusiSetup(kfList)  // remember child

        return myKamusi   // return myself
    }
} // end companion

//************************************************************************************
//****** utility methods         *****************************************************
//************************************************************************************

    // printStatus -- output status of dictionary
    fun printStatus() {
        printInfo("  ${myKamusiFormat.filename} has ${dictionary.count()} entries")
    }

    // listRandom -- output a random page of dictionary internal representation
    fun listRandom() {
        val totalPages: Int = dictionary.count() / MyEnvironment.myProps.listLineCount
        val startPage: Int = (1..totalPages).random()
        val startPageIndex = (startPage-1) * MyEnvironment.myProps.listLineCount

        listPage(startPage, startPageIndex)
    }

    // listPage -- output a specific page of the dictionary starting at index
    private fun listPage( page: Int, index: Int) {
        printInfo("Page $page from ${myKamusiFormat.filename}:")
            // sanity check on endIndex to make sure doesn't exceed number of lines
        var endIndex = index+ MyEnvironment.myProps.listLineCount
        if (endIndex >= dictionary.count()) endIndex = dictionary.count() - 1

        // for (idx in (index until endIndex)) println( dictionary[idx] )
        // use printResults to highlight the entry item
        printResults(
                dictionary.slice((index until endIndex)),
                """^([-~]?\w+[',’`]?\s?)+""".toRegex(RegexOption.IGNORE_CASE),
                false
        )
    }

    private fun advanceBrowserPage(): Int {
        val index = lastBrowseIndex + MyEnvironment.myProps.listLineCount - 1
        val maxindex = (dictionary.count() - MyEnvironment.myProps.listLineCount)

        return if (index <= maxindex) index else maxindex
    }

    // browsePage -- output a dictionary page starting at first search key match
    fun browsePage( keyList: List<String> )  {
        val index = if (keyList.isEmpty() ) advanceBrowserPage() else {
            jumpToPage(buildPattern( keyList.first() ).toRegex(RegexOption.IGNORE_CASE))
        }
        if (index < 0) {  // key not found
            printWarn("Couldn't find any entries starting with: ${keyList.first()}")
        }
        else {
            lastBrowseIndex = index  // remember from where we last searched
            listPage((index / MyEnvironment.myProps.listLineCount), index)
        }
    }

    // buildPattern -- builds a browse search pattern from the given key
    // ex: if key is ABCD, then returns: "^[-~]?ABCD"
    // this pattern will be used to find the largest possible initial sequence of letters
    private fun buildPattern(key: String): String {
        return "^[-~]?$key"
    }

    // jumpToPage -- find the starting page of entries to match search pattern
    private fun jumpToPage( itemRegex: Regex ): Int {
        if (MyEnvironment.myProps.debugFlag) printInfo("Browse key: $itemRegex")
        // try this dictionary
        val index = dictionary.indexOfFirst { itemRegex.containsMatchIn(it) }
        // return whatever we've found, possibly -1: no match
        if (MyEnvironment.myProps.debugFlag) printInfo("Browse index: $index")
        return index
    }

//************************************************************************************
//****** search methods         *****************************************************
//************************************************************************************
/*
*   m=matches.firstOrNull()
*   m.groupValues[1]
 */
    // searchKeyList -- searches dictionary using List of Keys
    fun searchKeyList(wordList: List<String>) {
        // for each search key in list, search the dictionary
        for (item in wordList) {
            printDivider(">>>>>>>>> $item >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" )
                // strip off the key fragment from the constraints
            val keyfrag = itemRegex.toRegex().find(item) ?: continue
                // search the dictionary for that key fragment, after prepping the key
                /* ****************************************************************
                 * at this point, keyfrag has:
                 *     keyfrag.groupValues[0] -- full search term
                 *     keyfrag.groupValues[1] -- key
                 *     keyfrag.groupValues[2] -- constraint code: #, %, &, @, ...
                 *     keyfrag.groupValues[3] -- constraint parameter: abcd, nn
                 * ****************************************************************
                 */
            var keyitem = prepKey( keyfrag.groupValues[1] )  // keyitem is the basic key we're looking for
            var typeConstraint = ""  // default is no type constraint

            // handle any kind of constraint forming a regex search pattern
            val pattern = when (keyfrag.groupValues[2]) {
                "#"  -> {
                        typeConstraint = prepTypeConstraint( keyfrag.groupValues[3] )
                        keyitem   // return value from block
                        }
                "%"  -> prepByField(keyitem, keyfrag.groupValues[3])  // returns updated keyitem
                "&"  -> {
                    keyitem = Swahili.postProcessKey(keyitem) // we want this to be the value highlighted!
                    keyitem
                }   // returns updated keyitem
                "@"  -> keyitem   // NOP for now; future expansion
                else -> keyitem
            }

            findByEntry( pattern, keyitem, typeConstraint )  // perform search, output results
        }
    }

    private fun printDivider(s: String) {
        if (MyEnvironment.myProps.verboseFlag) printVisual(s)
    }

    // findByEntry  -- searches all entries and returns list of matching entries
    // args:
    //   pattern: string of regex search pattern
    //   item: basic item (used for highlighting output)
    //   constraint: constraint to further limit the result list unless constraint.isEmpty()
    fun findByEntry(pattern: String, item: String, constraint: String) {
        // display the search pattern in a divider line
        printDivider(">|$pattern|<<<<$constraint\n")
        val itemRegex = pattern.toRegex(RegexOption.IGNORE_CASE)  // convert key to regex

        // filter dictionary grabbing only records with a match
        val resList = getResults(itemRegex )  // start with this dictionary and follow chain

        printResults(
                if( constraint.isEmpty() ) resList else {
                    val conregex = constraint.toRegex(RegexOption.IGNORE_CASE)
                    resList.filter { conregex.containsMatchIn(it) }
                },
                item.toRegex(RegexOption.IGNORE_CASE)
        )  // display results, if any found
    }

    // getResults  -- RECURSIVELY check all dictionaries in list until results found
    private fun getResults( itemRegex: Regex ): List<String> {
        var resList: List<String>

            // try this dictionary first
        resList = dictionary.filter { itemRegex.containsMatchIn(it) }
                // if nothing found and there's still a dictionary to be checked...
            if( resList.isEmpty() && nextKamusi != null ) {
                    // search in it for a result
                resList = nextKamusi!!.getResults( itemRegex )
            }
        return resList  // return whatever we've found, possibly empty
    }

    // printResults  -- handles all output for found items
    // output consists in pretty formatting dictionary entry, then highlighting found items
    // args:
    //   res: list of dictionary entries with at least one match
    //   rex: the regex of the search key determining that match
    fun printResults( res: List<String>, rex: Regex, divider: Boolean = true ){
        res.forEach {
            // output each line after highlighting the found text
            println( it
                    .replace(internalFields, showKeyDelim)
                    .replace(rex) { AnsiColor.wrapBlueBold(it.groupValues[0]) } )
        }
        if (divider)  printDivider( "${res.size} results\n")
    }

     /*
     * ***********************************************************************
     * ******** massaging key & prep functions    ****************************
     * ***********************************************************************
     */

    // prepKey -- massages key to add/remove based on special symbols
    // NOTE: by this point, mid-term ';' and ':' have been parsed out,
    //       so if present, they will only be leading and trailing
    private fun prepKey(s: String): String {
        return Swahili.preProcessKey(s)
                .replace(";".toRegex(), """\\b""")  // non-word boundaries
                .replace(spaceReplace.toRegex(), " ")     // underscore ==> space
                .trim()         // lead/trailing spaces
                .removeSurrounding(escapeLiteral, escapeLiteral)   // remove literal escape chars
    }

    // handleByField  -- massage key to constrain the search to a given dict field
    // also handles the search & printout
    private fun prepByField(keyitem: String, dfield: String): String {
        return when (dfield) {
            "1"  -> constrainToKeyField( keyitem )
            "2"  -> constrainToDefField( keyitem )
            "3"  -> constrainToUsageField( keyitem )
            else -> constrainToKeyField( keyitem )
        }
    }

    // prepConstraint  -- preps a constraint for the search
    private fun prepTypeConstraint(cfield: String): String {
        return ( if(cfield.isEmpty()) "" else constrainToDefField("($cfield)" ))
    }

    // findByKey  -- searches Key field in all entries and returns list of matching entries
    // wrap pattern with FIELD_KEY_HEAD unless pattern begins with "^"
    // and FIELD_KEY_TAIL
    private fun constrainToKeyField(pattern: String): String {
        return (
            if( pattern.first().equals(MyEnvironment.anchorHead)
        ) "" else myKamusiFormat.wrapKeyHead ) + pattern + myKamusiFormat.wrapKeyTail

    }

    // findByDefinition  -- searches Definition field in all entries and returns list of matching entries
    // wrap pattern with FIELD_DEF
    private fun constrainToDefField(pattern: String): String {
        return ( myKamusiFormat.wrapDefHead + pattern + myKamusiFormat.wrapDefTail)
    }

    // findByUsage  -- searches Usage field in all entries and returns list of matching entries
    // wrap pattern with FIELD_USG_HEAD
    // and FIELD_USG_TAIL, unless pattern ends with "$"
    private fun constrainToUsageField(pattern: String): String {
        return (
                myKamusiFormat.wrapUsgHead + pattern +
                ( if( pattern.last().equals(MyEnvironment.anchorTail) ) "" else myKamusiFormat.wrapUsgTail )
        )
    }


}  // end class Dictionary