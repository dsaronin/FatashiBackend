package org.umoja4life.fatashibackend

class LinuxPlatform : PlatformIO {

    override val myPath: String
        get() = "data/"

    override fun lineoutInfo(s: String) {
        println(s)
    }

    override fun lineoutError(s: String) {
        lineoutInfo(s)
    }

    override fun infoAlert(s: String) {
        println(s)
    }

    override fun infoAlert(l: List<String>) {
        l.forEach { println(it) }
    }

    override fun getCommandLine(): List<String> {
        return readLine()?.trim()?.split(' ') ?: listOf("exit")
    }

    override fun putPrompt(prompt: String) {
        print( prompt )
    }

    override fun listout(l: List<String>) {
        TODO("Not yet implemented")
    }

    override fun getJSONConfig(f: String): ConfigProperties {
        TODO("Not yet implemented")
    }

    override fun getJSONKamusiFormat(f: String): KamusiFormat {
        TODO("Not yet implemented")
    }

    override fun getKamusiText(f: String): String {
        TODO("Not yet implemented")
    }


}