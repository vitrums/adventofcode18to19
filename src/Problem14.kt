import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    val l = Files.readAllLines(Paths.get("input/14.txt"))[0].toCharArray().toMutableList()
    val initial = l.size
    val offset = 190221
//    println(offset)
    val n = 10
    var a = 0
    var b = 1
    while (true) {

        val d = (l[a]-'0') + (l[b]-'0')
        if (d >= 10) {
            l.add((d / 10 + '0'.toInt()).toChar())
            if (l.size >= 6 && l.subList(l.size-6, l.size).joinToString("").toInt() == offset)
                break
        }



        l.add((d % 10 + '0'.toInt()).toChar())
        if (l.size >= 6 && l.subList(l.size-6, l.size).joinToString("").toInt() == offset)
            break

        a = (a + 1 + (l[a] - '0')) % l.size
        b = (b + 1 + (l[b] - '0')) % l.size
    }
//    val res = l.subList(offset, offset+n)
//    println(res.joinToString(""))
    println(l.size - 6)
}