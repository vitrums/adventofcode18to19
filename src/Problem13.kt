import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

enum class Dir(val r: Int, val c: Int) {
    NORTH (-1, 0) ,
    EAST (0, +1),
    SOUTH ( +1, 0),
    WEST (0, -1)
}

enum class Turn (val offset: Int) {
    LEFT (-1),
    STRAIGHT (0),
    RIGHT (+1);
}

inline operator fun <reified T: Enum<T>> T.plus(offset: Int) : T {
    val vv = enumValues<T>()
    var newIndex = vv.indexOf(this) + offset
    while (newIndex < 0) newIndex += vv.size
    while (newIndex >= vv.size) newIndex -= vv.size
    return vv[newIndex]
}

data class Cart(var row: Int, var col: Int, var dir: Dir) : Comparable<Cart> {
    override fun compareTo(other: Cart): Int = (row - other.row).let {
        if (it == 0) (col - other.col)
        else it
    }

    var turn: Turn = Turn.LEFT
}
fun main(args: Array<String>) {
    val lines = Files.readAllLines(Paths.get("input/13x.txt"))
    val carts = mutableListOf<Cart>()

    val a  = Array(lines.size) { row -> lines[row].toCharArray().apply {
        forEachIndexed { col, c -> this[col] = when (c) {
            '<' -> {
                carts.add(Cart(row, col, Dir.WEST))
                '-'
            }
            '>' -> {
                carts.add(Cart(row, col, Dir.EAST))
                '-'
            }
            '^' -> {
                carts.add(Cart(row, col, Dir.NORTH))
                '|'
            }
            'V' -> {
                carts.add(Cart(row, col, Dir.SOUTH))
                '|'
            }
            else -> this[col]
        }
        }
    }}



    val set = mutableSetOf<Pair<Int, Int>>()
    for (c in carts) set.add(c.row to c.col)

    while (true) {
        carts.sort()
        for (c in carts) {
            set.remove(c.row to c.col)

            c.row += c.dir.r
            c.col += c.dir.c

            if (!set.add(c.row to c.col)) {
                println("${c.row},${c.col}")
                return
            }

            when (a[c.row][c.col]) {
                '+' -> {
                    c.dir += c.turn.offset
                    c.turn += 1
                }

                '/' ->  c.dir = when(c.dir) {
                    Dir.NORTH -> Dir.EAST
                    Dir.EAST -> Dir.NORTH
                    Dir.SOUTH -> Dir.WEST
                    Dir.WEST -> Dir.SOUTH
                }

                '\\' -> c.dir = when(c.dir) {
                    Dir.NORTH -> Dir.WEST
                    Dir.WEST -> Dir.NORTH
                    Dir.EAST -> Dir.SOUTH
                    Dir.SOUTH -> Dir.EAST
                }
                else -> {}
            }
        }
    }
}