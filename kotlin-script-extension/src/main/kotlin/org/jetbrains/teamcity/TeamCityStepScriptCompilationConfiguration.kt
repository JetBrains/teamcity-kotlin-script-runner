package org.jetbrains.teamcity

import org.example.ServiceMessages
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

class TeamCityStepScriptCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        ServiceMessages::class,
        DependsOn::class,
        // Import::class,
        Repository::class,
    )
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    compilerOptions.append("-Xadd-modules=ALL-MODULE-PATH")
    baseClass(TeamCityScriptDefinition::class)
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
})