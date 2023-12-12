package dev.kosmx.aoc23.stars

import java.io.File
import java.math.BigInteger
import kotlin.math.abs

operator fun MutableList<MutableList<Char>>.get(x: Int, y: Int): Char {
    return this[y][x]
}

fun <T> List<T>.allPairings() = sequence {
    for (a in indices) {
        for (b in a + 1 ..< size) {
            yield(get(a) to get(b))
        }
    }
}

fun main() {
    val starMap = File("stars.txt").useLines {
        it.map { it.toCharArray().toMutableList() }.toMutableList()
    }

    // insert rows
    for (x in starMap.indices.reversed()) {
        if (starMap.all { it[x] == '.' }) {
            starMap.forEach {
                it.add(x, '+')
            }
        }
    }

    // insert columns
    for (y in starMap.indices.reversed()) {
        if (starMap[y].all { it == '.' || it == '+' }) {
            starMap.add(y, MutableList(starMap[y].size) { '+' })
        }
    }

    // print map for debug
    //*
    starMap.forEach {
        println(it.joinToString(""))
    }// */

    // collect stars
    val stars = mutableListOf<Pair<Int, Int>>()
    for (y in starMap.indices) {
        for (x in starMap[y].indices) {
            if (starMap[x, y] == '#') {
                stars.add(x to y)
            }
        }
    }


    val distances = stars.allPairings().map { (a, b) ->
        abs(a.first - b.first) + abs(a.second - b.second)
    }.toList()

    println(distances.fold(BigInteger.ZERO) { acc, i -> acc + i.toBigInteger() })

    // now part 2

    val distances2 = stars.allPairings().map { (a, b) ->
        var holes = 0
        for (i in a.first progress b.first) {
            if (starMap[i, a.second] == '+') {
                holes++
            }
        }
        for (i in a.second progress b.second) {
            if (starMap[b.first, i] == '+') {
                holes++
            }
        }
        abs(a.first - b.first) + abs(a.second - b.second) + (holes*(1000000-2))
    }.toList()

    println(distances2.fold(BigInteger.ZERO) { acc, i -> acc + i.toBigInteger() })

}

private infix fun Int.progress(first: Int): IntProgression {
    return if (this < first) {
        first downTo this
    } else {
        first..this
    }
}
