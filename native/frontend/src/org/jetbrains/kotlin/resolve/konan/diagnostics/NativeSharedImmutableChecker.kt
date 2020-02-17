/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.konan.diagnostics


import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext

object NativeSharedImmutableChecker : DeclarationChecker {
    private val sharedImmutableFqName = FqName("kotlin.native.concurrent.SharedImmutable")

    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        if (descriptor !is VariableDescriptor || !descriptor.isVar) return
        val sharedImmutableAnnotation = descriptor.annotations.findAnnotation(sharedImmutableFqName)
        sharedImmutableAnnotation?.let {
            val reportLocation = DescriptorToSourceUtils.getSourceFromAnnotation(it) ?: declaration
            context.trace.report(ErrorsNative.INCOMPATIBLE_SHARED_IMMUTABLE.on(reportLocation))
        }
    }
}
