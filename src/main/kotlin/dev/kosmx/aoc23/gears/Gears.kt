package dev.kosmx.aoc23.gears

import java.io.File

operator fun List<String>.get(x: Int, y: Int): Char {
    return try {
        this[y][x]
    } catch (e: IndexOutOfBoundsException) {
        '.'
    }
}

fun main() {
    val matrix = File("gears.txt").readLines()
    val width = matrix[0].length

    val r = Regex("\\d+")


    var a = 0
    for (y in matrix.indices) {
        val numbers = r.findAll(matrix[y])
        numbers.filter { match ->
            val sx = match.range.let { it.first -1 .. it.last + 1 }
            val sy = y-1 .. y+1
            sx.flatMap { tx -> sy.map { ty -> matrix[tx, ty] } }.any { it != '.' && !it.isDigit() }
        }.map {
            it.value.toInt()
        }.sum().let {
            a += it
        }
    }
    println("Part1: $a")

    // part2
    val gears = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()

    for (y in matrix.indices) {
        val numbers = r.findAll(matrix[y])
        numbers.flatMap { match ->
            val sx = match.range.let { it.first -1 .. it.last + 1 }
            val sy = y-1 .. y+1
            sx.flatMap { tx -> sy.map { ty -> (tx to ty) to matrix[tx, ty] } }.filter { it.second == '*' }.map {
                it.first to match.value.toInt()
            }
        }.forEach {(pos, value) ->
            val l = gears.getOrPut(pos) { mutableListOf() }
            l += value
        }
    }

    val p2 = gears.asSequence().filter { it.value.size == 2 }.sumOf { it.value[0] * it.value[1] }
    println("Part2: $p2")
}