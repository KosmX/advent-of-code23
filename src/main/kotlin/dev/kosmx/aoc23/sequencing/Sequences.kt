package dev.kosmx.aoc23.sequencing

import java.io.File
import java.math.BigInteger

fun main() {
    val sequences = File("sequences.txt").readLines().filter { it.isNotEmpty() }.map { line -> line.split(" ").map { BigInteger(it) } }

    val differential = sequences.map { sequence ->
        val layers = mutableListOf(sequence)
        while (!layers.last().all { it == BigInteger.ZERO }) {
            layers += layers.last().zipWithNext().map { (a, b) ->
                b - a
            }
        }
        layers.toList()
    }

    val nextValues = differential.map { sequence ->
        return@map sequence.foldRight(BigInteger.ZERO) { entry, acc ->
            entry.last() + acc
        }
    }


    println("Part1: ${nextValues.sumOf { it }}")

    // Now, the negative history

    val prevValues = differential.map { sequence ->
        return@map sequence.foldRight(BigInteger.ZERO) { entry, acc ->
            entry.first() - acc
        }
    }
    println("Part2: ${prevValues.sumOf { it }}")



}