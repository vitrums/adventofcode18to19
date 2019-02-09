import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/1.txt"))
    val res  = lines.map { it.toLong() }.toMutableList()

    val has = mutableSetOf<Long>()

    var freq = 0L
    while (true)
    for (i in 0..res.lastIndex) {
        freq += res[i]
        if (has.contains(freq)) {
            println(freq)
            return
        }
        has.add(freq)
    }
}