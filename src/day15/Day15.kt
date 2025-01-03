package day15

import day15.Instruction.Down
import day15.Instruction.Left
import day15.Instruction.Right
import day15.Instruction.Up
import util.Vector
import util.println
import util.readInput
import util.split
import util.toGrid
import kotlin.time.measureTime

private const val folder = "day15"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    val testInput2 = readInput("$folder/test2")
    check(part1(testInput) == 10092)
    check(part1(testInput2) == 2028)
    check(part2(testInput) == 9021)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val (map, instructions) = parseInput(input)
    var position = findStartPosition(map)

    instructions.forEach { instruction ->
        position = moveRobot(map, position, instruction)
    }

    return calculateGps(map, 'O')
}

private fun part2(input: List<String>): Int {
    val (map, instructions) = parseInputPart2(input)
    var position = findStartPosition(map)

    instructions.forEach { instruction ->
        position = moveRobot(map, position, instruction)
    }

    return calculateGps(map, '[')
}

private fun findStartPosition(map: Array<CharArray>): Vector {
    map.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            if (c == '@') return Vector(x, y)
        }
    }
    throw IllegalArgumentException("No start position found")
}

private fun moveRobot(map: Array<CharArray>, position: Vector, instruction: Instruction): Vector {
    val newPosition = position + instruction.direction
    return when (map[newPosition.y][newPosition.x]) {
        '#' -> position
        'O' -> if (moveBox(map, newPosition, instruction)) {
            swap(map, position, newPosition)
            newPosition
        } else {
            position
        }
        '[', ']' -> {
            if (moveBigBox(map, newPosition, instruction)) {
                swap(map, position, newPosition)
                newPosition
            } else {
                position
            }
        }
        else -> {
            swap(map, position, newPosition)
            newPosition
        }
    }
}

private fun moveBox(map: Array<CharArray>, position: Vector, instruction: Instruction): Boolean {
    val newPosition = position + instruction.direction
    return when (map[newPosition.y][newPosition.x]) {
        '#' -> false
        'O' -> if (moveBox(map, newPosition, instruction)) {
            swap(map, position, newPosition)
            true
        } else {
            false
        }
        else -> {
            swap(map, position, newPosition)
            true
        }
    }
}

private fun moveBigBox(map: Array<CharArray>, position: Vector, instruction: Instruction): Boolean {
    return when (instruction) {
        Up, Down -> {
            if (isBigBoxMovable(map, position, instruction)) {
                moveBigBoxVertical(map, position, instruction)
            } else {
                false
            }
        }
        Left, Right -> {
            moveBigBoxHorizontal(map, position, instruction)
        }
    }
}

private fun isBigBoxMovable(map: Array<CharArray>, position: Vector, instruction: Instruction): Boolean {
    val secondHalf = when (map[position.y][position.x]) {
        '[' -> position + Vector(1, 0)
        ']' -> position + Vector(-1, 0)
        '#' -> return false
        else -> return true
    }
    if (map[secondHalf.y][secondHalf.x] !in listOf('[', ']')) throw IllegalArgumentException("secondHalf should be [ or ]")
    return isBigBoxMovable(map, position + instruction.direction, instruction)
            && isBigBoxMovable(map, secondHalf + instruction.direction, instruction)
}

private fun moveBigBoxVertical(map: Array<CharArray>, position: Vector, instruction: Instruction): Boolean {
    val secondHalf = when (map[position.y][position.x]) {
        '[' -> position + Vector(1, 0)
        ']' -> position + Vector(-1, 0)
        else -> return false
    }
    val newPositionFirstHalf = position + instruction.direction
    val newPositionSecondHalf = secondHalf + instruction.direction
    moveBigBoxVertical(map, newPositionFirstHalf, instruction)
    moveBigBoxVertical(map, newPositionSecondHalf, instruction)
    swap(map, position, newPositionFirstHalf)
    swap(map, secondHalf, newPositionSecondHalf)
    return true
}

private fun moveBigBoxHorizontal(map: Array<CharArray>, position: Vector, instruction: Instruction): Boolean {
    val newPositionOneStep = position + instruction.direction
    val newPositionTwoStep = newPositionOneStep + instruction.direction
    return when (map[newPositionTwoStep.y][newPositionTwoStep.x]) {
        '#' -> false
        '[', ']' -> if (moveBigBoxHorizontal(map, newPositionTwoStep, instruction)) {
            swap(map, newPositionOneStep, newPositionTwoStep)
            swap(map, position, newPositionOneStep)
            true
        } else {
            false
        }
        else -> {
            swap(map, newPositionOneStep, newPositionTwoStep)
            swap(map, position, newPositionOneStep)
            true
        }
    }
}

private fun calculateGps(map: Array<CharArray>, boxChar: Char): Int {
    return map.flatMapIndexed { y, row ->
        row.mapIndexed { x, c ->
            if (c == boxChar) 100 * y + x else 0
        }
    }.sum()
}

private fun swap(map: Array<CharArray>, pos1: Vector, pos2: Vector) {
    val swap = map[pos1.y][pos1.x]
    map[pos1.y][pos1.x] = map[pos2.y][pos2.x]
    map[pos2.y][pos2.x] = swap
}

private fun parseInput(input: List<String>): Pair<Array<CharArray>, List<Instruction>> {
    val (map, rawInstructions) = input.split { it.isEmpty() }
    val instructions = rawInstructions.flatMap { line ->
        line.map { c -> Instruction.valueOf(c) }
    }
    return map.toGrid() to instructions
}

private fun parseInputPart2(input: List<String>): Pair<Array<CharArray>, List<Instruction>> {
    val (map, instructions) = parseInput(input)
    val bigMap = map.map { row ->
        row.flatMap { c ->
            when (c) {
                '#' -> listOf('#', '#')
                '.' -> listOf('.', '.')
                'O' -> listOf('[', ']')
                '@' -> listOf('@', '.')
                else -> throw IllegalArgumentException("Unknown char $c")
            }
        }.toCharArray()
    }.toTypedArray()
    return bigMap to instructions
}

private enum class Instruction(val direction: Vector) {
    Up(Vector(0, -1)),
    Down(Vector(0, 1)),
    Left(Vector(-1, 0)),
    Right(Vector(1, 0));

    companion object {

        fun valueOf(c: Char): Instruction {
            return when (c) {
                '^' -> Up
                'v' -> Down
                '<' -> Left
                '>' -> Right
                else -> throw IllegalArgumentException("$c is not a valid Instruction")
            }
        }
    }
}
