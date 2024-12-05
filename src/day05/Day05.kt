package day05

import util.println
import util.readInput
import util.split
import kotlin.time.measureTime

private const val folder = "day05"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val (rules, updates) = getRulesAndUpdates(input)
    return updates.sumOf { update ->
        if (checkOrder(update, rules)) {
            update[update.size / 2]
        } else {
            0
        }
    }
}

private fun part2(input: List<String>): Int {
    val (rules, updates) = getRulesAndUpdates(input)
    val comparator = RulesComparator(rules)
    return updates.filterNot { update -> checkOrder(update, rules) }
        .sumOf { update ->
            update.sortedWith(comparator)[update.size/2]
        }
}

private fun checkOrder(update: List<Int>, rules: List<Rule>): Boolean {
    update.forEachIndexed { index, a ->
        (index..<update.size).forEach { b ->
            if (rules.any { rule -> rule.compare(a, update[b]) > 0 }) {
                return false
            }
        }
    }
    return true
}

private fun getRulesAndUpdates(input: List<String>): Pair<List<Rule>, List<List<Int>>> {
    val (rulesInput, updatesInput) = input.split { it.isEmpty() }
    val rules = rulesInput.map { line ->
        line.split('|')
            .map { it.toInt() }
            .let { (a, b) -> Rule(a, b) }
    }
    val updates = updatesInput.map { line ->
        line.split(',')
            .map { it.toInt() }
    }
    return rules to updates
}

private class RulesComparator(private val rules: List<Rule>) : Comparator<Int> {

    override fun compare(p0: Int?, p1: Int?): Int {
        rules.forEach { rule ->
            val result = rule.compare(p0, p1)
            if (result != 0) return result
        }
        return 0
    }
}

private data class Rule(
    val a: Int,
    val b: Int
) : Comparator<Int> {

    override fun compare(p0: Int?, p1: Int?): Int {
        return when {
            p0 == a && p1 == b -> -1
            p0 == b && p1 == a -> 1
            else -> 0
        }
    }
}
