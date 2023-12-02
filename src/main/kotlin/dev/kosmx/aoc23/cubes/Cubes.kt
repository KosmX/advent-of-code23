package dev.kosmx.aoc23.cubes

import java.io.File
import kotlin.math.max

fun main() {
    val games = File("cubes.txt").readLines().map { Game.of(it) }
    println(games)

    games.sumOf { game ->
        val r = game.rounds.fold(Round()) { acc, round ->
            Round(max(acc.red, round.red), max(acc.green, round.green), max(acc.blue, round.blue))
        }
        r.red * r.green * r.blue
    }
        .let { println(it) }
}

data class Round(val red: Int = 0, val green: Int = 0, val blue: Int = 0)

data class Game(
    val gameId: Int,
    val rounds: List<Round>,
) {
    companion object {
        private val linePattern = Regex("^Game (?<gameid>\\d+): (?<rounds>.+)\$")
        private val redPattern = Regex("((?<num>\\d+) red)")
        private val greenPattern = Regex("((?<num>\\d+) green)")
        private val bluePattern = Regex("((?<num>\\d+) blue)")
        fun of(line: String): Game {
            val match = linePattern.find(line)!!
            val gameId = match.groups["gameid"]!!.value.toInt()
            val rounds = match.groups["rounds"]!!.value.split(";").map {
                Round(
                    red = redPattern.find(it)?.groups?.get("num")?.value?.toInt() ?: 0,
                    green = greenPattern.find(it)?.groups?.get("num")?.value?.toInt() ?: 0,
                    blue = bluePattern.find(it)?.groups?.get("num")?.value?.toInt() ?: 0,
                )
            }
            return Game(gameId, rounds)
        }
    }
}
