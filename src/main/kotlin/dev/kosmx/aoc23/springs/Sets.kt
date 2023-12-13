package dev.kosmx.aoc23.springs

import java.io.File
import java.math.BigInteger
import javax.swing.Spring

fun <T> Collection<T>.subsets(k: Int): Sequence<Collection<T>> = sequence {
    if (k > size) throw IllegalArgumentException("k must be smaller than the superset size")
    if (k == size) yield(this@subsets)
    else if (k > 0) {
        subsets(k, this@subsets)
    } else {
        yield(emptyList()) // if request is 0 size subset(s), there is one and always one
    }
}

private suspend fun <T> SequenceScope<Collection<T>>.subsets(k: Int, set: Collection<T>, subset: Collection<T> = emptyList()) {
    if (k == 1) {
        yieldAll(set.map { subset + it })
    } else {
        set.forEachIndexed { index, t ->
            subsets(k - 1, set.drop(index + 1), subset + t)
        }
    }
}


// memoized solver
private class Variations(val rules: List<Int>, val springs: String) {
    private val memory: MutableMap<Pair<Int, Int>, BigInteger> = mutableMapOf()

    val variation: Int = springs.length - rules.sum() - rules.size + 1


    val offset = List(rules.size) { index ->
        rules.take(index).sum() + index
    }.toIntArray()

    init {
        assert(offset.last() + rules.last() + variation == springs.length) // sanity check
    }

    // This should be not simply recursive, but as recursive as possible.
    // That's how memoization became powerful
    fun getVariations(rulePos: Int = 0, startPos: Int = 0): BigInteger {
        assert(rulePos < rules.size)
        return memory.getOrPut(rulePos to startPos) {
            // step1: check if pos 0 is valid
            val offs = offset[rulePos]
            val zeroValid = (0 ..< rules[rulePos]).all {i ->
                springs[offs + startPos + i] != '.'
            }
                    && (rulePos + 1 == rules.size || springs[offs + startPos + rules[rulePos]] != '#')
                    && (rulePos != 0 || springs.substring(0 ..< startPos).all { it != '#' })

            val size = if (zeroValid) {
                if (rulePos == rules.size - 1) {
                    val remainingValid = (offs + startPos + rules[rulePos]..< springs.length).all { i ->
                        springs[i] != '#'
                    }
                    if (remainingValid) BigInteger.ONE else BigInteger.ZERO
                } else {
                    getVariations(rulePos + 1, startPos)
                }
            } else BigInteger.ZERO

            size + (if (startPos < variation && springs[offs + startPos] != '#' ) getVariations(rulePos, startPos + 1) else BigInteger.ZERO)
        }
    }
}

fun main() {
    val games = File("springs.txt").readLines().filter { it.isNotBlank() }.map { it }


    val p = Regex("#+")

    val sums = games.map { game ->
        var variations = 0
        val (springs, rules) = game.split(" ").let {
            it[0].let { s ->
                List(5) {s}.joinToString("?")
            } to it[1].let { s ->
                val tmp = s.split(",").map { i -> i.toInt() }
                (0 ..< 5).flatMap { tmp }
            }
        }

        val v = Variations(rules, springs)
        val variation = v.getVariations()

        /* //
        val check = run {
            val totalWrong = rules.sum()

            val missing = totalWrong - springs.count { it == '#' }
            val options = springs.mapIndexedNotNull { index, c -> if (c == '?') index else null }
            for (subset in options.subsets(missing)) {
                val newSprings = springs.toCharArray()
                for (index in subset) {
                    newSprings[index] = '#'
                }
                val matches = p.findAll(newSprings.joinToString("")).map { it.value.length }.toList()
                if (matches == rules) {
                    variations++
                }

            }
            variations
        }
        assert(check.toLong() == variation) // */

        variation
    }
    println("Part1: $sums\nsum is ${sums.sum()}")
}

private fun Collection<BigInteger>.sum(): BigInteger = fold(BigInteger.ZERO) { acc, i -> acc + i}
