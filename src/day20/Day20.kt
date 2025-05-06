package day20

import util.Vector
import util.findChar
import util.get
import util.println
import util.readInput
import util.set
import kotlin.time.measureTime

private const val folder = "day20"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput, 2) == 14 + 14 + 2 + 4 + 2 + 3 + 5)
    check(part1(testInput, 3) == 14 + 2 + 4 + 2 + 3 + 5)
    check(part1(testInput, 40) == 2)
    check(part2(testInput, 76) == 3)

    val input = readInput("$folder/input")
    measureTime {
        part1(input, 100).println()
    }.println()
    measureTime {
        part2(input, 100).println()
    }.println()
}

private fun part1(input: List<String>, minSavedTime: Int): Int = part2(input, minSavedTime, 2)

private fun part2(input: List<String>, minSavedTime: Int, cheatSize: Int = 20): Int {
    val possibleDirections = getPossibleDirections(cheatSize)

    val (positions, timeMap) = getPositionsAndTimeMap(input)

    val timeSaves = mutableMapOf<Pair<Vector, Vector>, Int>()
    positions.forEach { position ->
        possibleDirections.forEach { dir ->
            val cheatedPosition = position + dir
            if (cheatedPosition.inRangeOf(input)) {
                val timeSave = timeMap[cheatedPosition] - timeMap[position] - dir.length
                if (timeSave >= minSavedTime) {
                    timeSaves[position to cheatedPosition] = timeSave
                }
            }
        }
    }

    return timeSaves.count { it.value >= minSavedTime }
}

private fun getPositionsAndTimeMap(input: List<String>): Pair<MutableList<Vector>, Array<IntArray>> {
    val start = input.findChar('S')
    val positions = mutableListOf(start)

    val timeMap = Array(input.size) { IntArray(input.first().length) }
    var currentTime = 0
    var currentPosition = start
    while (input[currentPosition] != 'E') {
        currentTime++
        val direction = Vector.cardinalDirections.first { dir ->
            val nextPosition = currentPosition + dir
            input[nextPosition] in listOf('.', 'E') && timeMap[nextPosition] <= 0
        }
        currentPosition += direction
        timeMap[currentPosition] = currentTime
        positions.add(currentPosition)
    }
    return Pair(positions, timeMap)
}

private fun getPossibleDirections(maxSize: Int = 20): Set<Vector> {
    val possibleDirections = mutableSetOf<Vector>()

    for (size in 2..maxSize) {
        for (x in 0..size) {
            possibleDirections.add(Vector(x, size - x))
            possibleDirections.add(Vector(x, -size + x))
            possibleDirections.add(Vector(-x, size - x))
            possibleDirections.add(Vector(-x, -size + x))
        }
    }

    return possibleDirections
}
