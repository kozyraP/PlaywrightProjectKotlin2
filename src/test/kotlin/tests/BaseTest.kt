package tests

import base.PlaywrightFactory
import pages.HomePage
import pages.LoginPage
import utils.ExtentReporter
import utils.TestProps
import org.apache.logging.log4j.LogManager
import org.testng.ITestResult
import org.testng.annotations.AfterMethod
import org.testng.annotations.AfterSuite
import org.testng.annotations.BeforeMethod
import org.testng.annotations.BeforeSuite
import org.testng.asserts.SoftAssert
import com.aventstack.extentreports.ExtentReports
import com.aventstack.extentreports.ExtentTest
import com.aventstack.extentreports.Status
import com.microsoft.playwright.Page
import java.io.File
import java.nio.file.Paths

open class BaseTest {
    protected lateinit var page: Page
    protected val softAssert = SoftAssert()
    protected lateinit var extentTest: ExtentTest
    protected lateinit var testNode: ExtentTest
    protected lateinit var homePage: HomePage
    protected lateinit var loginPage: LoginPage

    companion object {
        const val HOME_PAGE_TITLE = "Your Store"
        const val LOGIN_PAGE_TITLE = "Account Login"
        lateinit var reporter: ExtentReports
        lateinit var testProperties: TestProps
        lateinit var log: org.apache.logging.log4j.Logger
    }

    @BeforeSuite
    @Throws(Exception::class)
    fun setupBeforeTestSuite() {
        val file = File("test-results")
        if (!file.exists()) file.mkdirs()
        log = LogManager.getLogger()
        testProperties = TestProps()
        testProperties.updateTestProperties()
        reporter = ExtentReporter.getExtentReporter(testProperties)
    }

    @AfterSuite
    fun teardownAfterTestSuite() {
        try {
            softAssert.assertAll()
            reporter.flush()
        } catch (e: Exception) {
            log.error("Error in AfterSuite Method ", e)
        }
    }

    @BeforeMethod
    fun startPlaywrightServer() {
        val pf = PlaywrightFactory(testProperties)
        page = pf.createPage()!!
        page.navigate(testProperties.getProperty("url"))
    }

    @AfterMethod
    fun closePage(result: ITestResult) {
        val testName = testNode.model.name.replace("[^A-Za-z0-9_\\-\\.\\s]".toRegex(), "")
        if (!result.isSuccess) {
            ExtentReporter.extentLogWithScreenshot(testNode, Status.WARNING, "The test is not Passed. Please refer the previous step.",
                PlaywrightFactory.takeScreenshot(page))
        }
        page.context().browser().close()
        reporter.flush()
    }
}
