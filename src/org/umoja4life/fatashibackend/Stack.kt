package org.umoja4life.fatashibackend

/**
 * Stack as type alias of Mutable List
 * suggested by: Malwinder Singh, https://stackoverflow.com/a/61724278/960009
 */
typealias Stack<T> = MutableList<T>

// empty constructor for Stack
fun <T> stackOf(): MutableList<T> = mutableListOf()

/**
 * Pushes item to [Stack]
 * @param item Item to be pushed
 */
inline fun <T> Stack<T>.push(item: T) = add(item)

/**
 * Pops (removes and return) last item from [Stack]
 * @return item Last item if [Stack] is not empty, null otherwise
 */
fun <T> Stack<T>.pop(): T? = if (isNotEmpty()) removeAt(lastIndex) else null

/**
 * Peeks (return) last item from [Stack]
 * @return item Last item if [Stack] is not empty, null otherwise
 */
fun <T> Stack<T>.peek(): T? = if (isNotEmpty()) this[lastIndex] else null

