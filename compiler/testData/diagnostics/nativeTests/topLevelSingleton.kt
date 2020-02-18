// FILE: test.kt
class AtomicInt(var value: Int)
object Foo {
    <!VARIABLE_IN_TOP_LEVEL_SINGLETON_WITHOUT_THERAD_LOCAL!>var field1: Int = 10<!>
    val backer2 = AtomicInt(0)
    var field2: Int
        get() = backer2.value
        set(value: Int) { backer2.value = value }
}