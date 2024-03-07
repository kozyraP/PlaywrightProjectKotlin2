package tests

import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import pages.HomePage

class AddProductToCartTests : BaseTest() {

    @BeforeClass
    fun setupBeforeClass() {
        extentTest = reporter.createTest("Test Suite - Verify Add Product to Cart", "Verify Add Products to Cart")
    }

    @Test
    fun searchProductWhichNotExistsTest() {
        testNode = extentTest.createNode("TC_05 Verify Product search which not exists")
        testNode.assignCategory("Test Suite - Verify Add Product to Cart")
        homePage = HomePage(page, testNode)
        Assert.assertFalse(homePage.searchProduct("InvalidProduct"))
    }

    @Test
    fun searchAndAddProductToCartWithoutLoginTest() {
        testNode = extentTest.createNode("TC_06 Verify Search And Add Product to Cart Without Login")
        testNode.assignCategory("Test Suite - Verify Add Product to Cart")
        homePage = HomePage(page, testNode)
        Assert.assertTrue(homePage.searchProduct("Macbook"))
        Assert.assertNotNull(homePage.addProductToCart())
    }

    @Test
    fun searchAndAddProductToCartWithLoginTest() {
        testNode = extentTest.createNode("TC_07 Verify Search And Add Product to Cart With Login")
        testNode.assignCategory("Test Suite - Verify Add Product to Cart")
        homePage = HomePage(page, testNode)
        loginPage = homePage.navigateToLoginPage()
        Assert.assertTrue(loginPage.doLogin(testProperties.getProperty("username").toString(), testProperties.getProperty("password").toString()))
        Assert.assertTrue(homePage.searchProduct("iPhone"))
        Assert.assertNotNull(homePage.addProductToCart())
    }
}
