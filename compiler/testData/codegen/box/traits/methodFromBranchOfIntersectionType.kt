interface K

interface I : K {
    fun ff(): String
}

interface J : K {}

class A: I, J {
    override fun ff() = "OK"
}

class B: I, J {
    override fun ff() = "Not OK"
}

val b: Boolean = true

fun box(): String {
    val v = if (b) A() else B()
    return v.ff()
}
