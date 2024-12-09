package util

fun List<String>.toGrid(): Array<CharArray> {
    return this.map { it.toCharArray() }
        .toTypedArray()
}

fun Array<CharArray>.printPlatform() {
    forEach { it.joinToString(separator = "").println() }
    "".println()
}

fun Array<CharArray>.asString(): String {
    return this.joinToString("") { it.joinToString("") }
}
