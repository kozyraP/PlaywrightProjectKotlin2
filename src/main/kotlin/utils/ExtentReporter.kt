package utils

import java.io.IOException
import java.nio.file.Paths
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import com.aventstack.extentreports.ExtentReports
import com.aventstack.extentreports.ExtentTest
import com.aventstack.extentreports.Status
import com.aventstack.extentreports.reporter.ExtentSparkReporter

object ExtentReporter {

    @Throws(IOException::class)
    fun getExtentReporter(testProperties: TestProps): ExtentReports {
        val reporter = ExtentSparkReporter(testProperties.getProperty("extentReportPath"))
        reporter.loadXMLConfig("./src/main/resources/extent-report-config.xml")
        reporter.config().setCss("img.r-img { width: 30%; }")
        return ExtentReports().apply {
            attachReporter(reporter)
            val applicationURL = "<a href=\"${testProperties.getProperty("url")}\" target=\"_blank\">Open cart Demo Application</a>"
            setSystemInfo("Application", applicationURL)
            setSystemInfo("OS", System.getProperty("os.name"))
            setSystemInfo("Browser", testProperties.getProperty("browser"))

            if (java.lang.Boolean.getBoolean(testProperties.getProperty("enableRecordVideo"))) {
                val filePath = Paths.get(testProperties.getProperty("recordVideoDirectory")).toAbsolutePath().toString()
                val recordedVideoFilePath = "<a href=\"$filePath\" target=\"_blank\">Open cart Demo Application</a>"
                setSystemInfo("Execution Recorded Video", recordedVideoFilePath)
            }
        }
    }

    fun extentLog(extentTest: ExtentTest, status: Status, message: String) {
        extentTest.log(status, message)
        log(status, message)
    }

    fun extentLogWithScreenshot(extentTest: ExtentTest, status: Status, message: String, base64Path: String) {
        val imageElement = "<br/><img class='r-img' src='data:image/png;base64,$base64Path' href='data:image/png;base64,$base64Path'data-featherlight='image'>"
        extentTest.log(status, "$message$imageElement")
        log(status, message)
    }

    private fun log(status: Status, message: String) {
        val cleanedMessage = message.replace(Regex("\\<.*?\\>"), "")
        val log = LogManager.getLogger(Thread.currentThread().stackTrace[3].className.split("\\.")[1] + "." + Thread.currentThread().stackTrace[3].methodName)
        val marker: Marker = MarkerManager.getMarker("ReportLog")
        when (status) {
            Status.FAIL, Status.WARNING, Status.SKIP -> log.warn(marker, cleanedMessage)
            Status.INFO -> log.info(marker, cleanedMessage)
            else -> log.debug(marker, cleanedMessage)
        }
    }
}
