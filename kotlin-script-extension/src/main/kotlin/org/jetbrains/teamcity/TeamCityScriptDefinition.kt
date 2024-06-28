package org.jetbrains.teamcity

import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    fileExtension = "teamcity.buildstep.kts",
    compilationConfiguration = TeamCityStepScriptCompilationConfiguration::class,
)
open class TeamCityScriptDefinition(args: Any)