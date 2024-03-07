package pages

import base.PlaywrightFactory.Companion.takeScreenshot
import utils.ExtentReporter.extentLog
import utils.ExtentReporter.extentLogWithScreenshot
import com.aventstack.extentreports.ExtentTest
import com.aventstack.extentreports.Status
import com.microsoft.playwright.Page
import com.microsoft.playwright.Locator

class HomePage(private val page: Page, private val extentTest: ExtentTest) {
    companion object {
        private const val ADD_TO_CART = "text='Add to Cart'"
        private const val ALERT = "div.alert"
        private const val LOGIN_LINK = "a:text('Login')"
        private const val MY_ACCOUNT_LINK = "a[title='My Account']"
        private const val PRODUCT_CAPTION = ".caption h4 a"
        private const val PRODUCT_SEARCH_RESULT = "div.product-thumb"
        private const val SEARCH = "input[name='search']"
        private const val SEARCH_ICON = "div#search button"
        private const val SEARCH_PAGE_HEADER = "div#content h1"
    }

    fun getHomePageTitle(): String {
        page.waitForLoadState()
        return page.title()
    }

    fun searchProduct(productName: String): Boolean {
        page.fill(SEARCH, productName)
        page.click(SEARCH_ICON)
        val header = page.textContent(SEARCH_PAGE_HEADER)
        extentLog(extentTest, Status.PASS, "Search of '$header' Product is successful")
        if (page.locator(PRODUCT_SEARCH_RESULT).count() > 0) {
            extentLog(extentTest, Status.PASS, "Search of '$productName' Product is successful")
            return true
        }
        extentLogWithScreenshot(extentTest, Status.INFO, "No Product is available for the search '$productName'",
            takeScreenshot(page))
        return false
    }

    fun addProductToCart(): String? {
        val productLocator: Locator = page.locator(PRODUCT_SEARCH_RESULT).nth(0)
        productLocator.locator(ADD_TO_CART).click()
        val product = productLocator.locator(PRODUCT_CAPTION).textContent()
        if (page.textContent(ALERT).contains("You have added $product to your shopping cart!")) {
            extentLogWithScreenshot(extentTest, Status.PASS, "The '$product' product is added to the cart.",
                takeScreenshot(page))
            return product
        }
        extentLog(extentTest, Status.FAIL, "Unable to add the product to the cart")
        return null
    }

    fun navigateToLoginPage(): LoginPage {
        page.click(MY_ACCOUNT_LINK)
        page.click(LOGIN_LINK)
        return LoginPage(page, extentTest)
    }
}
