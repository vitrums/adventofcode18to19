import java.nio.file.Files
import java.nio.file.Paths

class Node (val number: Int) {
    var l: Node = this
    var r: Node = this


    fun add(n: Int) : Pair<Node, Int> {
        if (n % 23 == 0) {
            val res =  l.l.l.l.l.l
            val score = res.l.number + n
            res.l = res.l.l
            res.l.r = res
            return res to score
        } else {
            val left = r;
            val right = r.r;
            val res = Node(n)
            res.l = left
            res.r = right
            left.r = res
            right.l = res

            return res to 0
        }
    }
}

fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/9.txt"))

    for (l in lines) {
        val stream = l.split(" ").map { it.toInt() }
        val nplayers = stream[0]
        val nmarbles = stream[1]

        var cur = Node(0)
        var curPlayer = -1
        val scores = LongArray(nplayers)
        for (i in 1..nmarbles) {
            curPlayer = (++ curPlayer) % nplayers
            val (node, score) = cur.add(i)

            scores[curPlayer] += score.toLong()
//            if (score == nmarbles)
//                break
            cur = node
        }

        println(scores.max())
    }
}

