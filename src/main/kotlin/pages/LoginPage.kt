package pages

import base.PlaywrightFactory.Companion.takeScreenshot
import utils.ExtentReporter.extentLog
import utils.ExtentReporter.extentLogWithScreenshot
import com.aventstack.extentreports.ExtentTest
import com.aventstack.extentreports.Status
import com.microsoft.playwright.Page
import java.util.Base64

class LoginPage(private val page: Page, private val extentTest: ExtentTest) {
    companion object {
        private const val ALERT_ERROR_SELECTOR = "div.alert"
        private const val EMAIL_ID = "//input[@id='input-email']"
        private const val LOGIN_BTN = "//input[@value='Login']"
        private const val LOGOUT_LINK = "//a[@class='list-group-item'][normalize-space()='Logout']"
        private const val PASSWORD = "//input[@id='input-password']"
    }

    fun doLogin(appUserName: String, appPassword: String): Boolean {
        extentLog(extentTest, Status.INFO, "Login to Application using username $appUserName")
        page.fill(EMAIL_ID, appUserName)
        page.fill(PASSWORD, String(Base64.getDecoder().decode(appPassword)))
        page.click(LOGIN_BTN)
        if (page.locator(LOGOUT_LINK).isVisible()) {
            extentLog(extentTest, Status.PASS, "User login to the Application successful.")
            return true
        }
        val isErrorDisplayed = page.textContent(ALERT_ERROR_SELECTOR)
            .contains("Warning: No match for E-Mail Address and/or Password.")
        extentLogWithScreenshot(extentTest, Status.FAIL, "User login to the Application is unsuccessful.",
            takeScreenshot(page))
        return !isErrorDisplayed
    }

    fun getLoginPageTitle(): String {
        page.waitForLoadState()
        return page.title()
    }
}
