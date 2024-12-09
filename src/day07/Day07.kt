package day07

import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day07"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 3749L)
    check(part2(testInput) == 11387L)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Long {
    return calculateTotalCalibrationResult(input, listOf(Operator.add, Operator.mulitply))
}

private fun part2(input: List<String>): Long {
    return calculateTotalCalibrationResult(input, Operator.entries)
}

private fun calculateTotalCalibrationResult(input: List<String>, operators: List<Operator>) = input.map { line -> getEquation(line) }
    .sumOf { (expectedResult, operands) ->
        if (combinable(expectedResult, operands, operators)) {
            expectedResult
        } else {
            0
        }
    }

private fun getEquation(equation: String): Pair<Long, List<Long>> {
    val (result, operands) = equation.split(": ")
    return result.toLong() to operands.split(' ').map { it.toLong() }
}

private fun combinable(expectedResult: Long, operands: List<Long>, operators: List<Operator>): Boolean {
    if (operands.size == 1) {
        return expectedResult == operands.first()
    }
    val a = operands.first()
    val b = operands[1]
    val rest = operands.drop(2)
    return operators.any { operator ->
        combinable(expectedResult, listOf(operator.evaluate(a,b)) + rest, operators)
    }
}

enum class Operator(val evaluate: (Long, Long) -> Long) {
    add({ a, b -> a + b }),
    mulitply({ a, b -> a * b }),
    concat({ a, b -> "$a$b".toLong() })
}
