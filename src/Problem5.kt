import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


fun react(s: String) : Int {
    val a = LinkedList<Char>()

    for (c in s) {
        if (!a.isEmpty() && c.toLowerCase() == a[a.lastIndex].toLowerCase() && c != a[a.lastIndex])
            a.removeLast()
        else
            a.add(c)
    }

    return a.size
}

fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/5.txt"))

    val line = lines[0]

    val res = ('a'..'z')
        .map { c -> react(
            line.replace("$c", "").replace("${c.toUpperCase()}","")
         ) }
        .min()
    println(res)
}