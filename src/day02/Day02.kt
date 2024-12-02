package day02

import util.println
import util.readInput
import kotlin.math.abs
import kotlin.time.measureTime

private const val folder = "day02"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    return readInput(input).count { report ->
        isSafe(report)
    }
}

private fun part2(input: List<String>): Int {
    return readInput(input).count { report ->
        if (isSafe(report)) {
            true
        } else {
            report.indices.any { indexToRemove ->
                isSafe(report.filterIndexed { index, _ -> index != indexToRemove })
            }
        }
    }
}

private fun readInput(input: List<String>): List<List<Int>> =
    input.map { line ->
        line.split(' ').map { it.toInt() }
    }

private fun isSafe(report: List<Int>) = report
    .windowed(2)
    .map { (first, second) -> second - first }
    .let { differences ->
        (differences.all { it > 0 } || differences.all { it < 0 }) && differences.all { abs(it) in 1..3 }
    }
