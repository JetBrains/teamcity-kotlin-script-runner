package org.jetbrains.teamcity

import org.example.ServiceMessages
import org.jetbrains.kotlin.mainKts.CompilerOptions
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import org.jetbrains.kotlin.mainKts.Import
import org.jetbrains.kotlin.mainKts.MainKtsConfigurator

class TeamCityStepScriptCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        ServiceMessages::class,
        DependsOn::class,
        Import::class,
        CompilerOptions::class,
        Repository::class,
    )
    refineConfiguration {
        onAnnotations(DependsOn::class, Repository::class, Import::class, CompilerOptions::class, handler = MainKtsConfigurator())
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    compilerOptions.append("-Xadd-modules=ALL-MODULE-PATH")
    baseClass(TeamCityScriptDefinition::class)
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
})