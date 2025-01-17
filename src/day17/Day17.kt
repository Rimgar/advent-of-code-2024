package day17

import util.println
import util.readInput
import util.split
import kotlin.math.pow
import kotlin.time.measureTime

private const val folder = "day17"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == "4,6,3,5,6,3,5,2,1,0")
    val testInput2 = readInput("$folder/test2")
    check(part2(testInput2) == 117440L)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): String {
    val (initialState, program) = parseInput(input)
    return part1(initialState, program).print()
}

private fun part1(initialState: State, program: List<Int>): State {
    var state = initialState
    while (state.instructionCounter in program.indices) {
        val instruction = Instruction.entries[program[state.instructionCounter]]
        val operand = program[state.instructionCounter + 1]
        state = instruction.operation(state, operand)
    }
    return state
}

private fun part2(input: List<String>): Long {
    val (initialState, program) = parseInput(input)

    for (regA in 1L..7) {
        val state = initialState.copy(regA = regA, output = mutableListOf())
        val endState = part1(state, program)
        if (program.endsWith(endState.output)) {
            val result = findNextDigit(initialState.copy(regA = regA, output = mutableListOf()), program)
            if (result != null) {
                return result
            }
        }
    }

    return -1
}

private fun findNextDigit(initialState: State, program: List<Int>): Long? {
    initialState.regA *= 8
    for (i in 0L..7) {
        val regA = initialState.regA + i
        val state = initialState.copy(regA = regA, output = mutableListOf())
        val endState = part1(state, program)
        if (program.endsWith(endState.output)) {
            if (program.size == endState.output.size) {
                return regA
            }
            val result = findNextDigit(initialState.copy(regA = regA, output = mutableListOf()), program)
            if (result != null) {
                return result
            }
        }
    }
    return null
}

private fun <T> List<T>.endsWith(suffix: List<T>): Boolean {
    if (suffix.size > size) return false

    return takeLast(suffix.size).zip(suffix).all { (first, second) -> first == second }
}

private fun parseInput(input: List<String>): Pair<State, List<Int>> {
    val (rawState, rawProgram) = input.split { it.isEmpty() }
    val state = State()
    rawState.forEach { rawRegister ->
        """Register ([ABC]): (\d+)""".toRegex().matchEntire(rawRegister)
            ?.destructured
            ?.let { (reg, value) ->
                when (reg) {
                    "A" -> state.regA = value.toLong()
                    "B" -> state.regB = value.toLong()
                    "C" -> state.regC = value.toLong()
                }
            }
    }
    val program = """Program: ([\d,]+)""".toRegex()
        .matchEntire(rawProgram.first())
        ?.groupValues
        ?.get(1)
        ?.split(",")
        ?.map { it.toInt() }
        ?: throw IllegalArgumentException("Program input not parseable")
    return state to program
}

private data class State(
    var regA: Long = 0,
    var regB: Long = 0,
    var regC: Long = 0,
    var instructionCounter: Int = 0,
    val output: MutableList<Int> = mutableListOf()
) {

    fun increaseInstructionCounter() {
        instructionCounter += 2
    }

    fun jumpTo(instructionAddress: Int) {
        instructionCounter = instructionAddress
    }

    fun addOutput(out: Int) {
        output += out
    }

    fun print(): String {
        return output.joinToString(",")
    }
}

private enum class Instruction(
    val operation: (State, Int) -> State
) {
    ADV({ state, operand ->
        val denominator = 2.0.pow(comboOperand(state, operand).toDouble())
        val result = state.regA / denominator
        state.regA = result.toLong()
        state.increaseInstructionCounter()
        state
    }),

    BXL({ state, operand ->
        state.regB = state.regB xor operand.toLong()
        state.increaseInstructionCounter()
        state
    }),

    BST({ state, operand ->
        state.regB = comboOperand(state, operand).mod(8L)
        state.increaseInstructionCounter()
        state
    }),

    JNZ({ state, operand ->
        if (state.regA == 0L) {
            state.increaseInstructionCounter()
        } else {
            state.jumpTo(operand)
        }
        state
    }),

    BXC({ state, _ ->
        state.regB = state.regB xor state.regC
        state.increaseInstructionCounter()
        state
    }),

    OUT({ state, operand ->
        state.addOutput(comboOperand(state, operand).mod(8))
        state.increaseInstructionCounter()
        state
    }),

    BDV({ state, operand ->
        val denominator = 2.0.pow(comboOperand(state, operand).toDouble())
        val result = state.regA / denominator
        state.regB = result.toLong()
        state.increaseInstructionCounter()
        state
    }),

    CDV({ state, operand ->
        val denominator = 2.0.pow(comboOperand(state, operand).toDouble())
        val result = state.regA / denominator
        state.regC = result.toLong()
        state.increaseInstructionCounter()
        state
    })
}

private fun comboOperand(state: State, operand: Int): Long {
    return when (operand) {
        in 0..3 -> operand.toLong()
        4 -> state.regA
        5 -> state.regB
        6 -> state.regC
        else -> throw IllegalArgumentException("$operand is an invalid comboOperator")
    }
}

