import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/2.txt"))
//
//    var s2 = 0L;
//    var s3 = 0L;
//    for (l in lines) {
//        var p2 = 0
//        var p3 = 0
//
//        for (c in l) {
//            val count = l.count { it == c }
//            if (count == 2) p2 = 1
//            if (count == 3) p3 = 1
//        }
//
//        s2 += p2
//        s3 += p3
//    }
//    println(s2*s3)



    fun diffIdx(a: String, b: String) : Int {
        var res = -1
        for (i in 0..a.lastIndex) {
            if (a[i] != b[i]) {
                if (res < 0)
                    res = i
                else
                    return -1
            }
        }

        return res
    }

    for (i in 0..lines.lastIndex - 1) {
        val line = lines[i]
        for (j in (i + 1)..lines.lastIndex) {
            val idx = diffIdx(line, lines[j])

            if (idx >= 0) {
                println(line.slice(0..idx - 1) + line.slice((idx + 1)..line.lastIndex))
                return
            }
        }
    }
}