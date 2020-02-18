// FILE: annotation.kt
package kotlin.native.concurrent

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class SharedImmutable

// FILE: test.kt
import kotlin.native.concurrent.SharedImmutable
data class Point(val x: Double, val y: Double)
@SharedImmutable
val point1 = Point(1.0, 1.0)

<!INAPPLICABLE_SHARED_IMMUTABLE_VAR!>@SharedImmutable<!>
var point2 = Point(2.0, 2.0)

class Date(<!INAPPLICABLE_SHARED_IMMUTABLE_TOP_LEVEL!>@SharedImmutable<!> val month: Int, <!INAPPLICABLE_SHARED_IMMUTABLE_TOP_LEVEL, INAPPLICABLE_SHARED_IMMUTABLE_VAR!>@SharedImmutable<!> var day:Int)
class Person(val name: String) {
    <!INAPPLICABLE_SHARED_IMMUTABLE_TOP_LEVEL, INAPPLICABLE_SHARED_IMMUTABLE_VAR!>@SharedImmutable<!>
    var surname: String? = null
}

class Figure {
    @SharedImmutable
    val cornerPoint: Point
        get() = point1
}