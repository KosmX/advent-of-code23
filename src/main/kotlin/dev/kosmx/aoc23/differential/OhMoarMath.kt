package dev.kosmx.aoc23.differential

import kotlin.math.*


// It's beautiful how mathematical functions can be written down in functional programming :D

fun distance(totalTime: Int, buttonPress: Int): Int = (totalTime - buttonPress) * buttonPress // travel time times speed
// -(buttonPress^2) + totalTime*buttonPress

// dl/dp  -- differentiated by buttonPress
fun dDistance(totalTime: Int, buttonPress: Int) = 2*buttonPress - totalTime
// this one is quite useless

// quadratic solution
// val x = buttonPress
// x^2 - totalTime*x + equals
fun zeroOfDistanceMinusTravel(totalTime: Double, equals: Double): Pair<Double, Double> {
    val discriminant = sqrt(totalTime * totalTime - 4 * equals) // -4ac => -4*1*-equals
    return (totalTime + discriminant)/2 to (totalTime - discriminant)/2
}

fun main() {
    // This input is simple, I don't see its necessary to use files
    val input = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent().lines()


    val races = run {
        val times = input[0].split(" ").asSequence().mapNotNull { it.toLongOrNull() }.toList()
        val distances = input[1].split(" ").asSequence().mapNotNull { it.toLongOrNull() }.toList()

        times.indices.map { times[it] to distances[it] }
    }

    races.fold(1L) { acc, (time, distance) ->
        val solutions = zeroOfDistanceMinusTravel(time.toDouble(), distance.toDouble())
        val margin = (solutions.first.nextDown().toLong() - (solutions.second.toLong()))
        println("solutions: $solutions margin: $margin")
        acc * margin
    }.let {
        println("part1: $it")
    }

    // for part2, just remove spaces
}
