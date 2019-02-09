import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


//data class Day(val mon: Int, val day: Int) {
//    companion object {
//        fun of(mon: Int, day: Int, hour: Int) : Day {
//            if (hour == 23) return Day(mon, day).next()
//            else return Day(mon, day)
//        }
//    }
//}

data class Guard(val number : Int) {
    val timetable = mutableListOf<Array<Boolean>>()
//    val timetable = Array(60) { false }
}

fun main(args: Array<String>) {

//    val a = Array(1000) { Array (1000) { 0 }}
    val lines = Files.readAllLines(Paths.get("input/4.txt"))
    lines.sort()


    val guards = mutableMapOf<Int, Guard>()

    lateinit var guard: Guard
    for (line in lines) {
        println(line)

        val s = Scanner(line).useDelimiter("[- :\\[\\]]+")
        val y = s.nextInt()
        val m = s.nextInt()
        val d = s.nextInt()
        val h = s.nextInt()
        val min = s.nextInt()

        val l = s.nextLine().trim()
//        println(l)

        when {
            l.contains("Guard") -> {
                val n = l.split(" ")[2].substring(1).toInt()
                guard = guards[n] ?: Guard(n).apply { guards[n] = this }
                guard.timetable.add(Array(60) { true })

            }
            l.contains("falls") -> {
                val last = guard.timetable.last()
                last.fill(false, min)

            }
            l.contains("wakes") -> {
                val last = guard.timetable.last()
                last.fill(true, min)

            }
            else -> {
                error("Fuck")
            }
        }
    }

//    guard = guards.values.maxBy { it.timetable.sumBy { a -> a.count { onDuty -> !onDuty } } } ?: error("Fuck")
//
//    println(guard)
//
//    val maxSleepMin = (0..59).maxBy { minute -> guard.timetable.count { a -> !a[minute] } }?: error("Fuck")
//
//    println(maxSleepMin)
//
//    println(maxSleepMin * guard.number)

    val pairs = guards.values.flatMap { guard -> (0..59).map { guard to it} }

    val res = pairs.maxBy { (guard, minute) -> guard.timetable.count { a -> !a[minute] } } ?: error("Fuck")
    println(res)
    println(res.first.number * res.second)
}