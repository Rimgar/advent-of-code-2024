package day08

import util.Vector
import util.allPairs
import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day08"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val antennas = findAntennas(input)
    val antiNodes = mutableSetOf<Vector>()
    antennas.values.forEach { frequencyAntennas ->
        frequencyAntennas.allPairs().forEach { (a, b) ->
            val difference = b - a
            antiNodes.add(a - difference)
            antiNodes.add(b + difference)
        }
    }
    return antiNodes.filter { it.inRangeOf(input) }
        .size
}

private fun part2(input: List<String>): Int {
    val antennas = findAntennas(input)
    val antiNodes = mutableSetOf<Vector>()
    antennas.values.forEach { frequencyAntennas ->
        frequencyAntennas.allPairs().forEach { (a, b) ->
            val difference = b - a
            antiNodes.addAll(findAntiNodes(a, -difference, input))
            antiNodes.addAll(findAntiNodes(b, difference, input))
        }
    }
    return antiNodes.size
}

private fun findAntiNodes(start: Vector, direction: Vector, input: List<String>): Set<Vector> {
    val antiNodes = mutableSetOf<Vector>()
    var i = 0
    while (true) {
        val currentCoordinate = start + direction * i
        if (!currentCoordinate.inRangeOf(input)) {
            break
        }
        antiNodes.add(currentCoordinate)
        i++
    }

    return antiNodes
}

private fun findAntennas(input: List<String>): Map<Char, List<Vector>> {
    val antennas = mutableMapOf<Char, List<Vector>>().withDefault { emptyList() }
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            if (c != '.') {
                antennas[c] = antennas.getValue(c) + Vector(x, y)
            }
        }
    }
    return antennas
}
