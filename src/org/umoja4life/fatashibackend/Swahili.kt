package org.umoja4life.fatashibackend

// Swahili  -- control everything language-specific

object Swahili {

    // preProcessKey  -- massage typical swahili ambiguities for dictionary lookup
    // note that English-words should be escaped with a ";" as the first symbol
    // thus preventing any swahili substitutions
   fun preProcessKey(key: String): String {
       return key.replace("^ma".toRegex(RegexOption.IGNORE_CASE), "\\\\b(ma)?")  // [li-ya] sing/plurals
                 .replace("^mi".toRegex(RegexOption.IGNORE_CASE), "\\\\bmi?")    //  [u-i] sing/plurals
                 .replace("^vi".toRegex(RegexOption.IGNORE_CASE), "\\\\b[kv]i") //  [ki-vi] sing/plurals
                 .replace("^-ji".toRegex(RegexOption.IGNORE_CASE), "-(ji)?") //  [ki-vi] sing/pluralss
                 .replace("ni$".toRegex(RegexOption.IGNORE_CASE), "(ni)?") //  trailing place sufficx

   }

   fun postProcessKey(key: String): String {
       return key.replace("l|r".toRegex(RegexOption.IGNORE_CASE), "[lr]")
               .replace("z".toRegex(RegexOption.IGNORE_CASE), "(z|dh)")
               .replace("^mu".toRegex(RegexOption.IGNORE_CASE), "m[uw]")
               .replace("^h".toRegex(RegexOption.IGNORE_CASE), "h?")
   }
}