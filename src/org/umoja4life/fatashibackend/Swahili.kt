package org.umoja4life.fatashibackend

// Swahili  -- control everything swahili-specific
// note that English-words should be escaped with a ";" as the first symbol
// thus preventing any turkish substitutions
// both of these are invoked prior to dictionary lookup
// but the PRE refers to processing that takes place PRIOR to common search key massaging
// and the POST refers to processing that takes place AFTER the common search key massaging

object Swahili : LangProcessing {

    // preProcessKey  -- massage typical swahili ambiguities
   override fun preProcessKey(key: String): String {
       return key.replace("^ma".toRegex(RegexOption.IGNORE_CASE), "\\\\b(ma)?")  // [li-ya] sing/plurals
                 .replace("^mi".toRegex(RegexOption.IGNORE_CASE), "\\\\bmi?")    //  [u-i] sing/plurals
                 .replace("^vi".toRegex(RegexOption.IGNORE_CASE), "\\\\b[kv]i") //  [ki-vi] sing/plurals
                 .replace("^-ji".toRegex(RegexOption.IGNORE_CASE), "-(ji)?") //  [ki-vi] sing/pluralss
                 .replace("ni$".toRegex(RegexOption.IGNORE_CASE), "(ni)?") //  trailing place suffix

   }

    // postProcessKey  -- relaxes swahili dialectical differences
   override fun postProcessKey(key: String): String {
       return key.replace("l|r".toRegex(RegexOption.IGNORE_CASE), "[lr]")
               .replace("z".toRegex(RegexOption.IGNORE_CASE), "(z|dh)")
               .replace("^mu".toRegex(RegexOption.IGNORE_CASE), "m[uw]")
               .replace("^h".toRegex(RegexOption.IGNORE_CASE), "h?")
   }
}    // Swahili singleton