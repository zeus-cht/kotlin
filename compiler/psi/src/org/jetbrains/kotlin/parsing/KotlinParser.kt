/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.config.LANGUAGE_VERSION_SETTINGS_KEY2
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.diagnostics.ParserErrors
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import kotlin.reflect.KFunction1

class KotlinParser : PsiParser {
    override fun parse(iElementType: IElementType, psiBuilder: PsiBuilder) = throw IllegalStateException("use another parse")

    // we need this method because we need psiFile
    fun parse(iElementType: IElementType?, psiBuilder: PsiBuilder, chameleon: ASTNode, psiFile: PsiFile): ASTNode {
        val ktParsing = KotlinParsing.createForTopLevel(SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder))
        val extension = FileUtilRt.getExtension(psiFile.name)

        if (extension.isEmpty() || extension == KotlinFileType.EXTENSION || psiFile is KtFile && psiFile.isCompiled) {
            ktParsing.parseFile()
        } else {
            ktParsing.parseScript()
        }

        val s = psiBuilder.treeBuilt

        return s.also { tree -> collectErrors(ktParsing, tree, chameleon) }
    }

    companion object {
        val errorsSetKey = Key<ParserErrors>("errors")
        val errorsSetKey2 = Key<Pair<ParserErrors, Int>>("errors2")

        private fun collectErrors(kotlinParsing: KotlinParsing, tree: ASTNode, chameleon: ASTNode) {
//            KotlinParsing.errors.forEach { (elementType, position), value: ParserErrors ->
//                KotlinParsing.errors[elementType to offset + position] = value
//            }
//            tree.putUserData(errorsSetKey, ParserErrors.EXCLAMATION_MARK_AFTER_TYPE)

            kotlinParsing.errors.run {
                forEach { (elType, position), value: ParserErrors ->
                    val psiElement = tree.findLeafElementAt(position)?.psi ?: return@forEach
                    val x = chameleon
//                    x.addChild(psiElement.node)
//                    val gg = SmartPointerManager.getInstance(psiElement.project).createSmartPsiElementPointer(x.psi.lastChild, chameleon.psi.containingFile)
//                    x.removeChild(psiElement.node)

//                    x.psi.containingFile.putCopyableUserData(errorsSetKey2, value to chameleon.startOffset + position)

                    // SmartPointerManager.getInstance(psiElement.project).createSmartPsiElementPointer(psiElement, chameleon.psi.containingFile)
                    // chameleon.psi.children[1].children[0].firstChild
                    // .children[0].children[1].children[1].children[0].firstChild
                    // element.containingFile.children[2].children[1].children[0].children[1].children[1].children[0].firstChild

                    psiElement.putUserData(errorsSetKey, value)
                }
                clear()
            }
        }

        private fun parseFragment(psiBuilder: PsiBuilder, chameleon: ASTNode, parse: KFunction1<KotlinParsing, Unit>): ASTNode {
            val lvs: LanguageVersionSettings? = psiBuilder.project.getUserData(LANGUAGE_VERSION_SETTINGS_KEY2)

            return KotlinParsing.createForTopLevel(SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder)).let { kotlinParsing ->
                parse(kotlinParsing)
                val f = psiBuilder.treeBuilt
                f.also { tree -> collectErrors(kotlinParsing, tree, chameleon) }
            }
        }

        @JvmStatic
        fun parseTypeCodeFragment(psiBuilder: PsiBuilder, chameleon: ASTNode) = parseFragment(
            psiBuilder,
            chameleon,
            KotlinParsing::parseTypeCodeFragment
        )

        @JvmStatic
        fun parseExpressionCodeFragment(psiBuilder: PsiBuilder, chameleon: ASTNode) = parseFragment(
            psiBuilder,
            chameleon,
            KotlinParsing::parseExpressionCodeFragment
        )

        @JvmStatic
        fun parseBlockCodeFragment(psiBuilder: PsiBuilder, chameleon: ASTNode) = parseFragment(
            psiBuilder,
            chameleon,
            KotlinParsing::parseBlockCodeFragment
        )

        @JvmStatic
        fun parseLambdaExpression(psiBuilder: PsiBuilder, chameleon: ASTNode) = parseFragment(
            psiBuilder,
            chameleon,
            KotlinParsing::parseLambdaExpression
        )

        @JvmStatic
        fun parseBlockExpression(psiBuilder: PsiBuilder, chameleon: ASTNode) = parseFragment(
            psiBuilder,
            chameleon,
            KotlinParsing::parseBlockExpression
        )
    }
}