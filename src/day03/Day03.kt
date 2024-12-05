package day03

import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day03"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 161)
    val testInput2 = readInput("$folder/test2")
    check(part2(testInput2) == 48)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    return """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
        .findAll(input.joinToString(""))
        .map { it.destructured.toList() }
        .sumOf { expr ->
            expr[0].toInt() * expr[1].toInt()
        }
}

private fun part2(input: List<String>): Int {
    var isActive = true
    return """mul\((\d{1,3}),(\d{1,3})\)|do\(\)|don't\(\)""".toRegex()
        .findAll(input.joinToString(""))
        .mapNotNull { expr ->
            when {
                expr.value == "do()" -> {
                    isActive = true
                    null
                }
                expr.value == "don't()" -> {
                    isActive = false
                    null
                }
                isActive -> expr.destructured.toList()
                else -> null
            }
        }.sumOf { expr ->
            expr[0].toInt() * expr[1].toInt()
        }
}
