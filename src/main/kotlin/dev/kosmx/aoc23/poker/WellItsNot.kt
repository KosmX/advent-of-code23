package dev.kosmx.aoc23.poker

import java.io.File
import java.math.BigInteger


fun main() {
    val games = File("poker.txt").readLines().map {
        var (cards, score) = it.split(" ")
        //cards = cards.replace("J", "X") Uncomment it to solve Part1
        Hand(cards) to score.toInt()
    }.sortedByDescending { it.first }


    println(games.filter { game -> game.first.cards.any { it == WILDCARD } }.joinToString("\n"))

    var score = BigInteger.ZERO
    for (i in games.indices) {
        score += BigInteger.valueOf(games[i].second.toLong()) * BigInteger.valueOf(i.toLong()+1)
    }
    println("Part1: Total score is $score")
}


data class Card(val letter: Char) : Comparable<Card> {

    private companion object {
        val ordering = arrayOf('A', 'K', 'Q', 'X', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
    }
    override fun compareTo(other: Card): Int {
        return ordering.indexOf(this.letter).compareTo(ordering.indexOf(other.letter))
    }
}

val WILDCARD = Card('J')

class Hand(input: String): Comparable<Hand> {
    val cards: Array<Card>

    init {
        cards = input.map { Card(it) }.toTypedArray()
        assert(cards.size == 5)
    }

    override fun compareTo(other: Hand): Int {
        if (type == other.type) {
            for (i in 0..<5) {
                val c = cards[i].compareTo(other.cards[i])
                if (c != 0) return c
            }
        }
        return other.type.compareTo(type) // compare but inverted
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hand

        return cards.contentEquals(other.cards)
    }

    override fun hashCode(): Int {
        return cards.contentHashCode()
    }

    override fun toString(): String {
        return "Hand(cards=${cards.joinToString(separator = "") { it.letter.toString() }}, type=$type)"
    }

    val type: Int
        get() {
            val groups = cards.asSequence().groupingBy { it }.eachCount()

            val wildcard = groups[Card('J')] ?: 0

            return when {

                //Only one type or two types including wildcard type. Any number of wildcard can exist in this case
                // edge-case here: all joker
                groups.size == 1 || groups.filter { it.key != WILDCARD }.any { it.value + wildcard == 5 } -> 6

                // Max 3 wildcards here

                //There is at least one group with size of four including wildcards.
                //Max 3 wildcards, if there is more, that would make a five
                // If there are 3 wildcards, this will happen anyway
                groups.filter { it.key != WILDCARD }.any { it.value + wildcard == 4 } -> 5

                // Max 2 wildcards and at least 3 groups

                // 2 groups are easy: If the sizes are 1 and 4, it is a four, it can't be. Only 2 and 3 or 3 and 2 are possible...
                // Wildcard case:
                // 1 single element can't be again
                // only possibility: 2, 2 and 1 wildcard which is matched
                //groups.size == 2 || groups.size == 3 && wildcard != 0 -> 4 // there are only two types or three types with wildcards
                groups.filter { it.key != WILDCARD }.size == 2 -> 4

                // There is any three with wildcards
                groups.any { it.value + wildcard == 3 } -> 3

                // Max 1 wildcard

                // if there would be more wildcards than 1, it would be a three
                // 3 groups are impossible
                // the wildcard will pair with a single card creating 1 extra group
                else -> (groups.count { it.value >= 2 } + wildcard).coerceAtMost(2) // 2 1 or 0 // if we have at least 2 wildcards, it'll be a three in a kind, only one wildcard can exist
                // which will increase the amount of groups but only to two
            }
        }

}