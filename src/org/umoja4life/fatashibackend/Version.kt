package org.umoja4life.fatashiBackend

// Version -- manages version numbering for the application
enum class Version(val vsn: Int)  {
    MAJOR_VERSION(_MAJOR_VERSION),
    MINOR_VERSION(_MINOR_VERSION),
    PATCH_VERSION(_PATCH_VERSION);

    companion object {

     // toVersion -- class-level function to return version number as string
        fun toVersion(): String {
            return enumValues<Version>().joinToString(".","v") { it.vsn.toString()  }
        }

    // toComment -- class-level function to return version comment
         fun toComment(): String {
            return if( _VERSION_COMMENT.isBlank() ) "" else "[$_VERSION_COMMENT]"
        }

        // printMyVersion  -- and anything else supplied
        fun printMyVersion(strsuffix: String) {
            printInfo("\n${MyEnvironment.myProps.appName} ${toVersion()} " + strsuffix)
        }
    }
}
