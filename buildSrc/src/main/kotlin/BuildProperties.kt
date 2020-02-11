/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.DynamicObjectAware
import org.jetbrains.kotlin.build.shared.*
import java.io.File


class KotlinBuildProperties(propertiesProvider: PropertiesProvider) : KotlinSharedBuildProperties(propertiesProvider) {
    val includeJava9: Boolean
        get() = !isInJpsBuildIdeaSync && getBoolean("kotlin.build.java9", true)

    val useBootstrapStdlib: Boolean
        get() = isInJpsBuildIdeaSync || getBoolean("kotlin.build.useBootstrapStdlib", false)

    val postProcessing: Boolean get() = isTeamcityBuild || getBoolean("kotlin.build.postprocessing", true)

    val relocation: Boolean get() = postProcessing

    val proguard: Boolean get() = postProcessing && getBoolean("kotlin.build.proguard", isTeamcityBuild)

    val jarCompression: Boolean get() = getBoolean("kotlin.build.jar.compression", isTeamcityBuild)

}

private const val extensionName = "kotlinBuildProperties"

val Project.kotlinBuildProperties: KotlinBuildProperties
    get() = rootProject.extensions.findByName(extensionName) as KotlinBuildProperties?
        ?: KotlinBuildProperties(ProjectProperties(rootProject)).also {
            rootProject.extensions.add(extensionName, it)
        }


