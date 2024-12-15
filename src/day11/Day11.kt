package day11

import util.println
import util.readInput
import kotlin.math.pow
import kotlin.time.measureTime

private const val folder = "day11"

private val cache = mutableMapOf<Pair<Long, Int>, Long>()

fun main() {

    check(blinkStone(0) == listOf(1L))
    check(blinkStone(1) == listOf(2024L))
    check(blinkStone(10) == listOf(1L, 0L))
    check(blinkStone(99) == listOf(9L, 9L))
    check(blinkStone(999) == listOf(999L * 2024))

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 55312L)
    // No testInput for part 2 given in puzzle
//    check(part2(testInput) == 281)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Long {
    val initial = input.first().split(' ').map { it.toLong() }
    return blinkArrangement(initial, 25)
}

private fun part2(input: List<String>): Long {
    val initial = input.first().split(' ').map { it.toLong() }
    return blinkArrangement(initial, 75)
}

private fun blinkArrangement(stones: List<Long>, cyclesLeft: Int): Long {
    return stones.sumOf { stone ->
        blinkStone(stone, cyclesLeft)
    }
}

private fun blinkStone(stone: Long, cyclesLeft: Int): Long {
    if (cyclesLeft == 0) return 1
    cache[stone to cyclesLeft]?.let { return it }

    return blinkStone(stone).sumOf {
        blinkStone(it, cyclesLeft - 1)
    }.also { cache[stone to cyclesLeft] = it }
}

private fun blinkStone(stone: Long): List<Long> {
    return when {
        stone == 0L -> listOf(1L)
        stone.toString().length % 2 == 0 -> {
            val splitSize = 10.0.pow(stone.toString().length / 2).toLong()
            listOf(stone / splitSize, stone % splitSize)
        }
        else -> listOf(stone * 2024)
    }
}
