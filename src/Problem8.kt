package p8
import java.nio.file.Files
import java.nio.file.Paths


class Node(val children: MutableList<Node>, val metadata: MutableList<Int>) {
    fun sumMetadata(): Int = metadata.sum() + children.sumBy { it.sumMetadata() }
    fun valueOfNode(): Int {
        return if (children.isEmpty())
            metadata.sum()
        else
            metadata.sumBy { if (it > children.size) 0 else children[it-1].valueOfNode() }
    }

    companion object {
        fun parse(stream: Iterator<Int>) : Node {
            val nchildren = stream.next()
            val nmetadata = stream.next()

            val res = Node(mutableListOf(), mutableListOf())
            repeat(nchildren) {
                res.children.add(parse(stream))
            }
            repeat(nmetadata) {
                res.metadata.add(stream.next())
            }
            return res;
        }
    }
}

fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/8.txt"))

    for (l in lines) {
        val stream = l.split(" ").map { it.toInt() }
        val root = Node.parse(stream.iterator())
        println(root.sumMetadata())
        println(root.valueOfNode())
    }
}

