import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/12.txt"))

    val n = 50
    val placeholder = "".padStart(n, '.')
    var state = placeholder + lines[0] + placeholder
    val rules = MutableList(lines.size - 1) {i ->
        lines[i+1].substring(0..4) to lines[i+1][9]
    }
    repeat(20) {
        val nextState = state.toCharArray().clone()
        cycle@for (i in 0..state.length-5) {
            for ((pattern, subst) in rules)
                if (state.substring(i..i + 4) == pattern) {
                    nextState[i + 2] = subst
                    continue@cycle
                }
            nextState[i+2] = '.'
        }

        state = String(nextState)
    }

    val pots = state.substring(n..(state.length - 1 - n))
    println(pots)


    println(state.foldIndexed(0) {idx, acc, ch ->
        if (ch == '.') acc else acc+idx-n}
    )
}

