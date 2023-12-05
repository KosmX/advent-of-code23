package dev.kosmx.aoc23.scratchcards

import java.io.File

fun main() {
    val lines = File("cards.txt").readLines()

    val p = Regex("^Card +(?<cardId>\\d+): (?<win>[^\\|]+) \\| (?<have>.+)$")

    val cards = lines.mapNotNull { line ->
        p.find(line)
    }.map { match ->
        ScratchCard(
            id = match.groups["cardId"]!!.value.toInt(),
            winning = match.groups["win"]!!.value.split(" ").mapNotNull { it.toIntOrNull() }.toList(),
            yours = match.groups["have"]!!.value.split(" ").mapNotNull { it.toIntOrNull() }.toSet(),
        )
    }.map { it.id to it }.toMap()

    cards.entries.sumOf { (_, it) ->
        it.points
    }.let {
        println("Part1: $it")
    }

    // part 2

    val cardsToProcess = cards.entries.asSequence().map { it.value }.toMutableList()

    var nCards = 0
    while (true) {
        val next = cardsToProcess.removeLastOrNull() ?: break
        nCards++
        for (i in next.id + 1..<(next.id + next.matches + 1)) {
            cardsToProcess += cards[i]!!
        }
    }

    println("Part 2: $nCards")

}

data class ScratchCard(val id: Int, val winning: List<Int>, val yours: Set<Int>) {
    val points: Int
        get() {
            val count = matches
            if (count == 0) return 0
            return 1 shl (count - 1)
        }

    val matches: Int
        get() = winning.intersect(yours).size

}
