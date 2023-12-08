package dev.kosmx.aoc23.router

import java.io.File
import java.math.BigInteger

data class Node(val id: String, val left: String, val right: String)

fun main() {
    val lines = File("maps.txt").readLines()
    val insn = lines[0]


    val p = Regex("^(?<from>\\w{3}) = \\((?<left>\\w{3}), (?<right>\\w{3})\\)$")


    val nodes = lines.asSequence().drop(1).mapNotNull {line ->
        p.find(line)?.let {
            Node(
                it.groups["from"]!!.value,
                it.groups["left"]!!.value,
                it.groups["right"]!!.value
            )
        }
    }.map { it.id to it }.toMap()

    // part1
    /*
    run {
        var length = 0
        var pc = 0
        var node = "AAA"
        while (node != "ZZZ") {
            node = nodes[node]!!.let { if (insn[pc] == 'L') it.left else it.right }
            pc++
            length++
            if (pc >= insn.length) pc = 0
        }

        println("p1 length is $length")
    }// */

    // part2
    run {
        val currentNodes = nodes.keys.filter { it.endsWith("A") }.toMutableList()
        // Here, we need moar meth, LCM to be precise

        val lengths = currentNodes.map { startNode ->
            var pc = 0
            var length = 0
            var node = startNode
            while (!node.endsWith("Z")) {
                node = nodes[node]!!.let { if (insn[pc] == 'L') it.left else it.right }
                pc++
                length++
                if (pc >= insn.length) pc = 0
            }
            BigInteger.valueOf(length.toLong())
        }

        val length = lengths.fold(BigInteger.ONE) {acc, i ->
            (acc * i)/acc.gcd(i)
        }


        println("p2 length is $length")
    }
}