package day04

import util.Vector
import util.get
import util.getOrNull
import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day04"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 9)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    return input.mapIndexed { y, line ->
        line.indices.sumOf { x ->
            countXmas(input, Vector(x, y))
        }
    }.sum()
}

fun countXmas(input: List<String>, vector: Vector): Int {
    var count = 0
    if (input[vector] == 'X') {
        for (x in listOf(-1, 1)) {
            for (y in -1..1) {
                val direction = Vector(x, y)
                if (
                    input.getOrNull(vector + direction) == 'M' &&
                    input.getOrNull(vector + direction * 2) == 'A' &&
                    input.getOrNull(vector + direction * 3) == 'S'
                ) {
                    count++
                }
            }
        }
        for (y in listOf(-1, 1)) {
            val direction = Vector(0, y)
            if (
                input.getOrNull(vector + direction) == 'M' &&
                input.getOrNull(vector + direction * 2) == 'A' &&
                input.getOrNull(vector + direction * 3) == 'S'
            ) {
                count++
            }
        }
    }
    return count
}

private fun part2(input: List<String>): Int {
    return input.mapIndexed { y, line ->
        line.indices.sumOf { x ->
            countMasAsX(input, Vector(x, y))
        }
    }.sum()
}

fun countMasAsX(input: List<String>, vector: Vector): Int {
    var count = 0
    if (input[vector] == 'A') {
        val upLeft = input.getOrNull(vector + Vector(-1, -1))
        val downRight = input.getOrNull(vector + Vector(1, 1))
        val upRight = input.getOrNull(vector + Vector(1, -1))
        val downLeft = input.getOrNull(vector + Vector(-1, 1))
        if (checkDiagonal(upLeft, downRight) && checkDiagonal(upRight, downLeft)) {
            count++
        }
    }
    return count
}

private fun checkDiagonal(c1: Char?, c2: Char?) = c1 == 'M' && c2 == 'S' || c1 == 'S' && c2 == 'M'
