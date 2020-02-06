// FILE: JavaClass.java

public class JavaClass {
    public int getFoo() {
        return 42;
    }
}

// FILE: test.kt

typealias KotlinClass = JavaClass

typealias Strategy = (klass: JavaClass) -> Boolean

fun foo(kc: KotlinClass) {
    kc.foo
}

fun getStrategy(): Strategy {
    return { klass ->
        klass.foo == 42
    }
}