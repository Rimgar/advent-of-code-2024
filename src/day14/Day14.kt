package day14

import util.Vector
import util.printPlatform
import util.println
import util.product
import util.readInput
import kotlin.time.measureTime

private const val folder = "day14"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput, Vector(11, 7)) == 12)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>, fieldSize: Vector = Vector(101, 103)): Int {
    return input.map { line -> Robot.fromString(line) }
        .onEach { it.move(fieldSize, 100) }
        .let { robots -> countQuadrants(robots, fieldSize) }
}

private fun countQuadrants(robots: List<Robot>, fieldSize: Vector): Int {
    val quadrantCounts = MutableList(4) { 0 }
    robots.map { it.position }.forEach { position ->
        if (position.x < fieldSize.x / 2) {
            if (position.y < fieldSize.y / 2) {
                quadrantCounts[1]++
            } else if (position.y > fieldSize.y / 2) {
                quadrantCounts[0]++
            }
        } else if (position.x > fieldSize.x / 2) {
            if (position.y < fieldSize.y / 2) {
                quadrantCounts[2]++
            } else if (position.y > fieldSize.y / 2) {
                quadrantCounts[3]++
            }
        }
    }
    return quadrantCounts.product()
}

private fun part2(input: List<String>, fieldSize: Vector = Vector(101, 103)): Int {
    val robots = input.map { line -> Robot.fromString(line) }
//    val history = mutableMapOf<List<Robot>,Int>()
//    history[robots] = 0
    for (i in 1..10403) {
        robots.onEach { it.move(fieldSize, 1) }
        if (containsChristmasTree(robots)) {
            displayRobots(robots, fieldSize)
            return i
        }
//        if (history.contains(robots)) {
//            "stopped after $i; repetition from ${history[robots]}".println()
//            break
//        } else {
//            history[robots] = i
//        }
    }
    return input.size
}

private fun containsChristmasTree(robots: List<Robot>): Boolean {
    robots.forEach { robot ->
        val christmasTreeFound = (1..5).all { i ->
            robots.find { it.position == robot.position + Vector(-1, 1) * i } != null &&
                    robots.find { it.position == robot.position + Vector(1, 1) * i } != null
        }
        if (christmasTreeFound) return true
    }
    return false
}

private fun displayRobots(robots: List<Robot>, fieldSize: Vector) {
    val grid = Array(fieldSize.y) { CharArray(fieldSize.x) { ' ' } }
    robots.forEach { robot -> grid[robot.position.y][robot.position.x] = 'X' }
    grid.printPlatform()
}

private data class Robot(
    var position: Vector,
    val velocity: Vector
) {

    fun move(fieldSize: Vector, n: Int = 1) {
        position = (velocity * n + position).mod(fieldSize)
    }

    companion object {

        fun fromString(string: String): Robot {
            return """p=(\d+),(\d+) v=(-?\d+),(-?\d+)""".toRegex()
                .matchEntire(string)
                ?.groupValues
                ?.drop(1)
                ?.map { it.toInt() }
                ?.let { (posX, posY, velX, velY) ->
                    Robot(Vector(posX, posY), Vector(velX, velY))
                }
                ?: throw IllegalArgumentException("'$string' is not a valid input")
        }
    }
}
