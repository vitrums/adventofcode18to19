import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


fun main(args: Array<String>) {

    val a = Array(1000) { Array (1000) { 0 }}
    val lines = Files.readAllLines(Paths.get("input/3.txt"))

    for (line in lines) {

        //#18 @ 925,936: 28x28
        val s = Scanner(line).useDelimiter("[,#x:@ ]+")


        val n = s.nextInt()
        val col = s.nextInt()
        val row = s.nextInt()
        val w = s.nextInt()
        val h = s.nextInt()

        for (i in row until row+h) {
            for (j in col until col+w) {
                a[i][j] ++
            }
        }
    }

    val res = a.sumBy { it.filter { it > 1 }.count() }
    println(res)

    for (line in lines) {

        //#18 @ 925,936: 28x28
        val s = Scanner(line).useDelimiter("[,#x:@ ]+")


        val n = s.nextInt()
        val col = s.nextInt()
        val row = s.nextInt()
        val w = s.nextInt()
        val h = s.nextInt()

        if (a.slice(row..row+h-1).all { it.sliceArray(col..col+w-1).all { it == 1 } })
        {
            println(n)
//            return
        }
    }
}