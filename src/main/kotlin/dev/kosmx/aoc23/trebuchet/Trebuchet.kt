package dev.kosmx.aoc23.trebuchet

import java.io.File

val textNumbers = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)

fun main() {

    val pattern = Regex("(?=(?<number>\\d|one|two|three|four|five|six|seven|eight|and|nine))")

    val lines = File("calibration.txt").readLines()
    lines.map { str ->
        println(str)
        val matches = pattern.findAll(str)
        val first = matches.first().toDigit()
        val last = matches.last().toDigit()

        return@map first * 10 + last
    }.sum().let {
        println(it)
    }
}

private fun MatchResult.toDigit(): Int {
    val str = groups["number"]!!.value
    return textNumbers[str] ?: str.toInt()
}
