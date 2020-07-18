package org.umoja4life.fatashibackend

// FatashiBackend -- a dictionary search & display package
private const val DEBUG  = false

// global expressions for output via Platform implementation interface
// example: MyEnvironment.myPlatform.lineoutInfo
fun printInfo(s: String)   = MyEnvironment.myPlatform.lineoutInfo(AnsiColor.wrapCyan(s))
fun printPrompt(s: String) = print(AnsiColor.wrapYellow(s))
fun printVisual(s: String) = print(AnsiColor.wrapGreen(s))
fun printWarn(s: String)   = MyEnvironment.myPlatform.lineoutInfo(AnsiColor.wrapGreenBold(s))
fun printError(s: String)  = MyEnvironment.myPlatform.lineoutError(AnsiColor.wrapRedBold(s))

// **************************************************************************
// FatashiWork -- SINGLETON *************************************************
// **************************************************************************

object FatashiWork  {

    private val helpList = "  tafuta, methali, list, browse, sts, options, help, quit, exit"

    // setupWork -- get things started, say hello to user
    fun setupWork() {
        Version.printMyVersion(" starting...")
    }

    // closedownWork -- shut things down; say good-bye to user
    fun closedownWork() {
        printInfo("...ending ${MyEnvironment.myProps.appName}")
    }

    // fatashi work loop: prompt, get input, parse commands
    fun work() {
        do {
            printPrompt("${MyEnvironment.myProps.appName} > ")  // command prompt
        } while ( parseCommands(
                        readLine()?.trim()?.split(' ') ?: listOf("exit")
                ) )
    } // fun work

    // parseCommands -- parses the command string and executes commands
    fun parseCommands( cmdlist: List<String> ): Boolean {
        var loop = true                 // user input loop while true
        var useKamusi: Kamusi? = null   // assume this isn't a search command
        var dropCount = 1               // assume need to drop cmd from list head

        // parse command
        when ( val cmd = cmdlist.first().trim() ) {
            "x", "ex", "exit"       -> loop = false   // exit program
            "q", "quit"             -> loop = false  // exit program

            // search dictionary OR methali
            "tafuta"                -> useKamusi = selectKamusi(1)
            "t","tt","ttt","tttt"   -> useKamusi = selectKamusi(cmd.length)

            "methali"               -> useKamusi = selectMethali(1)
            "m","mm","mmm","mmmm"   -> useKamusi = selectMethali(cmd.length)

            // list methali
            "ml","mll","mlll","mllll"  -> selectMethali(cmd.length - 1)?.listRandom()

            // list dictionary
            "list"                  -> selectKamusi(1)?.listRandom()
            "l","ll","lll","llll"   -> selectKamusi(cmd.length)?.listRandom()

            // browse from an item
            "browse"  -> selectKamusi(1)?.browsePage( cmdlist.drop(dropCount) )
            "b","bb","bbb","bbbb"   -> selectKamusi(cmd.length)
                    ?.browsePage( cmdlist.drop(dropCount) )

            // dict/methali status
            "s", "sts", "status" -> MyEnvironment.kamusiHead?.printStatus()
            "ms"                       -> selectMethali(1)?.printStatus()

            "f", "flags"     -> MyEnvironment.printOptions()  // list options
            "h", "help"      -> MyEnvironment.myPlatform.infoAlert(helpList)
            "v", "version"   -> Version.printMyVersion(" ")
            "o", "options"   -> MyEnvironment.printOptions()

            ""               -> loop = true   // empty line; NOP
            else        -> {  // treat it as tafuta lookup request
                useKamusi = MyEnvironment.kamusiHead
                dropCount = 0  // don't strip off a command
                if (DEBUG) MyEnvironment.printUsageError("$cmd is unrecognizable")
            }
        }

        // useKamusi will be non-null if a search command was encountered
        useKamusi?.searchKeyList( cmdlist.drop(dropCount) )   // do the search on key item list

        return loop
    }

    // selectKamusi  -- jump to a specific kamusi chain level
    private fun selectKamusi( n: Int ) : Kamusi? {
        var level = n
        var kamusi =
                if (MyEnvironment.myProps.prodFlag) MyEnvironment.kamusiHead else MyEnvironment.testHead

        // loop thru looking at deeper levels as long as available
        while (level > 1 && kamusi?.nextKamusi != null ) {
            kamusi = kamusi?.nextKamusi
            level--
        }
        return kamusi
    }

    // selectMethali  -- jump to a specific methali chain level
    private fun selectMethali( n: Int ) : Kamusi? {
        var level = n
        var kamusi = MyEnvironment.methaliHead

        // loop thru looking at deeper levels as long as available
        while (level > 1 && kamusi?.nextKamusi != null ) {
            kamusi = kamusi?.nextKamusi
            level--
        }
        return kamusi
    }

}  // object FatashiWork
