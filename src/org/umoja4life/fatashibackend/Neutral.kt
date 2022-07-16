package org.umoja4life.fatashibackend

// Neutral is the default pre/post processing for an undefined language
object Neutral  : LangProcessing {
        // but the PRE refers to processing that takes place PRIOR to common search key massaging
        // and the POST refers to processing that takes place AFTER the common search key massaging

        // preProcessKey -- nop
        override fun preProcessKey(key: String) = key

        // postProcessKey -- nop
        override fun postProcessKey(key: String) = key

}  // Neutral singleton