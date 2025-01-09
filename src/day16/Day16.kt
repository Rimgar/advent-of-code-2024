package day16

import util.Edge
import util.Graph
import util.Vector
import util.Vertex
import util.VertexDirection
import util.VertexDirection.Horizontal
import util.get
import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day16"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    val testInput2 = readInput("$folder/test2")
    check(part1(testInput) == 7036)
    check(part1(testInput2) == 11048)
    check(part2(testInput) == 45)
    check(part2(testInput2) == 64)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    return buildGraph(input).findShortestPath()
}

private fun part2(input: List<String>): Int {
    return buildGraph(input).findVerticesOfShortestPaths()
        .map { it.data.coordinates }
        .toSet()
        .size
}

private fun buildGraph(input: List<String>): Graph<VertexData> {
    val startCoordinates = findCoordinates(input, 'S')
    val startVertex = Vertex(VertexData(startCoordinates, Horizontal))
    val destinationCoordinates = findCoordinates(input, 'E')
    val destinationVertex = Vertex(VertexData(destinationCoordinates, null))
    val vertices = mutableSetOf(startVertex, destinationVertex)

    val toProcess = mutableSetOf(startVertex)

    while (toProcess.isNotEmpty()) {
        val currentVertex = toProcess.first()
        toProcess.remove(currentVertex)

        // straight
        currentVertex.data.direction?.vectors?.forEach { direction ->
            val newCoordinates = currentVertex.data.coordinates + direction
            if (input[newCoordinates] != '#') {
                val targetVertex = if (input[newCoordinates] == 'E') {
                    destinationVertex
                } else {
                    val newVertex = Vertex(VertexData(newCoordinates, currentVertex.data.direction))
                    vertices.find { it == newVertex }
                        ?: newVertex.also {
                            vertices += newVertex
                            toProcess += newVertex
                        }
                }
                currentVertex.edges.add(Edge(targetVertex, 1))
            }
        }

        // turn
        currentVertex.data.direction?.vectors?.first()?.let { direction ->
            if (input[currentVertex.data.coordinates + direction.turnLeft()] != '#'
                || input[currentVertex.data.coordinates + direction.turnRight()] != '#'
            ) {
                var newVertex = Vertex(VertexData(currentVertex.data.coordinates, VertexDirection.fromVector(direction.turnLeft())))
                newVertex = vertices.find { it == newVertex }
                    ?: newVertex.also {
                        vertices += newVertex
                        toProcess += newVertex
                    }
                currentVertex.edges.add(Edge(newVertex, 1000))
            }
        }
    }

    return Graph(vertices, startVertex, destinationVertex)
}

private fun findCoordinates(input: List<String>, tile: Char): Vector {
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            if (c == tile) return Vector(x, y)
        }
    }
    throw IllegalArgumentException("Tile $tile not found")
}

private data class VertexData(
    val coordinates: Vector,
    val direction: VertexDirection?
)
