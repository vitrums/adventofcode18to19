import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


class Point(val x : Int, val y : Int) {
    infix fun dist(p: Point) = Math.abs(x - p.x) + Math.abs(y - p.y)
}

fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/6.txt"))

    val points = mutableListOf<Point>()
    for (line in lines) {
        val s = Scanner(line).useDelimiter("[, ]+")
        val x = s.nextInt()
        val y = s.nextInt()

        points.add(Point(x, y))
    }

//    val n = 500
//    val a = Array(n) {IntArray(n)}
//
//    for (i in 0..n-1)
//        for (j in 0..n-1) {
//            val p = Point(i, j)
//            a[i][j] = (0..points.lastIndex).exclusiveMinBy { points[it].dist(p) } ?: -1
//        }
//
//    val forbidden = mutableSetOf<Int>()
//
//    for (i in 0 until n) {
//        forbidden.add(a[i][0])
//        forbidden.add(a[i][n-1])
//        forbidden.add(a[0][i])
//        forbidden.add(a[n-1][i])
//    }
//
//    val res = (0..points.lastIndex)
//        .filter { !forbidden.contains(it) }
//        .map { p -> a.sumBy { it.count { elt -> elt == p } } }
//        .max()
//    println(res)

    var res = 0
    for (i in -1000..1500)
        for (j in -1000..1500)
            if (points.sumBy { it.dist(Point(i,j))} < 10000)
                res++

    println(res)
}