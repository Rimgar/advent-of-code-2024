package day12

import util.Vector
import util.get
import util.getOrNull
import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day12"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    val testInput2 = readInput("$folder/test2")
    val testInput3 = readInput("$folder/test3")
    val testInput4 = readInput("$folder/test4")
    val testInput5 = readInput("$folder/test5")
    check(part1(testInput) == 140)
    check(part1(testInput2) == 772)
    check(part1(testInput3) == 1930)
    check(part2(testInput) == 80)
    check(part2(testInput2) == 436)
    check(part2(testInput3) == 1206)
    check(part2(testInput4) == 236)
    check(part2(testInput5) == 368)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val visited = Array(input.size) { BooleanArray(input.first().length) }
    var price = 0
    for (y in input.indices) {
        for (x in input.indices) {
            if (!visited[y][x]) {
                price += visitRegion(Vector(x, y), input, visited).let { region -> region.area * region.perimeter }
            }
        }
    }
    return price
}

private fun part2(input: List<String>): Int {
    val visited = Array(input.size) { BooleanArray(input.first().length) }
    var price = 0
    for (y in input.indices) {
        for (x in input.indices) {
            if (!visited[y][x]) {
                price += visitRegion(Vector(x, y), input, visited).let { region -> region.area * region.sides }
            }
        }
    }
    return price
}

private fun visitRegion(startCoordinate: Vector, input: List<String>, visited: Array<BooleanArray>): Region {
    val regionName = input[startCoordinate]
    var area = 0
    var perimeter = 0
    var sides = 0
    val visitedSides = mutableSetOf<Pair<Vector, Vector>>()
    val queue = ArrayDeque<Vector>().apply {
        addLast(startCoordinate)
    }
    while (queue.isNotEmpty()) {
        val currentCoordinate = queue.removeFirst()
        if (visited.getOrNull(currentCoordinate.y)?.getOrNull(currentCoordinate.x) == false) {
            visited[currentCoordinate.y][currentCoordinate.x] = true
            area++
            Vector.cardinalDirections
                .forEach { direction ->
                    val neighbor = currentCoordinate + direction
                    if (input.getOrNull(neighbor) == regionName) {
                        queue.addLast(neighbor)
                    } else {
                        perimeter++
                        if (!visitedSides.contains(currentCoordinate to direction)) {
                            sides++
                            visitedSides += findSide(currentCoordinate, direction, regionName, input)
                        }
                    }
                }
        }
    }
    return Region(area, perimeter, sides)
}

private fun findSide(startCoordinate: Vector, direction: Vector, regionName: Char, input: List<String>): Set<Pair<Vector, Vector>> {
    val visitedSides = mutableSetOf(startCoordinate to direction)
    val rightSide = direction.turnRight()
    var currentCoordinate = startCoordinate + rightSide
    while (input.getOrNull(currentCoordinate) == regionName && input.getOrNull(currentCoordinate + direction) != regionName) {
        visitedSides.add(currentCoordinate to direction)
        currentCoordinate += rightSide
    }
    val leftSide = direction.turnLeft()
    currentCoordinate = startCoordinate + leftSide
    while (input.getOrNull(currentCoordinate) == regionName && input.getOrNull(currentCoordinate + direction) != regionName) {
        visitedSides.add(currentCoordinate to direction)
        currentCoordinate += leftSide
    }
    return visitedSides
}

private data class Region(
    val area: Int,
    val perimeter: Int,
    val sides: Int
)
