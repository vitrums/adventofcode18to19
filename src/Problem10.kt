import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


data class Vector(val x: Int, val y: Int) {
    operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)
}
data class Particle(var pos: Vector, val speed: Vector) {
    val x: Int get() = pos.x
    val y: Int get() = pos.y

    fun next() {
        pos += speed
    }
}

fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/10.txt"))

    val points = mutableListOf<Particle>()

    for (l in lines) {
        val st = StringTokenizer(l, "<=, >", false)
        st.nextToken()
        val pos = Vector(st.nextToken().toInt(), st.nextToken().toInt())

        st.nextToken()
        val speed = Vector(st.nextToken().toInt(), st.nextToken().toInt())

        points.add(Particle(pos, speed))
    }
    println(points.size)


    var prevw = 1000000
    while (true) {
        val yy = points.map { it.y }
        val width = yy.max ()!! - yy.min() !!
        println(width)
        if (width == 9) break
        prevw = width
        points.forEach { it.next()}
    }

    points.sortBy { it.x }
    points.sortBy { -it.y }

    val xx = points.map { it.x }
    val yy = points.map { it.y }

    val minx = xx.min()!!
    val maxx = xx.max()!!
    val miny = yy.min()!!
    val maxy = yy.max()!!

    val message = Array(maxy-miny+1) {CharArray(maxx-minx+1) {'.'} }
    for (point in points) {
        message[point.y - miny][point.x-minx] = '#'
    }

    println(message.joinToString ("\n") { String(it)})
}

