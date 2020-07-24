package org.umoja4life.fatashibackend

import java.io.File

fun Boolean.toChar() = this.toString().first()

// **************************************************************************
// **************************************************************************
const val CONFIG_PROPERTIES_FILE ="config_properties.json"

// **************************************************************************
// default parameters; not to be used outside of MyEnvironment
// **************************************************************************

private const val ANCHOR_HEAD = '^'     // pattern anchor for head of FIELD_KEY
private const val ANCHOR_TAIL = '$'     // pattern anchor for tail of FIELD_USG

object MyEnvironment {
    // **************************************************************************
    // ******** MyEnvironment public properties  ********************************
    // **************************************************************************

    // myProps is read in from config_properties.json when singleton first accessed
    lateinit var myProps : ConfigProperties
    lateinit var myPlatform : PlatformIO
    val anchorHead       = ANCHOR_HEAD
    val anchorTail       = ANCHOR_TAIL
    var kamusiHead:  Kamusi? = null
    var methaliHead: Kamusi? = null
    var testHead:    Kamusi? = null

    private var kamusiFormatList: Stack<KamusiFormat> = mutableListOf<KamusiFormat>()
    private var methaliFormatList: Stack<KamusiFormat> = mutableListOf<KamusiFormat>()
    private var testFormatList: Stack<KamusiFormat> = mutableListOf<KamusiFormat>()

    // **************************************************************************
    // **************************************************************************

    // setup -- initializes the environment
    // args -- are the cli argument list when invoked
    // platformIO -- the platform implementation for I/O: Linux or Android
    // NOTE: expected to be called within CoroutineScope
    suspend fun setup(args: Array<String>, platformIO: PlatformIO): Unit {
        myPlatform = platformIO  // MUST ALWAYS BE DONE FIRST!!!
            // vvvvvv  MUST ALWAYS BE DONE SECOND!!!  vvvvvvv
        myProps = ConfigProperties.readJsonConfigurations(CONFIG_PROPERTIES_FILE)

        parseArgList(args)

        if (myProps.debugFlag) {
            printArgList(args)
            printOptions()
        }

        // get the various KamusiFormat files for each kamusi
        KamusiFormat.kamusiFormatSetup(myProps.kamusiList, kamusiFormatList)
        KamusiFormat.kamusiFormatSetup(myProps.methaliList, methaliFormatList)
        KamusiFormat.kamusiFormatSetup(myProps.testList, testFormatList)

        // process the KamusiFormat files to create kamusi objects
        kamusiHead = Kamusi.kamusiSetup(kamusiFormatList)
        methaliHead = Kamusi.kamusiSetup(methaliFormatList)
        testHead = Kamusi.kamusiSetup(testFormatList)
    }

        // replacePlatform  --
        // each Android Activity lifecycle requires a refreshed AndroidPlatform
        // NEEDS to be done when other requests are not pending
    fun replacePlatform(platformIO: PlatformIO) {
        myPlatform = platformIO
    }

    // regex recognizer for short/long flags and extracts the flag w/o punctuation
    private val flag_regex = Regex("""--?(\w\b|\w+)""")

    // parseArgList -- parse the command line argument option flags
    // ex: -v -n 5 -d --version --kamusi1 "dsa_dictionary.txt"
    fun parseArgList(args: Array<String>) {
        if( args.isEmpty() ) return

        val lifo = stackOf<String>()

        // build from args LIFO stack in reverse (right-to-left) for top-down parsing
        for( i in args.indices.reversed() ) lifo.push( args[i] )

        // now parse stack (LIFO == args left-to-right)
        while(lifo.isNotEmpty()) {
            val flagArg = lifo.pop() ?: ""

            val matches = flag_regex.findAll( flagArg )  // recognize & extract
            val m=matches.firstOrNull()
            if (m == null) {
                printArgUsageError("invalid flag syntax: $flagArg")
            }
            else {
                // extract the flag (it will be in either of the two groupings, but not both
                val flag = if(m.groupValues[1].isNotEmpty()) m.groupValues[1] else m.groupValues[2]

                // flag is now the extracted flag
                when (flag) {
                    "n"             -> myProps.setLineCount(popValueOrDefault(lifo, LIST_LINE_COUNT.toString()))
                    "v", "verbose"  -> myProps.verboseFlag = true
                    "d", "debug"    -> myProps.debugFlag = true
                    "p", "prod"     -> myProps.prodFlag = true
                    "h", "help"     -> printHelp()
                    "version"       -> Version.printMyVersion(" ")
//                   "kamusi1"  -> kamusiMainFile = popFileNameOrDefault(lifo, productionFile )
//                   "kamusi2"  -> kamusiStdFile = popFileNameOrDefault(lifo,KAMUSI_STANDARD_FILE)
//                   "methali1" -> methaliStdFile = popFileNameOrDefault(lifo,METHALI_STANDARD_FILE)

                    else -> printArgUsageError("unknown flag: $flag")
                }
            }

        }
    }

    // printUsageError -- print an error, wrapped in Red
    fun printUsageError(s: String) {
        printError("***** $s *****")
    }

    // **************************************************************************
    // *****  private internal MyEnvironment Functions  *************************
    // **************************************************************************

    // printArgList -- outputs the command line argument option flags
    private fun printArgList(args: Array<String>) {
        var list = mutableListOf( if( args.isEmpty() ) "No args passed." else "My calling args are...")
        for (i in args.indices ) list.add("args[$i] is: ${args[i]}")
        myPlatform.infoAlert(list)
    }

    // printOptions -- display the current state of options
    fun printOptions() {
        val optionList = "  verbose (%c), debug (%c), prod (%c) list n(%d)"

        myPlatform.infoAlert(
                optionList.format(
                        myProps.verboseFlag.toChar(),
                        myProps.debugFlag.toChar(),
                        myProps.prodFlag.toChar(),
                        myProps.listLineCount
                )
        )

    }

    // printHelp  -- outputs std arg line expected
    private fun printHelp() {
        val argLine = "\$ ${myProps.appName} [<options>] \n  <options> ::= -v -d -n dddd --version --help \n  -v: verbose, -d: debug traces, -n: dictionary list lines <nn>"

        printInfo("Usage and Argument line expected:\n$argLine")
    }

    // popValueOrDefault -- peeks ahead on LIFO and pops if valid value; else default
    private fun popValueOrDefault(lifo: Stack<String>, default: String): Int {
            // peek at top of stack; if missing, use default
        var num = lifo.peek() ?: default

            // if it matches another flag, use default && dont pop
        if ( flag_regex.matches( num ) ) {
            num = default
        }

        else {
            lifo.pop()   // then pop from stack since it wasn't the next flag

            // but if it doesn't match a number
            if ( ! """^\d+$""".toRegex().matches(num) ) {
                   // it was an argument format error
                printArgUsageError(num)  // warn user
                num = default   // use default
            }
        }

        return num.toInt()
    }

    // popFileNameOrDefault  -- peeks ahead at LIFO && pops valid filename, else default
    private fun popFileNameOrDefault(lifo: Stack<String>, default: String): String {
        // peek at top of stack; if missing, use default
        var str = lifo.peek() ?: default

        // if it matches another flag, use default && dont pop
        if ( flag_regex.matches( str ) ) {
            str = default
        }

        else {
            lifo.pop()   // then pop from stack since it wasn't the next flag

            // but if it doesn't actually match a file on the system...
            if ( ! File( str ).exists() ) {
                // filename wasn't valid or doesn't exist
                printArgUsageError("filename <$str> isn't valid or doesn't exit")  // warn user
                str = default   // use default
            }
        }

        return str
    }

    // ******** output usage error information ***********

    private fun printArgUsageError(s: String) {
        printUsageError("Command line arg input error: $s")
    }

    fun printWarnIfDebug(s: String) {
        if( myProps.debugFlag ) printWarn(s)
    }

}
