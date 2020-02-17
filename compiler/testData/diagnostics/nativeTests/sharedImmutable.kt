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

<!INCOMPATIBLE_SHARED_IMMUTABLE!>@SharedImmutable<!>
var point2 = Point(2.0, 2.0)

class Date(@SharedImmutable val month: Int, <!INCOMPATIBLE_SHARED_IMMUTABLE!>@SharedImmutable<!> var day:Int)
class Person(val name: String) {
    <!INCOMPATIBLE_SHARED_IMMUTABLE!>@SharedImmutable<!>
    var surname: String? = null
}