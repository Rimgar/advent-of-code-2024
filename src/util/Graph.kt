package util

class Graph<T>(
    val vertices: Set<Vertex<T>>,
    val startVertex: Vertex<T>,
    val destinationVertex: Vertex<T>
) {

    fun findShortestPath(): Int {
        val weightMap = mutableMapOf(startVertex to 0).withDefault { Int.MAX_VALUE }
        val queue = vertices.toMutableSet()

        while (queue.isNotEmpty()) {
            val currentElement = queue.minBy { weightMap.getValue(it) }
            queue.remove(currentElement)
            val currentWeight = weightMap.getValue(currentElement)
            if (currentElement == destinationVertex) {
                return currentWeight
            }
            currentElement.edges.forEach { edge ->
                val newWeight = currentWeight + edge.weight
                if (newWeight < weightMap.getValue(edge.target)) {
                    weightMap[edge.target] = newWeight
                }
            }
        }

        throw IllegalStateException("A path to the destination should have been found")
    }

    fun findVerticesOfShortestPaths(): Set<Vertex<T>> {
        val weightMap = mutableMapOf(startVertex to 0).withDefault { Int.MAX_VALUE }
        val previousVertex = mutableMapOf<Vertex<T>, MutableSet<Vertex<T>>>()
        val queue = vertices.toMutableSet()

        while (queue.isNotEmpty()) {
            val currentElement = queue.minBy { weightMap.getValue(it) }
            queue.remove(currentElement)
            val currentWeight = weightMap.getValue(currentElement)
            if (currentElement == destinationVertex) {
                break
            }
            currentElement.edges.forEach { edge ->
                val newWeight = currentWeight + edge.weight
                val oldWeight = weightMap.getValue(edge.target)
                if (newWeight < oldWeight) {
                    weightMap[edge.target] = newWeight
                    previousVertex[edge.target] = mutableSetOf(currentElement)
                } else if (newWeight == oldWeight) {
                    previousVertex[edge.target]?.add(currentElement)
                }
            }
        }

        val vertices = mutableSetOf<Vertex<T>>()
        val toProcess = ArrayDeque<Vertex<T>>().apply {
            addLast(destinationVertex)
        }
        while (toProcess.isNotEmpty()) {
            val currentVertex = toProcess.removeFirst()
            if (currentVertex in vertices) continue
            vertices += currentVertex
            previousVertex[currentVertex]?.let { toProcess.addAll(it) }
        }
        return vertices
    }
}

data class Vertex<T>(
    val data: T,
) {

    val edges = mutableSetOf<Edge<T>>()
}

data class Edge<T>(
    val target: Vertex<T>,
    val weight: Int
) {

    override fun hashCode(): Int = target.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Edge<*>

        return target == other.target
    }
}

enum class VertexDirection(val vectors: List<Vector>) {
    Horizontal(listOf(Vector(1, 0), Vector(-1, 0))),
    Vertical(listOf(Vector(0, 1), Vector(0, -1)));

    companion object {

        fun fromVector(v: Vector): VertexDirection {
            return if (v.x == 0) {
                if (v.y == 0) {
                    throw IllegalArgumentException("Zero vector does not have a direction")
                } else {
                    Vertical
                }
            } else if (v.y == 0) {
                Horizontal
            } else {
                throw IllegalArgumentException("Vector has both components != 0")
            }
        }
    }
}
