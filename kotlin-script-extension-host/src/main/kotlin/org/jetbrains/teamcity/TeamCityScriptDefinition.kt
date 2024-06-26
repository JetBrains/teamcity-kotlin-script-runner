package org.jetbrains.teamcity

import org.example.ServiceMessages
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    fileExtension = "teamcity.buildstep.kts",
    compilationConfiguration = TeamCityStepScriptCompilationConfiguration::class
)
abstract class TeamCityScriptDefinition