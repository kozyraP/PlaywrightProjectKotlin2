package base

import java.nio.file.Paths
import java.util.Base64
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import com.microsoft.playwright.*
import utils.TestProps

class PlaywrightFactory(private val testProperties: TestProps) {
    private val playwright: Playwright = Playwright.create()
    private val log: Logger = LogManager.getLogger()

    private fun getBrowser(): Browser {
        val browserName = testProperties.getProperty("browser")
        val headless = testProperties.getProperty("headless").toBoolean()
        val launchOptions = BrowserType.LaunchOptions().setHeadless(headless)
        val browserType = when (browserName?.lowercase()) {
            "chromium" -> playwright.chromium()
            "firefox" -> playwright.firefox()
            "safari" -> playwright.webkit()
            else -> {
                val message = "Browser Name '$browserName' specified is Invalid. Please specify one of the supported browsers [chromium, firefox, safari, chrome, edge]."
                log.debug(message)
                throw IllegalArgumentException(message)
            }
        }
        log.info("Browser Selected for Test Execution '$browserName' with headless mode as '$headless'")
        return browserType.launch(launchOptions)
    }

    private fun getBrowserContext(): BrowserContext {
        val browser = getBrowser()
        val newContextOptions = Browser.NewContextOptions()

        if (testProperties.getProperty("enableRecordVideo").toBoolean()) {
            val path = Paths.get(testProperties.getProperty("recordVideoDirectory"))
            newContextOptions.setRecordVideoDir(path)
            log.info("Browser Context - Video Recording is enabled at location '${path.toAbsolutePath()}'")
        }

        val viewPortHeight = testProperties.getProperty("viewPortHeight")?.toInt()
        val viewPortWidth = testProperties.getProperty("viewPortWidth")?.toInt()
        if (viewPortWidth != null && viewPortHeight != null) {
            newContextOptions.setViewportSize(viewPortWidth, viewPortHeight)
        }
        log.info("Browser Context - Viewport Width '$viewPortWidth' and Height '$viewPortHeight'")

        if (testProperties.getProperty("useSessionState").toBoolean()) {
            val path = Paths.get(testProperties.getProperty("sessionState"))
            newContextOptions.setStorageStatePath(path)
            log.info("Browser Context - Used the Session Storage State at location '${path.toAbsolutePath()}'")
        }

        val browserContext = browser.newContext(newContextOptions)

        if (testProperties.getProperty("enableTracing").toBoolean()) {
            browserContext.tracing().start(Tracing.StartOptions().setScreenshots(true).setSnapshots(true))
            log.info("Browser Context - Tracing is enabled with Screenshots and Snapshots")
        }
        return browserContext
    }

    fun createPage(): Page? {
        var page: Page? = null
        try {
            page = getBrowserContext().newPage()
        } catch (e: Exception) {
            log.error("Unable to create Page: ", e)
        }
        return page
    }

    companion object {
        fun takeScreenshot(page: Page): String {
            val path = System.getProperty("user.dir") + "/test-results/screenshots/" + System.currentTimeMillis() + ".png"
            val buffer = page.screenshot(Page.ScreenshotOptions().setPath(Paths.get(path)).setFullPage(true))
            val base64Path = Base64.getEncoder().encodeToString(buffer)
            LogManager.getLogger().debug("Screenshot is taken and saved at location $path")
            return base64Path
        }
    }
}
