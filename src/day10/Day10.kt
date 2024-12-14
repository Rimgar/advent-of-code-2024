package day10

import util.Vector
import util.getOrNull
import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day10"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 36)
    check(part2(testInput) == 81)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val heightMap = input.map { row -> row.map { it.digitToInt() } }
    return heightMap.flatMapIndexed { y, row ->
        row.mapIndexed { x, height ->
            if (height == 0) {
                findGoals(heightMap, Vector(x, y)).toSet().size
            } else {
                0
            }
        }
    }.sum()
}

private fun findGoals(heightMap: List<List<Int>>, coordinate: Vector): List<Vector> {
    val currentHeight = heightMap[coordinate.y][coordinate.x]
    if (currentHeight == 9) {
        return listOf(coordinate)
    }
    return Vector.cardinalDirections.flatMap { direction ->
        val newCoordinate = coordinate + direction
        if (heightMap.getOrNull(newCoordinate) == currentHeight + 1) {
            findGoals(heightMap, newCoordinate)
        } else {
            emptyList()
        }
    }
}

private fun part2(input: List<String>): Int {
    val heightMap = input.map { row -> row.map { it.digitToInt() } }
    return heightMap.flatMapIndexed { y, row ->
        row.mapIndexed { x, height ->
            if (height == 0) {
                findGoals(heightMap, Vector(x, y)).size
            } else {
                0
            }
        }
    }.sum()
}
