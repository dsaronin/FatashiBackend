package org.umoja4life.fatashibackend

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

        // getMyVersion -- returns formatted appname + version info
        fun getMyVersion() = "${MyEnvironment.myProps.appName} ${toVersion()} "

        // printMyVersion  -- and anything else supplied
        fun printMyVersion(strsuffix: String) {
            MyEnvironment.myPlatform.infoAlert("${getMyVersion()} " + strsuffix)
        }
    }
}
