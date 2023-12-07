package dev.kosmx.aoc23.garden

import java.io.File
import java.math.BigInteger
import kotlin.math.abs

typealias Offset = Pair<OpenEndRange<BigInteger>, BigInteger>

val OpenEndRange<BigInteger>.size: BigInteger
    get() = endExclusive - start

val Offset.destination: OpenEndRange<BigInteger>
    get() = (first.start + second) ..< (first.endExclusive + second)

fun List<Offset>.binarySearch(i: BigInteger) = binarySearch { (range, _) ->
    if (i in range) {
        0
    } else {
        range.start.compareTo(i)
    }
}

operator fun List<OpenEndRange<BigInteger>>.contains(item: BigInteger): Boolean {
    val idx = binarySearch { range ->
        if (item in range) {
            0
        } else {
            range.start.compareTo(item)
        }
    }
    return this.getOrNull(idx)?.contains(item) ?: false
}

fun min(a: BigInteger, b: BigInteger): BigInteger = if (a < b) a else b
class RangeMapping(ranges: List<Offset> = listOf()) {
    val rangesList: List<Offset>

    init {
        rangesList = ranges.sortedBy { it.first.start }
    }

    operator fun get(i: BigInteger): BigInteger {
        val r = this.rangesList.binarySearch(i)

        return if (r >= 0) {
            i + rangesList[r].second
        } else i
    }

    override fun toString(): String {
        return "RangeMapping(rangesList=$rangesList)"
    }


    // one step of simplify
    operator fun plus(rhs: RangeMapping): RangeMapping {
        val newRanges = mutableListOf<Offset>()
        val newRangesB = rhs.rangesList.toMutableList()

        for (range in rangesList) {
            val currentRange = range.destination
            var current = currentRange.start

            var targetRangeIdx = abs(newRangesB.binarySearch(current))

            while (current < currentRange.endExclusive && targetRangeIdx < newRangesB.size) {
                val dest = newRangesB[targetRangeIdx]
                if (current in dest.first) { // two ranges intersect, cut both range

                    // cut destination range if necessary
                    if (current > dest.first.start) {
                        newRangesB.add(targetRangeIdx, dest.first.start ..< current to dest.second)
                        targetRangeIdx++
                    }

                    val newEnd = min(currentRange.endExclusive, dest.first.endExclusive)
                    newRanges += current-range.second ..< newEnd-range.second to range.second + dest.second
                    current = newEnd

                    if (newEnd < dest.first.endExclusive) {
                        newRangesB[targetRangeIdx] = newEnd ..< dest.first.endExclusive to dest.second
                        targetRangeIdx++
                    } else {
                        newRangesB.removeAt(targetRangeIdx)
                    }
                } else {
                    val newEnd = min(currentRange.endExclusive, dest.first.start)
                    newRanges += current-range.second ..< newEnd-range.second to range.second
                    current = newEnd

                }
            }
            if (current < range.first.endExclusive) {
                newRanges += current-range.second ..< range.first.endExclusive to range.second
            }
        }

        newRanges.removeIf { it.first.isEmpty() }

        run {
            var i = 0
            while (i < newRangesB.size) {
                val r = newRangesB[i]
                val pos = abs(newRanges.binarySearch(r.first.start))
                if (r.first.isEmpty() || newRanges.size > pos && newRanges[pos].first.start < r.first.endExclusive) {
                    // there is some intersection
                    // new r range start after the other range
                    if (r.first.isEmpty() || r.first.start >= newRanges[pos].first.start) {
                        if (r.first.isEmpty() || r.first.endExclusive <= newRanges[pos].first.endExclusive) {
                            newRangesB.removeAt(i) // just throw it away
                        } else {
                            newRangesB[i] = newRanges[pos].first.endExclusive ..< r.first.endExclusive to r.second // trim it
                        }
                    } else { // the current range starts before the other, we cut it always
                        newRangesB[i] = r.first.start ..< newRanges[pos].first.start to r.second // this range is okay

                        // add the following part, next iteration can handle it.
                        newRangesB.add(i + 1, newRanges[pos].first.start ..< r.first.endExclusive to r.second)
                        i++
                    }

                } else {
                    i++
                }
            }
        }

        newRanges += newRangesB // now, these shouldn't intersect
        // make it sorted
        newRanges.sortBy { it.first.start }

        // optimise this mess
        var i = 0
        while (i < newRanges.size - 1) {
            val current = newRanges[i]
            val next by lazy { newRanges[i + 1] }
            when {
                current.second == BigInteger.ZERO || current.first.isEmpty() -> {
                    newRanges.removeAt(i)
                }
                current.first.endExclusive == next.first.start && current.second == next.second -> {
                    newRanges[i] = current.first.start ..< next.first.endExclusive to current.second
                    newRanges.removeAt(i + 1)
                }
                else -> i++
            }
        }
        return RangeMapping(newRanges)
    }

}

fun main() {
    val lines = File("mapping.txt").readLines().filter { it.isNotBlank() }

    val seeds = mutableListOf<BigInteger>()

    val conversionSequence = mutableListOf<RangeMapping>()

    // parse seeds
    seeds += lines[0].split(" ").drop(1).map { BigInteger(it) }

    val p = Regex("^(?<from>\\w+)-to-(?<to>\\w+) map:$")

    var i = 1
    while (lines.size > i) {
        val rangeList = mutableListOf<Pair<OpenEndRange<BigInteger>, BigInteger>>()
        while (lines.size > i && !p.matches(lines[i])) {
            val l = lines[i].split(" ").map { BigInteger(it) }
            rangeList += (l[1] ..< (l[1] + l[2])) to l[0] - l[1]
            i++
        }
        if (rangeList.isNotEmpty()) {
            conversionSequence += RangeMapping(rangeList)
        }
        i++
    }

    println(conversionSequence)
    val min = seeds.map { conversionSequence[it] }.toList().min()
    println("The lowest number Id is $min")

    val range = conversionSequence.simplify()

    val seedRanges = lines[0].split(" ").asSequence().drop(1).map { BigInteger(it) }.windowed(2, 2).map { (start, len) ->
        start ..< start + len
    }.sortedBy { it.start }.toList()


    var minSeed = range[seedRanges[0].start]
    (range.rangesList.asSequence().map { it.first.start } + seedRanges.asSequence().map { it.start }).filter {
        it in seedRanges
    }.forEach {
        val new = conversionSequence[it]
        if (new < minSeed) {
            minSeed = new
        }
    }

    println("Part2: $minSeed")

}

// This is the key for Part2
// Something is still not okay, but I don't care right now
fun List<RangeMapping>.simplify(): RangeMapping = fold(RangeMapping()) { acc, rangeMapping ->
    acc + rangeMapping
}

operator fun List<RangeMapping>.get(i: BigInteger, debug: Boolean = false): BigInteger {
    if (debug) print(i)
    return fold(i) {acc, rangeMapping ->
        rangeMapping[acc].also {
            if(debug) print(" -> $it")
        }
    }.also {
        if (debug) println()
    }
}
