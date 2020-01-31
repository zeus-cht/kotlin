fun test() {
    x as Int
    !b
}

fun test() {
    x as Int;!b
}

fun test() {
    x as Int; !b
}

fun test() {
    x as () -> Unit?
    !b
}

fun test() {
    x as A<T>
    !b
}

fun test() {
    x as () -> Unit
    !b
}

fun test() {
    x is Int
    !b
}

fun test() {
    ::Int
    !b
}

fun test() {
    val x = {
        x as Int
        !b
    }
}