package org.umoja4life.fatashibackend

// LangProcessing is the supertype for all language
// search key pre/post processing modules
//
// subtypes need to extend this LangProcessing
interface LangProcessing {

    fun preProcessKey(key: String) : String

    fun postProcessKey(key: String) : String

}