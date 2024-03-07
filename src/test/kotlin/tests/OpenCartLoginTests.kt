package tests

import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import pages.HomePage
import java.util.UUID

class OpenCartLoginTests : BaseTest() {

    @BeforeClass
    fun setupBeforeClass() {
        extentTest = reporter.createTest("Test Suite Verify Open Cart Login", "Verify login functionality of Open Cart")
    }

    @Test
    fun loginWithExistingUserCredentialsTest() {
        testNode = extentTest.createNode("TC_01 & TC_03 Validate Homepage & Verify Open Cart Login with Valid Credentials")
        testNode.assignCategory("Test Suite Verify Open Cart Login")
        homePage = HomePage(page, testNode)
        softAssert.assertEquals(homePage.getHomePageTitle(), HOME_PAGE_TITLE)
        loginPage = homePage.navigateToLoginPage()
        Assert.assertTrue(loginPage.doLogin(testProperties.getProperty("username").toString(), testProperties.getProperty("password").toString()))
    }

    @Test
    fun loginWithNonExistingUserCredentialsTest() {
        testNode = extentTest.createNode("TC_02 & TC04 Validate LoginPage & Verify Open Cart Login with Invalid Credentials")
        testNode.assignCategory("Test Suite Verify Open Cart Login")
        homePage = HomePage(page, testNode)
        loginPage = homePage.navigateToLoginPage()
        softAssert.assertEquals(loginPage.getLoginPageTitle(), LOGIN_PAGE_TITLE)
        Assert.assertFalse(loginPage.doLogin(UUID.randomUUID().toString().replace("-", ""), "InvalidPassword"))
    }
}
