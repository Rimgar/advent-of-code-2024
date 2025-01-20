package day19

import util.println
import util.readInput
import util.split
import kotlin.time.measureTime

private const val folder = "day19"

private val cache = mutableMapOf<String, Long>()

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 6)
    check(part2(testInput) == 16L)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val (towels, patterns) = parseInput(input)
    val regex = towels.joinToString(prefix = "(", separator = "|", postfix = ")+").toRegex()
    return patterns.filter { pattern -> pattern.matches(regex) }
        .size
}

private fun part2(input: List<String>): Long {
    cache.clear()
    val (towels, patterns) = parseInput(input)
    val regex = towels.joinToString(prefix = "(", separator = "|", postfix = ")+").toRegex()
    return patterns.filter { pattern -> pattern.matches(regex) }
        .sumOf { pattern ->
            findMatches(towels, pattern)
        }
}

private fun findMatches(towels: List<String>, pattern: String): Long {
    cache[pattern]?.let { return it }
    return towels.sumOf { towel ->
        if (towel == pattern) {
            1
        } else if (pattern.startsWith(towel)) {
            findMatches(towels, pattern.drop(towel.length))
        } else {
            0
        }
    }.also { cache[pattern] = it }
}

private fun parseInput(input: List<String>): Pair<List<String>, List<String>> {
    val (rawTowels, patterns) = input.split { it.isEmpty() }
    val towels = rawTowels.first().split(", ")
    return towels to patterns
}
