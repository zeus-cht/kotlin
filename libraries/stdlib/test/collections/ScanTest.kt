/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package test.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class ScanTest {
    @Test
    fun scan() {
        for (size in 0 until 4) {
            val expected = listOf("", "0", "01", "012", "0123").subList(0, size + 1)

            // Iterable
            assertEquals(expected, List(size) { it }.scan("") { acc, e -> acc + e })
            // Sequence
            assertEquals(expected, List(size) { it }.asSequence().scan("") { acc, e -> acc + e }.toList())
            // CharSequence
            assertEquals(expected, List(size) { it }.joinToString(separator = "").scan("") { acc, e -> acc + e })
            // Array<T>
            assertEquals(expected, Array(size) { it }.scan("") { acc, e -> acc + e })

            // Primitive Arrays
            assertEquals(expected, ByteArray(size) { it.toByte() }.scan("") { acc, e -> acc + e })
            assertEquals(expected, CharArray(size) { '0' + it }.scan("") { acc, e -> acc + e })
            assertEquals(expected, ShortArray(size) { it.toShort() }.scan("") { acc, e -> acc + e })
            assertEquals(expected, IntArray(size) { it }.scan("") { acc, e -> acc + e })
            assertEquals(expected, LongArray(size) { it.toLong() }.scan("") { acc, e -> acc + e })
            assertEquals(expected, FloatArray(size) { it.toFloat() }.scan("") { acc, e -> acc + e.toInt() })
            assertEquals(expected, DoubleArray(size) { it.toDouble() }.scan("") { acc, e -> acc + e.toInt() })

            // Unsigned Arrays
            assertEquals(expected, UByteArray(size) { it.toUByte() }.scan("") { acc, e -> acc + e })
            assertEquals(expected, UShortArray(size) { it.toUShort() }.scan("") { acc, e -> acc + e })
            assertEquals(expected, UIntArray(size) { it.toUInt() }.scan("") { acc, e -> acc + e })
            assertEquals(expected, ULongArray(size) { it.toULong() }.scan("") { acc, e -> acc + e })
        }
    }

    @Test
    fun scanIndexed() {
        for (size in 0 until 4) {
            val expected = listOf("+", "+[0: a]", "+[0: a][1: b]", "+[0: a][1: b][2: c]", "+[0: a][1: b][2: c][3: d]").subList(0, size + 1)

            // Iterable
            assertEquals(
                expected,
                List(size) { 'a' + it }.scanIndexed("+") { index, acc, e -> "$acc[$index: $e]" }
            )
            // Sequence
            assertEquals(
                expected,
                List(size) { 'a' + it }.asSequence().scanIndexed("+") { index, acc, e -> "$acc[$index: $e]" }.toList()
            )
            // CharSequence
            assertEquals(
                expected,
                List(size) { 'a' + it }.joinToString(separator = "").scanIndexed("+") { index, acc, e -> "$acc[$index: $e]" }
            )
            // Array<T>
            assertEquals(
                expected,
                Array(size) { 'a' + it }.scanIndexed("+") { index, acc, e -> "$acc[$index: $e]" }
            )


            // Primitive Arrays
            assertEquals(
                expected,
                ByteArray(size) { it.toByte() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )
            assertEquals(
                expected,
                CharArray(size) { it.toChar() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )
            assertEquals(
                expected,
                ShortArray(size) { it.toShort() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )
            assertEquals(
                expected,
                IntArray(size) { it }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e}]" }
            )
            assertEquals(
                expected,
                LongArray(size) { it.toLong() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )
            assertEquals(
                expected,
                FloatArray(size) { it.toFloat() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )
            assertEquals(
                expected,
                DoubleArray(size) { it.toDouble() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )

            // Unsigned Arrays
            assertEquals(
                expected,
                UByteArray(size) { it.toUByte() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )
            assertEquals(
                expected,
                UShortArray(size) { it.toUShort() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )
            assertEquals(
                expected,
                UIntArray(size) { it.toUInt() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )
            assertEquals(
                expected,
                ULongArray(size) { it.toULong() }.scanIndexed("+") { index, acc, e -> "$acc[$index: ${'a' + e.toInt()}]" }
            )
        }
    }

    @Test
    fun scanReduce() {
        for (size in 0 until 4) {
            val expected = listOf(0, 1, 3, 6).subList(0, size)

            // Iterable
            assertEquals(
                expected,
                List(size) { it }.scanReduce { acc, e -> acc + e }
            )
            // Sequence
            assertEquals(
                expected,
                List(size) { it }.asSequence().scanReduce { acc, e -> acc + e }.toList()
            )
            // CharSequence
            assertEquals(
                expected.map { it.toChar() },
                CharArray(size) { it.toChar() }.concatToString().scanReduce { acc, e -> acc + e.toInt() }
            )
            // Array<T>
            assertEquals(
                expected,
                Array(size) { it }.scanReduce { acc, e -> acc + e }
            )

            // Primitive Arrays
            assertEquals(
                expected.map { it.toByte() },
                ByteArray(size) { it.toByte() }.scanReduce { acc, e -> (acc + e).toByte() }
            )
            assertEquals(
                expected.map { it.toChar() },
                CharArray(size) { it.toChar() }.scanReduce { acc, e -> acc + e.toInt() }
            )
            assertEquals(
                expected.map { it.toShort() },
                ShortArray(size) { it.toShort() }.scanReduce { acc, e -> (acc + e).toShort() }
            )
            assertEquals(
                expected,
                IntArray(size) { it }.scanReduce { acc, e -> acc + e }
            )
            assertEquals(
                expected.map { it.toLong() },
                LongArray(size) { it.toLong() }.scanReduce { acc, e -> acc + e }
            )
            assertEquals(
                expected.map { it.toFloat() },
                FloatArray(size) { it.toFloat() }.scanReduce { acc, e -> acc + e.toInt() }
            )
            assertEquals(
                expected.map { it.toDouble() },
                DoubleArray(size) { it.toDouble() }.scanReduce { acc, e -> acc + e.toInt() }
            )

            // Unsigned Arrays
            assertEquals(
                expected.map { it.toUByte() },
                UByteArray(size) { it.toUByte() }.scanReduce { acc, e -> (acc + e).toUByte() }
            )
            assertEquals(
                expected.map { it.toUShort() },
                UShortArray(size) { it.toUShort() }.scanReduce { acc, e -> (acc + e).toUShort() }
            )
            assertEquals(
                expected.map { it.toUInt() },
                UIntArray(size) { it.toUInt() }.scanReduce { acc, e -> acc + e }
            )
            assertEquals(
                expected.map { it.toULong() },
                ULongArray(size) { it.toULong() }.scanReduce { acc, e -> acc + e }
            )
        }
    }

    @Test
    fun scanReduceIndexed() {
        for (size in 0 until 4) {
            val expected = listOf(0, 1, 6, 27).subList(0, size)

            // Iterable
            assertEquals(
                expected,
                List(size) { it }.scanReduceIndexed { index, acc, e -> index * (acc + e) }
            )
            // Sequence
            assertEquals(
                expected,
                List(size) { it }.asSequence().scanReduceIndexed { index, acc, e -> index * (acc + e) }.toList()
            )
            // CharSequence
            assertEquals(
                expected.map { it.toChar() },
                CharArray(size) { it.toChar() }.concatToString().scanReduceIndexed { index, acc, e -> (index * (acc.toInt() + e.toInt())).toChar() }
            )
            // Array<T>
            assertEquals(
                expected,
                Array(size) { it }.scanReduceIndexed { index, acc, e -> index * (acc + e) }
            )

            // Primitive Arrays
            assertEquals(
                expected.map { it.toByte() },
                ByteArray(size) { it.toByte() }.scanReduceIndexed { index, acc, e -> (index * (acc + e)).toByte() })
            assertEquals(
                expected.map { it.toChar() },
                CharArray(size) { it.toChar() }.scanReduceIndexed { index, acc, e -> (index * (acc.toInt() + e.toInt())).toChar() }
            )
            assertEquals(
                expected.map { it.toShort() },
                ShortArray(size) { it.toShort() }.scanReduceIndexed { index, acc, e -> (index * (acc + e)).toShort() }
            )
            assertEquals(
                expected,
                IntArray(size) { it }.scanReduceIndexed { index, acc, e -> index * (acc + e) }
            )
            assertEquals(
                expected.map { it.toLong() },
                LongArray(size) { it.toLong() }.scanReduceIndexed { index, acc, e -> index * (acc + e) }
            )
            assertEquals(
                expected.map { it.toFloat() },
                FloatArray(size) { it.toFloat() }.scanReduceIndexed { index, acc, e -> index * (acc + e) }
            )
            assertEquals(
                expected.map { it.toDouble() },
                DoubleArray(size) { it.toDouble() }.scanReduceIndexed { index, acc, e -> index * (acc + e) }
            )

            // Unsigned Arrays
            assertEquals(
                expected.map { it.toUByte() },
                UByteArray(size) { it.toUByte() }.scanReduceIndexed { index, acc, e -> (index.toUInt() * (acc + e)).toUByte() }
            )
            assertEquals(
                expected.map { it.toUShort() },
                UShortArray(size) { it.toUShort() }.scanReduceIndexed { index, acc, e -> (index.toUInt() * (acc + e)).toUShort() }
            )
            assertEquals(
                expected.map { it.toUInt() },
                UIntArray(size) { it.toUInt() }.scanReduceIndexed { index, acc, e -> index.toUInt() * (acc + e) }
            )
            assertEquals(
                expected.map { it.toULong() },
                ULongArray(size) { it.toULong() }.scanReduceIndexed { index, acc, e -> index.toULong() * (acc + e) }
            )
        }
    }
}

