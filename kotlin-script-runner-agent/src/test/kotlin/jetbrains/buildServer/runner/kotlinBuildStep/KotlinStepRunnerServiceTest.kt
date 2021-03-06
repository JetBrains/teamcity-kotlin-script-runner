package jetbrains.buildServer.runner.kotlinBuildStep

import io.mockk.*
import io.mockk.impl.annotations.MockK
import jetbrains.buildServer.agent.*
import jetbrains.buildServer.util.FileUtil
import org.assertj.core.api.BDDAssertions.then
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.File

class KotlinStepRunnerServiceTest {
    @MockK private lateinit var runnerContext: BuildRunnerContext
    @MockK private lateinit var build: AgentRunningBuild
    @MockK private lateinit var buildLogger: BuildProgressLogger
    @MockK private lateinit var flowLogger: FlowLogger
    @MockK private lateinit var buildParameters: BuildParametersMap

    private lateinit var runnerParameters: HashMap<String, String>
    private lateinit var tempDir: File
    private lateinit var runnerService: KotlinStepRunnerService


    @BeforeMethod
    @Throws(Exception::class)
    protected fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()

        tempDir = FileUtil.createTempDirectory("kotlinstep", "tmp")
        runnerParameters = HashMap<String, String>()


        every { build.buildLogger } returns buildLogger
        every { build.agentTempDirectory } returns tempDir
        every { build.checkoutDirectory } returns tempDir
        every { buildLogger.getFlowLogger(any()) } returns flowLogger
        every { flowLogger.startFlow() } just Runs
        every { runnerContext.runnerParameters } returns runnerParameters
        every { runnerContext.buildParameters } returns buildParameters
        every { runnerContext.getToolPath(KOTLIN_COMPILER_TOOL_TYPE) } returns "path/to/kotlin"
        every { runnerContext.isVirtualContext } returns false
        every { runnerContext.workingDirectory } returns tempDir
        every { buildParameters.allParameters } returns emptyMap<String, String>()
        every { buildParameters.systemProperties } returns emptyMap<String, String>()
        every { buildParameters.environmentVariables } returns emptyMap<String, String>()

        runnerService = KotlinStepRunnerService()
    }

    @AfterMethod
    @Throws(Exception::class)
    protected fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    public fun `simple command line script`() {
        runnerParameters[RunnerParamNames.SCRIPT_TYPE] = ScriptTypes.CUSTOM
        runnerParameters[RunnerParamNames.SCRIPT_CONTENT] = "println(\"Hello!\")"
        runnerParameters[RunnerParamNames.KOTLIN_PATH] = "path/to/kotlin"
        runnerParameters[RunnerParamNames.KOTLIN_ARGS] = "one two three"
        runnerService.initialize(build, runnerContext)
        val commandLine = runnerService.makeProgramCommandLine()
        then(commandLine.executablePath).containsIgnoringCase("java")
        then(commandLine.arguments.map { if (it.contains(File.separator)) it.substring(it.lastIndexOf(File.separator) + 1) else it}.joinToString(" "))
                .startsWith("-Dkotlin.main.kts.compiled.scripts.cache.dir= -classpath kotlin-preloader.jar org.jetbrains.kotlin.preloading.Preloader -cp kotlin-compiler.jar org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -script")
                .endsWith(".main.kts -- one two three")
    }

    @Test
    public fun `simple command line file`() {
        runnerParameters[RunnerParamNames.SCRIPT_TYPE] = ScriptTypes.FILE
        runnerParameters[RunnerParamNames.SCRIPT_FILE] = "myscript.main.kts"
        runnerParameters[RunnerParamNames.KOTLIN_PATH] = "path/to/kotlin"
        runnerParameters[RunnerParamNames.KOTLIN_ARGS] = "one two three"
        runnerService.initialize(build, runnerContext)
        val commandLine = runnerService.makeProgramCommandLine()
        then(commandLine.executablePath).containsIgnoringCase("java")
        then(commandLine.arguments.map { if (it.contains(File.separator)) it.substring(it.lastIndexOf(File.separator) + 1) else it}.joinToString(" "))
                .isEqualTo("-Dkotlin.main.kts.compiled.scripts.cache.dir= -classpath kotlin-preloader.jar org.jetbrains.kotlin.preloading.Preloader -cp kotlin-compiler.jar org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -script myscript.main.kts -- one two three")
    }

    @Test
    public fun `simple command line script with dashes`() {
        runnerParameters[RunnerParamNames.SCRIPT_TYPE] = ScriptTypes.CUSTOM
        runnerParameters[RunnerParamNames.SCRIPT_CONTENT] = "println(\"Hello!\")"
        runnerParameters[RunnerParamNames.KOTLIN_PATH] = "path/to/kotlin"
        runnerParameters[RunnerParamNames.KOTLIN_ARGS] = "-- one two three"
        runnerService.initialize(build, runnerContext)
        val commandLine = runnerService.makeProgramCommandLine()
        then(commandLine.executablePath).containsIgnoringCase("java")
        then(commandLine.arguments.map { if (it.contains(File.separator)) it.substring(it.lastIndexOf(File.separator) + 1) else it}.joinToString(" "))
                .startsWith("-Dkotlin.main.kts.compiled.scripts.cache.dir= -classpath kotlin-preloader.jar org.jetbrains.kotlin.preloading.Preloader -cp kotlin-compiler.jar org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -script")
                .endsWith(".main.kts -- one two three")
    }

    @Test
    public fun `simple command line file with dashes`() {
        runnerParameters[RunnerParamNames.SCRIPT_TYPE] = ScriptTypes.FILE
        runnerParameters[RunnerParamNames.SCRIPT_FILE] = "myscript.main.kts"
        runnerParameters[RunnerParamNames.KOTLIN_PATH] = "path/to/kotlin"
        runnerParameters[RunnerParamNames.KOTLIN_ARGS] = "-- one two three"
        runnerService.initialize(build, runnerContext)
        val commandLine = runnerService.makeProgramCommandLine()
        then(commandLine.executablePath).containsIgnoringCase("java")
        then(commandLine.arguments.map { if (it.contains(File.separator)) it.substring(it.lastIndexOf(File.separator) + 1) else it}.joinToString(" "))
                .isEqualTo("-Dkotlin.main.kts.compiled.scripts.cache.dir= -classpath kotlin-preloader.jar org.jetbrains.kotlin.preloading.Preloader -cp kotlin-compiler.jar org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -script myscript.main.kts -- one two three")
    }

}