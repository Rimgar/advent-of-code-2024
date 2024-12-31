package util

import kotlin.math.abs

data class Vector(
    val x: Int,
    val y: Int
) {

    val first = x
    val second = y
    val length = abs(x) + abs(y)

    operator fun unaryMinus() = Vector(-x, -y)
    operator fun plus(p: Vector) = Vector(x + p.x, y + p.y)
    operator fun minus(p: Vector) = Vector(x - p.x, y - p.y)
    operator fun times(p: Int) = Vector(x * p, y * p)
    operator fun div(p: Int) = Vector(x / p, y / p)
    fun mod(p: Vector) = Vector(x.mod(p.x), y.mod(p.y))

    fun turnRight() = Vector(-y, x)
    fun turnLeft() = Vector(y, -x)

    fun inRangeOf(input: List<String>) = y in input.indices && x in input.first().indices

    fun cityBlockDistance(other: Vector) = (other - this).length

    override fun toString(): String = "($x, $y)"

    companion object {

        val cardinalDirections = listOf(Vector(0, -1), Vector(1, 0), Vector(0, 1), Vector(-1, 0))
    }
}

operator fun List<String>.get(p: Vector) = this[p.y][p.x]
fun List<String>.getOrNull(p: Vector) = this.getOrNull(p.y)?.getOrNull(p.x)

operator fun List<List<Any>>.get(p: Vector) = this[p.y][p.x]
fun List<List<Any>>.getOrNull(p: Vector) = this.getOrNull(p.y)?.getOrNull(p.x)

data class LongVector(
    val x: Long,
    val y: Long
) {

    operator fun unaryMinus() = LongVector(-x, -y)
    operator fun plus(p: LongVector) = LongVector(x + p.x, y + p.y)
    operator fun minus(p: LongVector) = LongVector(x - p.x, y - p.y)
    operator fun times(p: Int) = LongVector(x * p, y * p)
    operator fun times(p: Long) = LongVector(x * p, y * p)
    operator fun div(p: Long) = LongVector(x / p, y / p)

    override fun toString(): String = "($x, $y)"
}
