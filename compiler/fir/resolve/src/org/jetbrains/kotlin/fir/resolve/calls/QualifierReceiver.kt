/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.calls

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.FirResolvedQualifier
import org.jetbrains.kotlin.fir.resolve.ScopeSession
import org.jetbrains.kotlin.fir.resolve.firSymbolProvider
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.scopes.FirScope
import org.jetbrains.kotlin.fir.scopes.KotlinScopeProvider
import org.jetbrains.kotlin.fir.scopes.impl.FirCompositeScope
import org.jetbrains.kotlin.fir.scopes.impl.FirQualifierScope
import org.jetbrains.kotlin.fir.scopes.impl.nestedClassifierScope
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeAliasSymbol
import org.jetbrains.kotlin.fir.types.ConeClassErrorType
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.name.ClassId
import java.util.ArrayDeque


fun FirClassLikeDeclaration<*>.fullyExpandedClass(useSiteSession: FirSession): FirRegularClass? {
    if (this is FirTypeAlias) return this.expandedConeType?.lookupTag?.toSymbol(useSiteSession)?.fir?.fullyExpandedClass(useSiteSession)
    if (this is FirRegularClass) return this
    error("Not supported: $this")
}

fun createQualifierReceiver(explicitReceiver: FirResolvedQualifier, useSiteSession: FirSession): QualifierReceiver? {
    val classId = explicitReceiver.classId ?: return null
    val classLikeSymbol = useSiteSession.firSymbolProvider.getClassLikeSymbolByFqName(classId) ?: return null
    val classSymbol = classLikeSymbol.fir.fullyExpandedClass(useSiteSession)?.symbol ?: return null
    return QualifierReceiver(explicitReceiver, classSymbol, classLikeSymbol)
}

class QualifierReceiver(
    override val explicitReceiver: FirResolvedQualifier,
    val classSymbol: FirRegularClassSymbol,
    val originalSymbol: FirClassLikeSymbol<*>
) : AbstractExplicitReceiver<FirResolvedQualifier>() {


    private fun collectSuperTypeScopesComposedByDepth(
        klass: FirClass<*>,
        useSiteSession: FirSession,
        scopeSession: ScopeSession
    ): List<FirScope> {
        val result = mutableListOf<FirScope>()
        val provider = klass.scopeProvider
        val levelScopes = mutableListOf<FirScope>()
        var currentDepth = 1
        val queue =
            ArrayDeque<Pair<ConeClassLikeType, Int>>()
        queue.addAll(
            lookupSuperTypes(klass, lookupInterfaces = true, deep = false, useSiteSession = useSiteSession).map { it to 1 }
        )
        val visitedSymbols = mutableSetOf<FirRegularClassSymbol>()
        while (queue.isNotEmpty()) {
            val (useSiteSuperType, depth) = queue.poll()
            if (depth > currentDepth) {
                currentDepth = depth
                result += FirCompositeScope(levelScopes.toMutableList())
                levelScopes.clear()
            }
            if (useSiteSuperType is ConeClassErrorType) continue
            val superTypeSymbol = useSiteSuperType.lookupTag.toSymbol(useSiteSession) as? FirRegularClassSymbol
                ?: continue
            if (!visitedSymbols.add(superTypeSymbol)) continue
            val superTypeScope = provider.getStaticMemberScopeForCallables(
                superTypeSymbol.fir, useSiteSession, scopeSession
            )
            if (superTypeScope != null) {
                levelScopes += superTypeScope
            }
            queue.addAll(
                lookupSuperTypes(
                    superTypeSymbol.fir, lookupInterfaces = true, deep = false, useSiteSession = useSiteSession
                ).map { it to currentDepth + 1 }
            )
        }
        return result
    }

    private fun getCallableScopes(
        useSiteSession: FirSession,
        scopeSession: ScopeSession
    ): List<FirScope> {
        val klass = classSymbol.fir
        val result = mutableListOf<FirScope>()
        val provider = klass.scopeProvider
        val klassScope = provider.getStaticMemberScopeForCallables(klass, useSiteSession, scopeSession)
        if (klassScope != null) {
            result += klassScope
            if (provider is KotlinScopeProvider) return result
            result += collectSuperTypeScopesComposedByDepth(klass, useSiteSession, scopeSession)
        }
        return result
    }

    fun callablesScopes(useSiteSession: FirSession, scopeSession: ScopeSession): List<FirScope> {
        return getCallableScopes(useSiteSession, scopeSession)
    }

    fun classifierScope(useSiteSession: FirSession, scopeSession: ScopeSession): FirScope? {
        val klass = classSymbol.fir
        return klass.scopeProvider.getNestedClassifierScope(klass, useSiteSession, scopeSession)
    }

}