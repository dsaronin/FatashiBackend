package org.umoja4life.fatashibackend

// Turkish is the pre/post search key processing for Turkish language
object Turkish  : LangProcessing {
    // note that English-words should be escaped with a ";" as the first symbol
    // thus preventing any turkish substitutions
    // both of these are invoked prior to dictionary lookup
    // but the PRE refers to processing that takes place PRIOR to common search key massaging
    // and the POST refers to processing that takes place AFTER the common search key massaging

    // preProcessKey  -- massage typical turkish ambiguities
    override fun preProcessKey(key: String): String {
        return key
            .replace("(l[ae]r)$".toRegex(RegexOption.IGNORE_CASE), "($1)?") //  trailing place suffix

    }

    // postProcessKey  -- relaxes turkish unique letters
    override fun postProcessKey(key: String): String {
        return key
            .replace("s|ş".toRegex(RegexOption.IGNORE_CASE), "[sş]")
            .replace("c|ç".toRegex(RegexOption.IGNORE_CASE), "[cç]")
            .replace("u|ü".toRegex(RegexOption.IGNORE_CASE), "[uü]")
            .replace("o|ö".toRegex(RegexOption.IGNORE_CASE), "[oö]")
            .replace("i|ı".toRegex(RegexOption.IGNORE_CASE), "[iı]")
            .replace("g|ğ".toRegex(RegexOption.IGNORE_CASE), "[gğ]")
            .replace("[éèėêë]".toRegex(RegexOption.IGNORE_CASE), "[e]")
            .replace("[áàȧâä]".toRegex(RegexOption.IGNORE_CASE), "[a]")
    }

}  // Turkish singleton