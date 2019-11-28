/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin

public actual open class Error : Throwable {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
    actual constructor(cause: Throwable?) : super(cause)
}

public actual open class Exception : Throwable {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
    actual constructor(cause: Throwable?) : super(cause)
}

public actual open class RuntimeException : Exception {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
    actual constructor(cause: Throwable?) : super(cause)
}

public actual open class IllegalArgumentException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
    actual constructor(cause: Throwable?) : super(cause)
}

public actual open class IllegalStateException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
    actual constructor(cause: Throwable?) : super(cause)
}

public actual open class IndexOutOfBoundsException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
}

public actual open class ConcurrentModificationException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
    actual constructor(cause: Throwable?) : super(cause)
}

public actual open class UnsupportedOperationException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
    actual constructor(cause: Throwable?) : super(cause)
}


public actual open class NumberFormatException : IllegalArgumentException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
}


public actual open class NullPointerException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
}

public actual open class ClassCastException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
}

public actual open class AssertionError : Error {
    actual constructor() : super()
    actual constructor(message: Any?) : this(message?.toString(), message as? Throwable)
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
}

public actual open class NoSuchElementException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
}

@SinceKotlin("1.3")
public actual open class ArithmeticException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
}

public actual open class NoWhenBranchMatchedException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
    actual constructor(cause: Throwable?) : super(cause)
}

public actual open class UninitializedPropertyAccessException : RuntimeException {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
    actual constructor(message: String?, cause: Throwable?) : super(message, cause)
    actual constructor(cause: Throwable?) : super(cause)
}
