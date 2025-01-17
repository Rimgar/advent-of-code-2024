package day18

import util.Edge
import util.Graph
import util.Vector
import util.Vertex
import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day18"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput, 7, 12) == 22)
    check(part2(testInput, 7).also { it.println() } == "6,1")

    val input = readInput("$folder/input")
    measureTime {
        part1(input, 71, 1024).println()
    }.println()
    measureTime {
        part2(input, 71).println()
    }.println()
}

private fun part1(input: List<String>, size: Int, numBytes: Int): Int {
    val corruptedCoordinates = input.map { row ->
        val (x, y) = row.split(",").map { it.toInt() }
        Vector(x, y)
    }.take(numBytes)
    val graph = buildGraph(corruptedCoordinates, size)

    return graph.findShortestPath()
}

private fun buildGraph(corruptedCoordinates: List<Vector>, size: Int): Graph<Vector> {
    val range = 0..<size
    val vertices = range.flatMap { y ->
        range.mapNotNull { x ->
            val data = Vector(x, y)
            if (data !in corruptedCoordinates) {
                Vertex(data)
            } else {
                null
            }
        }
    }.toSet()
    vertices.forEach { vertex ->
        Vector.cardinalDirections.forEach { direction ->
            val neighbor = vertex.data + direction
            vertices.find { it.data == neighbor }
                ?.let { vertex.edges.add(Edge(it, 1)) }
        }
    }
    return Graph(vertices, vertices.first(), vertices.last())
}

private fun part2(input: List<String>, size: Int): String {
    var min = 0
    var max = input.size - 1
    while (max - min > 1) {
        val middle = (min + max) / 2
        val result = part1(input, size, middle)
        if (result >= 0) {
            min = middle
        } else {
            max = middle
        }
    }
    return input[min]
}
