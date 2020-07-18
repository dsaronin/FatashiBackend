package org.umoja4life.fatashibackend

interface PlatformIO {
    val myPath: String

    fun lineoutInfo(s: String)   // simple line of info
    fun lineoutError(s: String)  // simple line of error info

    fun infoAlert(s: String)     // info for an alert-type box

    // massages and returns list of items: readLine()?.trim()?.split(' ') ?: listOf("exit")
    fun getCommandLine(prompt: String) :  List<String>   // prompt & get response

    fun listout(l:List<String>)   // display list of strings

    fun getJSONConfig(f:String) : ConfigProperties    // get JSON for config properties
    fun getJSONKamusiFormat(f:String) : KamusiFormat  // get JSON for KamusiFormat

    fun getKamusiText(f:String) : String  // read a kamusi file

}  // Interface