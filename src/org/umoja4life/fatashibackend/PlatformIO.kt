package org.umoja4life.fatashibackend

interface PlatformIO {
    val myPath: String

    fun lineoutInfo(s: String)
    fun lineoutPrompt(s: String)
    fun lineoutVisual(s: String)
    fun lineoutWarn(s: String)
    fun lineoutError(s: String)

    fun statusAlert(s: String)

    fun getCommandLine(prompt: String) : String

    fun listout(l:List<String>)

    fun getJSONConfig(f:String) : ConfigProperties
    fun getJSONKamusiFormat(f:String) : KamusiFormat

    fun getKamusiText(f:String) : String

}  // Interface