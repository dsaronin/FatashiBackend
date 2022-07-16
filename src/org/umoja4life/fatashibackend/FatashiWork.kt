package org.umoja4life.fatashibackend

// FatashiBackend -- a dictionary search & display package
private const val DEBUG  = false

// global expressions for output via Platform implementation interface
// example: MyEnvironment.myPlatform.lineoutInfo
fun printInfo(s: String)   = MyEnvironment.myPlatform.lineoutInfo(AnsiColor.wrapCyan(s))
fun printPrompt(s: String) = MyEnvironment.myPlatform.putPrompt(AnsiColor.wrapYellow(s))
// fun printVisual(s: String) = print(AnsiColor.wrapGreen(s))
fun printWarn(s: String)   = MyEnvironment.myPlatform.lineoutInfo(AnsiColor.wrapGreenBold(s))
fun printError(s: String)  = MyEnvironment.myPlatform.lineoutError(AnsiColor.wrapRedBold(s))

// **************************************************************************
// FatashiWork -- SINGLETON *************************************************
// **************************************************************************

object FatashiWork  {

    private val helpList = "  tafuta (t), methali (m), list (l), browse (b), status (s), " +
            "options (o), langctl (&), help, quit, exit"

    // setupWork -- get things started, say hello to user
    fun setupWork() {
        Version.printMyVersion(" starting...")
    }

    // closedownWork -- shut things down; say good-bye to user
    fun closedownWork() {
        printInfo("...ending ${MyEnvironment.myProps.appName}")
    }

    // fatashi work loop: prompt, get input, parse commands
    // OLD: printPrompt("${MyEnvironment.myProps.appName} > ")  // command prompt
    fun work() {
        do {
            printPrompt("${MyEnvironment.myLanguage.prompt()} > ")  // command prompt
        } while ( parseCommands(MyEnvironment.myPlatform.getCommandLine()) )
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
            "sts", "status" -> selectKamusi(1)?.printStatus()
            "s","ss","sss","ssss"   -> selectKamusi(cmd.length)?.printStatus()
            "ms"                       -> selectMethali(1)?.printStatus()

            "f", "flags"     -> MyEnvironment.printOptions()  // list options
            "h", "help"      -> MyEnvironment.myPlatform.infoAlert(helpList)
            "v", "version"   -> Version.printMyVersion(MyEnvironment.myPlatform.getFrontVersion())
            "o", "options"   -> MyEnvironment.printOptions()

            "&", "langctl" -> {
                MyEnvironment.myLanguage.toggleLanguage()
                useKamusi = selectKamusi(1)   // search for any other key items in list
            }

            "&&", "langctl" -> {
                if (cmdlist.size > 1) {  // process 2d element as a language code
                    MyEnvironment.myLanguage.setDefaultByKey( cmdlist[1].trim() )
                }
                else {  // langcode arg missing; just toggle the languages
                    MyEnvironment.myLanguage.toggleLanguage()
                }

            }

            ""               -> loop = true   // empty line; NOP
            else        -> {  // treat it as tafuta lookup request
                useKamusi = selectKamusi(1)
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
            if (MyEnvironment.myProps.prodFlag  &&
                MyEnvironment.myLanguage.getDefaultLanguage().kamusiHead != null
            ) MyEnvironment.myLanguage.getDefaultLanguage().kamusiHead else MyEnvironment.myLanguage.getDefaultLanguage().testHead

        // loop thru looking at deeper levels as long as available
        while (level > 1 && kamusi?.nextKamusi != null ) {
            kamusi = kamusi.nextKamusi
            level--
        }
        return kamusi
    }

    // selectMethali  -- jump to a specific methali chain level
    private fun selectMethali( n: Int ) : Kamusi? {
        var level = n
        var kamusi = MyEnvironment.myLanguage.getDefaultLanguage().methaliHead

        // loop thru looking at deeper levels as long as available
        while (level > 1 && kamusi?.nextKamusi != null ) {
            kamusi = kamusi.nextKamusi
            level--
        }
        return kamusi
    }

}  // object FatashiWork
