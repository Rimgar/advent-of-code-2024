package day01

import util.println
import util.readInput
import kotlin.math.abs
import kotlin.time.measureTime

private const val folder = "day01"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val (leftColumn, rightColumn) = splitByColumns(input)
    return leftColumn.indices.sumOf { i ->
        abs(leftColumn[i] - rightColumn[i])
    }
}

private fun part2(input: List<String>): Int {
    val (leftColumn, rightColumn) = splitByColumns(input)
    return leftColumn.sumOf { leftNumber ->
        rightColumn.count { it == leftNumber } * leftNumber
    }
}

private fun splitByColumns(input: List<String>): Pair<List<Int>, List<Int>> {
    val lines = input.map { line ->
        line.split("""\s+""".toRegex()).map { it.toInt() }
    }
    val leftColumn = lines.map { it.first() }.sorted()
    val rightColumn = lines.map { it[1] }.sorted()
    return leftColumn to rightColumn
}
