package util

fun List<String>.toGrid(): Array<CharArray> {
    return this.map { it.toCharArray() }
        .toTypedArray()
}

operator fun Array<CharArray>.get(p: Vector) = this[p.y][p.x]
operator fun Array<CharArray>.set(p: Vector, c: Char) {
    this[p.y][p.x] = c
}

fun Array<CharArray>.printPlatform() {
    forEach { it.joinToString(separator = "").println() }
    "".println()
}

fun Array<CharArray>.asString(): String {
    return this.joinToString("") { it.joinToString("") }
}
