/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js.utils

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.types.isInt
import org.jetbrains.kotlin.ir.types.isNullableAny
import org.jetbrains.kotlin.ir.types.isString
import org.jetbrains.kotlin.ir.types.isSubtypeOf
import org.jetbrains.kotlin.ir.util.isTopLevelDeclaration
import org.jetbrains.kotlin.name.Name

fun TODO(element: IrElement): Nothing = TODO(element::class.java.simpleName + " is not supported yet here")

fun IrFunction.isEqualsInheritedFromAny() =
    name == Name.identifier("equals") &&
            dispatchReceiverParameter != null &&
            valueParameters.size == 1 &&
            valueParameters[0].type.isNullableAny()
// TODO
//            &&
//            returnType.isBoolean()

fun IrFunction.isToStringInheritedFromAny() =
    name == Name.identifier("toString") &&
            dispatchReceiverParameter != null &&
            valueParameters.size == 0 &&
            returnType.isString()

fun IrFunction.isHashCodeInheritedFromAny() =
    name == Name.identifier("hashCode") &&
            dispatchReceiverParameter != null &&
            valueParameters.size == 0 &&
            returnType.isInt()

fun IrFunction.isJsValueOf(irBuiltIns: IrBuiltIns) =
    name == Name.identifier("valueOf") &&
            dispatchReceiverParameter != null &&
            valueParameters.size == 0 &&
            (returnType.isString() || returnType.isSubtypeOf(irBuiltIns.numberType, irBuiltIns))

fun IrDeclaration.hasStaticDispatch() = when (this) {
    is IrSimpleFunction -> dispatchReceiverParameter == null
    is IrProperty -> isTopLevelDeclaration
    is IrField -> isStatic
    else -> true
}