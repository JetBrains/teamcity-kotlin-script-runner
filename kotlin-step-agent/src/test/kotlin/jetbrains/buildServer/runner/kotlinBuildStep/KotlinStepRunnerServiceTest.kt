package jetbrains.buildServer.runner.kotlinBuildStep

import io.mockk.*
import io.mockk.impl.annotations.MockK
import jetbrains.buildServer.BaseTestCase
import jetbrains.buildServer.agent.*
import org.assertj.core.api.BDDAssertions.then
import org.jmock.Expectations
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.File

@Test
class KotlinStepRunnerServiceTest: BaseTestCase() {
    @MockK private lateinit var myRunner: BuildRunnerContext
    @MockK private lateinit var myBuild: AgentRunningBuild
    @MockK private lateinit var myBuildLogger: BuildProgressLogger
    @MockK private lateinit var myFlowLogger: FlowLogger
    @MockK private lateinit var myBuildParameters: BuildParametersMap

    private lateinit var myRunnerParameters: HashMap<String, String>
    private lateinit var myTempDir: File
    private lateinit var myService: KotlinStepRunnerService


    @BeforeMethod
    @Throws(Exception::class)
    override protected fun setUp() {
        super.setUp()
        MockKAnnotations.init(this)
        clearAllMocks()

        myTempDir = this.createTempDir()
        myRunnerParameters = HashMap<String, String>()


        every { myBuild.buildLogger } returns myBuildLogger
        every { myBuild.agentTempDirectory } returns myTempDir
        every { myBuild.checkoutDirectory } returns myTempDir
        every { myBuildLogger.getFlowLogger(any()) } returns myFlowLogger
        every { myFlowLogger.startFlow() } just Runs
        every { myRunner.runnerParameters } returns myRunnerParameters
        every { myRunner.buildParameters } returns myBuildParameters
        every { myRunner.getToolPath(KotlinToolProvider.TOOL_NAME) } returns "path/to/kotlin"
        every { myRunner.isVirtualContext } returns false
        every { myRunner.workingDirectory } returns myTempDir
        every { myBuildParameters.allParameters } returns emptyMap<String, String>()
        every { myBuildParameters.systemProperties } returns emptyMap<String, String>()
        every { myBuildParameters.environmentVariables } returns emptyMap<String, String>()

        myService = KotlinStepRunnerService()
    }


    public fun `simple command line script`() {
        myRunnerParameters[Constants.PARAM_SCRIPT_TYPE] = Constants.SCRIPT_TYPE_CUSTOM
        myRunnerParameters[Constants.PARAM_SCRIPT_CONTENT] = "println(\"Hello!\")"
        myService.initialize(myBuild, myRunner)
        val commandLine = myService.makeProgramCommandLine()
        then(commandLine.executablePath).containsIgnoringCase("java")
        then(commandLine.arguments.map { if (it.contains(File.separator)) it.substring(it.lastIndexOf(File.separator) + 1) else it}.joinToString(" "))
                .startsWith("-classpath kotlin-preloader.jar org.jetbrains.kotlin.preloading.Preloader -cp kotlin-compiler.jar org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -script")
                .endsWith(".main.kts")
    }

    public fun `simple command line file`() {
        myRunnerParameters[Constants.PARAM_SCRIPT_TYPE] = Constants.SCRIPT_TYPE_FILE
        myRunnerParameters[Constants.PARAM_SCRIPT_FILE] = "myscript.main.kts"
        myService.initialize(myBuild, myRunner)
        val commandLine = myService.makeProgramCommandLine()
        then(commandLine.executablePath).containsIgnoringCase("java")
        then(commandLine.arguments.map { if (it.contains(File.separator)) it.substring(it.lastIndexOf(File.separator) + 1) else it}.joinToString(" "))
                .isEqualTo("-classpath kotlin-preloader.jar org.jetbrains.kotlin.preloading.Preloader -cp kotlin-compiler.jar org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -script myscript.main.kts")
    }


}