package day06

import util.Vector
import util.println
import util.readInput
import util.toGrid
import kotlin.time.measureTime

private const val folder = "day06"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val grid = input.toGrid()
    var currentPosition = input.findStart()
    var direction = Vector(0, -1)
    grid[currentPosition.y][currentPosition.x] = 'X'
    while (currentPosition.inRangeOf(input)) {
        val newPosition = currentPosition + direction
        if (newPosition.inRangeOf(input)) {
            if (grid[newPosition.y][newPosition.x] == '#') {
                direction = direction.turnRight()
            } else {
                currentPosition = newPosition
                grid[currentPosition.y][currentPosition.x] = 'X'
            }
        } else {
            break
        }
    }
    return grid.sumOf { line -> line.count { it == 'X' } }
}

private fun part2(input: List<String>): Int {
    val start = input.findStart()
    return input.mapIndexed { y, row ->
            row.mapIndexed { x, c ->
                if (c != '^' && input.toGrid().also { it[y][x] = '#' }.detectLoop(start)) {
                    1
                } else {
                    0
                }
            }.sum()
        }.sum()
}

private fun Array<CharArray>.detectLoop(start: Vector): Boolean {

    var currentPosition = start
    var direction = Vector(0, -1)
    val visitedFields = mutableSetOf(start to direction)
    while (currentPosition.y in indices && currentPosition.x in first().indices) {
        val newPosition = currentPosition + direction
        if (newPosition.y in indices && newPosition.x in first().indices) {
            if (this[newPosition.y][newPosition.x] == '#') {
                direction = direction.turnRight()
            } else {
                currentPosition = newPosition
            }
            if (visitedFields.contains(currentPosition to direction)) {
                return true
            } else {
                visitedFields.add(currentPosition to direction)
            }
        } else {
            return false
        }
    }
    return false
}

private fun List<String>.findStart(): Vector {
    this.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c == '^') {
                return Vector(x, y)
            }
        }
    }
    throw IllegalArgumentException("no start found")
}
