inline fun <T, R : Comparable<R>> Iterable<T>.exclusiveMinBy(selector: (T) -> R): T? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var minElem: T? = iterator.next()
    var minValue = selector(minElem!!)
    while (iterator.hasNext()) {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        } else if (minValue == v) {
            minElem = null
        }
    }
    return minElem
}

inline fun <T, R : Comparable<R>> Iterable<T>.exclusiveMaxBy(selector: (T) -> R): T? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var maxElem: T? = iterator.next()
    var maxValue = selector(maxElem!!)
    while (iterator.hasNext()) {
        val e = iterator.next()
        val v = selector(e)
        if (maxValue < v) {
            maxElem = e
            maxValue = v
        } else if (maxValue == v) {
            maxElem = null
        }
    }
    return maxElem
}