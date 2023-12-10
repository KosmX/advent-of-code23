package dev.kosmx.aoc23.graphs

import java.io.File
import java.util.PriorityQueue

operator fun List<String>.get(x: Int, y: Int): Char = this.getOrNull(y)?.getOrNull(x) ?: '.'

operator fun List<String>.get(xy: Pair<Int, Int>): Char = this[xy.first, xy.second]

operator fun List<String>.contains(xy: Pair<Int, Int>): Boolean = xy.second in indices && xy.first in this[0].indices

val deltas = listOf(-1 to 0, 0 to -1, 1 to 0, 0 to 1)

val neighbours: Map<Char, Collection<Pair<Int, Int>>> = mapOf(
    '.' to listOf(),
    'S' to deltas,
    'L' to listOf(0 to -1, 1 to 0),
    'F' to listOf(1 to 0, 0 to 1),
    'J' to listOf(0 to -1, -1 to 0),
    '7' to listOf(-1 to 0, 0 to 1),
    '-' to listOf(-1 to 0, 1 to 0),
    '|' to listOf(0 to -1, 0 to 1)
)

fun main() {
    val map = File("pipes.txt").readLines()
    val start = map.indexOfFirst { 'S' in it }.let { y -> map[y].indexOf('S') to y }

    println("Start at $start")

    val lengths = mutableMapOf(start to 0)

    val seen = PriorityQueue<Pair<Int, Int>> { a, b -> lengths[a]!!.compareTo(lengths[b]!!) }.apply {
        this += start
    }

    // coordinate to vector
    val directions: MutableMap<Pair<Int, Int>, Pair<Int, Int>> = mutableMapOf(start to (0 to 0))

    // Let's BFS
    piece@while (seen.any()) {
        val head: Pair<Int, Int> = seen.remove()
        for (delta in neighbours[map[head]]!!) {
            val newPos = head + delta
            if (newPos !in lengths && neighbours[map[newPos]]!!.contains(-delta)) {
                lengths[newPos] = lengths[head]!! + 1
                // update direction vectors for part2
                directions[head] = directions.getOrDefault(head, 0 to 0) + delta
                directions[newPos] = directions.getOrDefault(newPos, 0 to 0) + delta

                seen += newPos
                continue@piece
            }
        }
    }

    println("Part1: ${lengths.maxBy { (it.value+1)/2 }}")

    // part 2, false for negative true for positive area
    val areas = mutableMapOf<Pair<Int, Int>, Boolean>()

    // last created list
    var lastUpdate = listOf<Pair<Pair<Int, Int>, Boolean>>()


    run { // Round 0
        val new = mutableListOf<Pair<Pair<Int, Int>, Boolean>>()
        for (y in map.indices) {
            for (x in map[y].indices) {
                if ((x to y) !in directions) {
                    deltas.asSequence().map { d -> d to (x to y) + d }
                        .mapNotNull { directions[it.second]?.let { (dx, dy) -> it.first * (dy to -dx) } }
                        .firstOrNull()?.let { side ->
                            if (side != 0) {
                                new += (x to y) to (side > 0)
                            }
                        }
                }
            }

        }
        lastUpdate = new
        areas += lastUpdate
    }

    //*
    while (lastUpdate.isNotEmpty()) {
        val new = mutableListOf<Pair<Pair<Int, Int>, Boolean>>()
        lastUpdate.forEach { (pos, side) ->
            deltas.forEach { delta ->
                val p = pos + delta
                if (p !in directions && p !in areas && p in map) {
                    areas += p to side
                    new += p to side
                }
            }
        }

        lastUpdate = new
    }// */


    //*
    for (y in map.indices) {
        for (x in map[y].indices) {
            val area = areas[x to y]
            val areaCode = if (area != null) if(area) "I" else "O" else "X"

            val direction = directions[x to y]?.let {
                when(it) {
                    (1 to 0), (2 to 0) -> '→'
                    -1 to 0, -2 to 0 -> '←'
                    0 to 1, 0 to 2 -> '↓'
                    0 to -1, 0 to -2 -> '↑'
                    1 to 1, -> '↘'
                    1 to -1 -> '↗'
                    -1 to -1 -> '↖'
                    -1 to 1 -> '↙'
                    else -> '↻'
                }
            }

            print((direction ?: areaCode).toString().padEnd(1, ' '))
        }
        println()
    }// */

    val groups = areas.asSequence().groupingBy { it.value }.eachCount()
    println(groups)
}



// Math!
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> = first + other.first to second + other.second
operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> = first - other.first to second - other.second
operator fun Pair<Int, Int>.unaryMinus(): Pair<Int, Int> = -first to -second

// Mathematical dot product
operator fun Pair<Int, Int>.times(other: Pair<Int, Int>): Int = this.first * other.first + this.second * other.second
