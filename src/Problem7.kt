import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/7.txt"))


    val n = 26
    val dependsOf = Array(n) { arrayListOf<Int>()}

    for (line in lines) {
        val ss = line.split(" ")
        val before = ss[1][0]
        val after = ss[7][0]

        dependsOf[after - 'A'].add(before - 'A')
    }


    //task 1: topological sort
//    val res = StringBuilder()
//    (0..25).forEach {
//        val idx = dependsOf.indexOfFirst { it.isEmpty() }
//        res.append(('A' + idx))
//
//        dependsOf.forEach { it.remove(idx) }
//        dependsOf[idx].add(-1)
//    }
//
//    println(res)

    //task 2: workers simulation
    var freeWorkers = 5
    var elapsed = 0
    var processed = 0

    val additionalSeconds = 60
    val restTimeToProcess = IntArray(26) { -1 }

    while (processed < 26) {

        while (true) {
            val nextToProcess = dependsOf.indexOfFirst { it.isEmpty() }
            if (nextToProcess < 0 || freeWorkers == 0 || restTimeToProcess[nextToProcess] >= 0)
                break

            freeWorkers--
            restTimeToProcess[nextToProcess] = additionalSeconds + nextToProcess + 1
            dependsOf[nextToProcess].add(-1)
        }

        (0..25).forEach { idx ->
            if (restTimeToProcess[idx] > 0) {
                restTimeToProcess[idx]--

                if (restTimeToProcess[idx] == 0) {
                    processed ++
                    dependsOf.forEach { it.remove(idx) }
                    freeWorkers ++
                }
            }
        }

        elapsed ++
    }

    println(elapsed)
}

