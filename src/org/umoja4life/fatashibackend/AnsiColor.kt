package org.umoja4life.fatashibackend

// ANSI escape sequences for changing display colors


enum class AnsiColor(val code: String) {
    //Color end string, color reset
    ANSI_RESET("\u001b[0m"),

    // Regular Colors. Normal color, no bold / background color etc.
    ANSI_BLACK("\u001b[0;30m"),  // BLACK
    ANSI_RED("\u001b[0;31m"),  // RED
    ANSI_GREEN("\u001b[0;32m"),  // GREEN
    ANSI_YELLOW("\u001b[0;33m"),  // YELLOW
    ANSI_BLUE("\u001b[0;34m"),  // BLUE
    ANSI_MAGENTA("\u001b[0;35m"),  // MAGENTA
    ANSI_CYAN("\u001b[0;36m"),  // CYAN
    ANSI_WHITE("\u001b[0;37m"),  // WHITE

    // Bold
    BLACK_BOLD("\u001b[1;30m"),  // BLACK
    RED_BOLD("\u001b[1;31m"),  // RED
    GREEN_BOLD("\u001b[1;32m"),  // GREEN
    YELLOW_BOLD("\u001b[1;33m"),  // YELLOW
    BLUE_BOLD("\u001b[1;34m"),  // BLUE
    MAGENTA_BOLD("\u001b[1;35m"),  // MAGENTA
    CYAN_BOLD("\u001b[1;36m"),  // CYAN
    WHITE_BOLD("\u001b[1;37m"),  // WHITE

    // Underline
    BLACK_UNDERLINED("\u001b[4;30m"),  // BLACK
    RED_UNDERLINED("\u001b[4;31m"),  // RED
    GREEN_UNDERLINED("\u001b[4;32m"),  // GREEN
    YELLOW_UNDERLINED("\u001b[4;33m"),  // YELLOW
    BLUE_UNDERLINED("\u001b[4;34m"),  // BLUE
    MAGENTA_UNDERLINED("\u001b[4;35m"),  // MAGENTA
    CYAN_UNDERLINED("\u001b[4;36m"),  // CYAN
    WHITE_UNDERLINED("\u001b[4;37m"),  // WHITE

    // Background
    BLACK_BACKGROUND("\u001b[40m"),  // BLACK
    RED_BACKGROUND("\u001b[41m"),  // RED
    GREEN_BACKGROUND("\u001b[42m"),  // GREEN
    YELLOW_BACKGROUND("\u001b[43m"),  // YELLOW
    BLUE_BACKGROUND("\u001b[44m"),  // BLUE
    MAGENTA_BACKGROUND("\u001b[45m"),  // MAGENTA
    CYAN_BACKGROUND("\u001b[46m"),  // CYAN
    WHITE_BACKGROUND("\u001b[47m"),  // WHITE

    // High Intensity
    BLACK_BRIGHT("\u001b[0;90m"),  // BLACK
    RED_BRIGHT("\u001b[0;91m"),  // RED
    GREEN_BRIGHT("\u001b[0;92m"),  // GREEN
    YELLOW_BRIGHT("\u001b[0;93m"),  // YELLOW
    BLUE_BRIGHT("\u001b[0;94m"),  // BLUE
    MAGENTA_BRIGHT("\u001b[0;95m"),  // MAGENTA
    CYAN_BRIGHT("\u001b[0;96m"),  // CYAN
    WHITE_BRIGHT("\u001b[0;97m"),  // WHITE

    // Bold High Intensity
    BLACK_BOLD_BRIGHT("\u001b[1;90m"),  // BLACK
    RED_BOLD_BRIGHT("\u001b[1;91m"),  // RED
    GREEN_BOLD_BRIGHT("\u001b[1;92m"),  // GREEN
    YELLOW_BOLD_BRIGHT("\u001b[1;93m"),  // YELLOW
    BLUE_BOLD_BRIGHT("\u001b[1;94m"),  // BLUE
    MAGENTA_BOLD_BRIGHT("\u001b[1;95m"),  // MAGENTA
    CYAN_BOLD_BRIGHT("\u001b[1;96m"),  // CYAN
    WHITE_BOLD_BRIGHT("\u001b[1;97m"),  // WHITE

    // High Intensity backgrounds
    BLACK_BACKGROUND_BRIGHT("\u001b[0;100m"),  // BLACK
    RED_BACKGROUND_BRIGHT("\u001b[0;101m"),  // RED
    GREEN_BACKGROUND_BRIGHT("\u001b[0;102m"),  // GREEN
    YELLOW_BACKGROUND_BRIGHT("\u001b[0;103m"),  // YELLOW
    BLUE_BACKGROUND_BRIGHT("\u001b[0;104m"),  // BLUE
    MAGENTA_BACKGROUND_BRIGHT("\u001b[0;105m"),  // MAGENTA
    CYAN_BACKGROUND_BRIGHT("\u001b[0;106m"),  // CYAN
    WHITE_BACKGROUND_BRIGHT("\u001b[0;107m");

    override fun toString(): String {
        return code
    }

    companion object {
        fun wrapCyan(str: String) = ANSI_CYAN.toString() + str + ANSI_RESET.toString()
        fun wrapCyanBold(str: String) = CYAN_BOLD.toString() + str + ANSI_RESET.toString()

        fun wrapBlue(str: String) = ANSI_BLUE.toString() + str + ANSI_RESET.toString()
        fun wrapBlueBold(str: String) = BLUE_BOLD.toString() + str + ANSI_RESET.toString()

        fun wrapGreen(str: String) = ANSI_GREEN.toString() + str + ANSI_RESET.toString()
        fun wrapGreenBold(str: String) = GREEN_BOLD.toString() + str + ANSI_RESET.toString()

        fun wrapRed(str: String) = ANSI_RED.toString() + str + ANSI_RESET.toString()
        fun wrapRedBold(str: String) = RED_BOLD.toString() + str + ANSI_RESET.toString()

        fun wrapYellow(str: String) = ANSI_YELLOW.toString() + str + ANSI_RESET.toString()
        fun wrapYellowBold(str: String) = YELLOW_BOLD.toString() + str + ANSI_RESET.toString()
    }

}