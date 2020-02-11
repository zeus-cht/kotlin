/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.DynamicObjectAware
import java.io.File
import java.util.*

interface PropertiesProvider {
    val rootProjectDir: File
    fun getProperty(key: String): Any?
}

/**
 * A class defining strongly-typed build properties.
 *
 * Place in this class only these properties you'd like to share between
 * settings.gradle[.kts], buildSrc, and plain .gradle[.kts] project files.
 *
 * The properties that are used in projects only can be defined
 * in buildSrc/src/main/kotlin/BuildProperties.kt.
 */
open class KotlinSharedBuildProperties(
    private val propertiesProvider: PropertiesProvider
) {
    private val localProperties: Properties = Properties()

    init {
        val localPropertiesFile = propertiesProvider.rootProjectDir.resolve("local.properties")
        if (localPropertiesFile.isFile) {
            localPropertiesFile.reader().use(localProperties::load)
        }
    }

    operator fun get(key: String): Any? = localProperties.getProperty(key) ?: propertiesProvider.getProperty(key)

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        this[key]?.toString()?.trim()?.toBoolean() ?: default

    val isJpsBuildEnabled: Boolean = getBoolean("jpsBuild")

    val isInIdeaSync: Boolean = run {
        // "idea.sync.active" was introduced in 2019.1
        System.getProperty("idea.sync.active")?.toBoolean() == true || let {
            // before 2019.1 there is "idea.active" that was true only on sync,
            // but since 2019.1 "idea.active" present in task execution too.
            // So let's check Idea version
            val majorIdeaVersion = System.getProperty("idea.version")
                ?.split(".")
                ?.getOrNull(0)
            val isBeforeIdea2019 = majorIdeaVersion == null || majorIdeaVersion.toInt() < 2019

            isBeforeIdea2019 && System.getProperty("idea.active")?.toBoolean() == true
        }
    }

    val isInJpsBuildIdeaSync: Boolean
        get() = isJpsBuildEnabled && isInIdeaSync

//    val includeJava9: Boolean
//        get() = !isInJpsBuildIdeaSync && getBoolean("kotlin.build.java9", true)
//
//    val useBootstrapStdlib: Boolean
//        get() = isInJpsBuildIdeaSync || getBoolean("kotlin.build.useBootstrapStdlib", false)

    private val kotlinUltimateExists: Boolean = propertiesProvider.rootProjectDir.resolve("kotlin-ultimate").exists()

    val isTeamcityBuild: Boolean = getBoolean("teamcity") || System.getenv("TEAMCITY_VERSION") != null

    val intellijUltimateEnabled: Boolean
        get() {
            val explicitlyEnabled = getBoolean("intellijUltimateEnabled")
            if (!kotlinUltimateExists && explicitlyEnabled) {
                error("intellijUltimateEnabled property is set, while kotlin-ultimate repository is not provided")
            }
            return kotlinUltimateExists && (explicitlyEnabled || isTeamcityBuild)
        }

    val includeCidrPlugins: Boolean = kotlinUltimateExists && getBoolean("cidrPluginsEnabled")

    val includeUltimate: Boolean = kotlinUltimateExists && (isTeamcityBuild || intellijUltimateEnabled)

//    val postProcessing: Boolean get() = isTeamcityBuild || getBoolean("kotlin.build.postprocessing", true)
//
//    val relocation: Boolean get() = postProcessing
//
//    val proguard: Boolean get() = postProcessing && getBoolean("kotlin.build.proguard", isTeamcityBuild)
//
//    val jarCompression: Boolean get() = getBoolean("kotlin.build.jar.compression", isTeamcityBuild)
//
    internal val buildCacheUrl: String? = get("kotlin.build.cache.url") as String?

    internal val pushToBuildCache: Boolean = getBoolean("kotlin.build.cache.push", isTeamcityBuild)

    internal val localBuildCacheEnabled: Boolean = getBoolean("kotlin.build.cache.local.enabled", !isTeamcityBuild)

    val buildScanServer: String? = get("kotlin.build.scan.url") as String?

    internal val buildCacheUser: String? = get("kotlin.build.cache.user") as String?

    internal val buildCachePassword: String? = get("kotlin.build.cache.password") as String?
}

private const val extensionName = "kotlinSharedBuildProperties"

class ProjectProperties(val project: Project) : PropertiesProvider {
    override val rootProjectDir: File
        get() = project.rootProject.projectDir.let { if (it.name == "buildSrc") it.parentFile else it }

    override fun getProperty(key: String): Any? = project.findProperty(key)
}

val Project.kotlinSharedBuildProperties: KotlinSharedBuildProperties
    get() = rootProject.extensions.findByName(extensionName) as KotlinSharedBuildProperties?
        ?: KotlinSharedBuildProperties(ProjectProperties(rootProject)).also {
            rootProject.extensions.add(extensionName, it)
        }

class SettingsProperties(val settings: Settings) : PropertiesProvider {
    override val rootProjectDir: File
        get() = settings.rootDir.let { if (it.name == "buildSrc") it.parentFile else it }

    override fun getProperty(key: String): Any? {
        val obj = (settings as DynamicObjectAware).asDynamicObject
        return if (obj.hasProperty(key)) obj.getProperty(key) else null
    }
}

fun getKotlinBuildPropertiesForSettings(settings: Any) = (settings as Settings).kotlinSharedBuildProperties

val Settings.kotlinSharedBuildProperties: KotlinSharedBuildProperties
    get() = extensions.findByName(extensionName) as KotlinSharedBuildProperties?
        ?: KotlinSharedBuildProperties(SettingsProperties(this)).also {
            extensions.add(extensionName, it)
        }