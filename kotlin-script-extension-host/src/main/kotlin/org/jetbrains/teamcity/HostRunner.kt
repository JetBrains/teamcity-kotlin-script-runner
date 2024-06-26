package org.jetbrains.teamcity

import java.io.File
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost


class HostRunner {
    fun main(args: Array<String>) {
        val scriptHost = BasicJvmScriptingHost()
        val scriptText = File(args.first()).readText(Charsets.UTF_8)
        scriptHost.evalWithTemplate<TeamCityScriptDefinition>(scriptText.toScriptSource())
    }
}