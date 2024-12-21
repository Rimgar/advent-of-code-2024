package day13

import day13.Button.Companion.parseButton
import day13.Prize.Companion.parsePrize
import util.LongVector
import util.println
import util.readInput
import util.split
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.min
import kotlin.time.measureTime

private const val folder = "day13"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    val testInput2 = readInput("$folder/test2")
    check(part1(testInput) == 480L)
    check(part1(testInput2) == 10854L)
    check(part2(testInput) == 875318608908)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Long {
    return input.split { it.isEmpty() }
        .map { machineString -> Machine.parseMachine(machineString) }
        .calculate()
}

private fun part2(input: List<String>): Long {
    return input.split { it.isEmpty() }
        .map { machineString -> Machine.parseMachine(machineString) }
        .map { machine -> machine.copy(prize = machine.prize.copy(machine.prize.target + LongVector(1, 1) * 10000000000000L)) }
        .calculate()
}

private fun List<Machine>.calculate(): Long {
    return sumOf { machine -> machine.findWinningCosts() }
}

private data class Machine(
    val buttonA: Button,
    val buttonB: Button,
    val prize: Prize
) {

    override fun toString(): String {
        return "$buttonA\n$buttonB\n$prize\n"
    }

    fun calculateCost(a: Long, b: Long): Long {
        return a * buttonA.cost + b * buttonB.cost
    }

    fun findWinningCosts(): Long {
        val (a, b) = calculateAB()
        return if (a.toBigInteger().toDouble() == a.toDouble() && b.toBigInteger().toDouble() == b.toDouble()) {
            calculateCost(a.toLong(), b.toLong())
        } else {
            0
        }
    }

    fun findWinningCostsSmartSearch(): Long {
        var currentB = min(prize.target.x / buttonB.movement.x, prize.target.y / buttonB.movement.y)
        var currentA = 0L
        while (currentB >= 0 && buttonA.movement * currentA + buttonB.movement * currentB != prize.target) {
            val currentPosition = buttonA.movement * currentA + buttonB.movement * currentB
            if (currentPosition.x > prize.target.x || currentPosition.y > prize.target.y) {
                currentB--
            } else {
                currentA++
            }
        }
        return if (currentB >= 0) {
            calculateCost(currentA, currentB)
        } else {
            0
        }
    }

    fun calculateAB(): Pair<BigDecimal, BigDecimal> {
        val a = calculateA().setScale(5, RoundingMode.HALF_UP)
        val b = calculateB(a).setScale(5, RoundingMode.HALF_UP)
        return a to b
    }

    fun calculateA(): BigDecimal {
        val numerator = BigDecimal(prize.target.x) - BigDecimal(prize.target.y * buttonB.movement.x).setScale(50) / BigDecimal(buttonB.movement.y)
        val denominator =
            BigDecimal(buttonA.movement.x) - BigDecimal(buttonA.movement.y * buttonB.movement.x).setScale(50) / BigDecimal(buttonB.movement.y.toDouble())
        return numerator / denominator
    }

    fun calculateB(a: BigDecimal): BigDecimal {
        val numerator = BigDecimal(prize.target.y) - a * BigDecimal(buttonA.movement.y).setScale(50)
        val denominator = BigDecimal(buttonB.movement.y).setScale(50)
        return numerator / denominator
    }

    companion object {

        fun parseMachine(machineString: List<String>): Machine {
            return Machine(
                parseButton(machineString[0]),
                parseButton(machineString[1]),
                parsePrize(machineString[2]),
            )
        }
    }
}

private data class Button(
    val name: Char,
    val movement: LongVector,
    val cost: Int
) {

    override fun toString(): String {
        return "Button $name: X+${movement.x}, Y+${movement.y}"
    }

    companion object {

        fun parseButton(string: String): Button {
            return """Button ([AB]): X\+(\d+), Y\+(\d+)""".toRegex()
                .matchEntire(string)
                ?.let { matchResult ->
                    val name = matchResult.groupValues[1].first()
                    val cost = if (name == 'A') 3 else 1
                    Button(
                        name,
                        LongVector(matchResult.groupValues[2].toLong(), matchResult.groupValues[3].toLong()),
                        cost
                    )
                } ?: throw IllegalArgumentException("\"$string\" is not a Button")
        }
    }
}

private data class Prize(
    val target: LongVector
) {

    override fun toString(): String {
        return "Prize: X=${target.x}, Y=${target.y}"
    }

    companion object {

        fun parsePrize(string: String): Prize {
            return """Prize: X=(\d+), Y=(\d+)""".toRegex()
                .matchEntire(string)
                ?.let { matchResult ->
                    Prize(LongVector(matchResult.groupValues[1].toLong(), matchResult.groupValues[2].toLong()))
                } ?: throw IllegalArgumentException("\"$string\" is not a Prize")
        }
    }
}
