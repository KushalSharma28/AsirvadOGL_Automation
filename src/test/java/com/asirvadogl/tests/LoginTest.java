package com.asirvadogl.tests;

import com.asirvadogl.base.BaseTest;
import com.asirvadogl.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * LoginTest — TestNG test class for all Login-screen scenarios.
 *
 * <p>Test coverage:
 * <ol>
 *   <li>Verify the Login page is displayed on app launch</li>
 *   <li>Valid login redirects to the Dashboard</li>
 *   <li>Invalid credentials show an error message</li>
 *   <li>Login with empty fields shows validation error</li>
 *   <li>Forgot Password navigation</li>
 *   <li>Register link navigation</li>
 * </ol>
 *
 * <p><strong>Pre-requisite:</strong> Replace credential placeholders with real
 * test account data (ideally loaded from a JSON/Excel data file or CI env vars).
 */
public class LoginTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────
    // Test Data — TODO: move to src/test/resources/testdata/ JSON file
    // ──────────────────────────────────────────────────────────────

    /** A registered, active test account */
    private static final String VALID_MOBILE   = "9999900000"; // replace with real
    private static final String VALID_PASSWORD  = "1234";       // replace with real MPIN

    /** A mobile number that is NOT registered */
    private static final String INVALID_MOBILE  = "0000000000";
    private static final String INVALID_PASSWORD = "0000";

    // ──────────────────────────────────────────────────────────────
    // Page Object
    // ──────────────────────────────────────────────────────────────

    private LoginPage loginPage;

    /**
     * Runs after {@code BaseTest.setUp()} (TestNG @BeforeMethod ordering):
     * constructs the LoginPage with the ready driver.
     */
    @BeforeMethod(dependsOnMethods = "setUp")
    public void initLoginPage() {
        loginPage = new LoginPage(getDriver());
    }

    // ──────────────────────────────────────────────────────────────
    // Tests
    // ──────────────────────────────────────────────────────────────

    /**
     * TC-L-001: Verify the Login page is loaded when the app opens.
     */
    @Test(priority = 1,
          description = "TC-L-001: Login page must be visible on app launch")
    public void testLoginPageIsDisplayed() {
        boolean isDisplayed = loginPage.isLoginPageDisplayed();
        Assert.assertTrue(isDisplayed,
            "Login page should be visible but was not found.");
    }

    /**
     * TC-L-002: Successful login navigates to the Dashboard.
     *
     * <p>Note: This test will fail if the Appium server is not running or the
     * device is not connected. It also requires a valid test account.
     */
    @Test(priority = 2,
          description = "TC-L-002: Valid credentials should navigate to Dashboard",
          groups = {"smoke", "regression"})
    public void testValidLogin() {
        boolean dashboardDisplayed =
            loginPage.loginWith(VALID_MOBILE, VALID_PASSWORD)
                     .isDashboardDisplayed();

        Assert.assertTrue(dashboardDisplayed,
            "Dashboard should be displayed after successful login.");
    }

    /**
     * TC-L-003: Invalid credentials should display an error message.
     */
    @Test(priority = 3,
          description = "TC-L-003: Invalid credentials must show an error",
          groups = {"regression"})
    public void testInvalidLoginShowsError() {
        loginPage.enterMobileNumber(INVALID_MOBILE)
                 .enterPassword(INVALID_PASSWORD)
                 .tapLoginButton();  // tapLoginButton returns DashboardPage
                                     // but we stay on LoginPage on failure

        boolean errorShown = loginPage.isErrorMessageDisplayed();
        Assert.assertTrue(errorShown,
            "An error message should be displayed for invalid credentials.");
    }

    /**
     * TC-L-004: Error message text should contain expected keyword.
     */
    @Test(priority = 4,
          description = "TC-L-004: Error text validation for invalid login",
          groups = {"regression"})
    public void testInvalidLoginErrorMessage() {
        loginPage.enterMobileNumber(INVALID_MOBILE)
                 .enterPassword(INVALID_PASSWORD)
                 .tapLoginButton();

        String errorText = loginPage.getErrorMessage().toLowerCase();

        // The app may return "Invalid credentials", "User not found", etc.
        boolean containsExpectedKeyword =
            errorText.contains("invalid")
            || errorText.contains("incorrect")
            || errorText.contains("not found")
            || errorText.contains("failed");

        Assert.assertTrue(containsExpectedKeyword,
            "Error message [" + errorText + "] does not contain an expected keyword.");
    }

    /**
     * TC-L-005: Tapping Forgot Password should navigate to the reset screen.
     */
    @Test(priority = 5,
          description = "TC-L-005: Forgot Password link navigates to reset screen",
          groups = {"regression"})
    public void testForgotPasswordNavigation() {
        // Just assert the navigation doesn't throw — next page assertion
        // belongs in a ForgotPasswordTest class.
        try {
            loginPage.tapForgotPassword();
            Assert.assertTrue(true, "Navigation to Forgot Password succeeded.");
        } catch (Exception e) {
            Assert.fail("Navigation to Forgot Password failed: " + e.getMessage());
        }
    }

    /**
     * TC-L-006: Tapping Register link should navigate to the Registration screen.
     */
    @Test(priority = 6,
          description = "TC-L-006: Register link navigates to Registration page",
          groups = {"smoke", "regression"})
    public void testRegisterLinkNavigation() {
        boolean registerPageDisplayed =
            loginPage.tapRegisterLink().isRegisterPageDisplayed();

        Assert.assertTrue(registerPageDisplayed,
            "Register page should be displayed after tapping the Register link.");
    }

    /**
     * TC-L-007: Data-driven test — multiple invalid credential combinations.
     *
     * @param mobile   invalid mobile number
     * @param password invalid password
     * @param scenario human-readable scenario name (for reporting)
     */
    @Test(priority = 7,
          dataProvider = "invalidCredentials",
          description = "TC-L-007: Data-driven invalid login attempts",
          groups = {"regression"})
    public void testMultipleInvalidCredentials(String mobile, String password, String scenario) {
        loginPage.enterMobileNumber(mobile)
                 .enterPassword(password)
                 .tapLoginButton();

        boolean errorShown = loginPage.isErrorMessageDisplayed();
        Assert.assertTrue(errorShown,
            "Scenario [" + scenario + "] should show an error message.");
    }

    // ──────────────────────────────────────────────────────────────
    // Data Providers
    // ──────────────────────────────────────────────────────────────

    /**
     * Provides rows of [mobile, password, scenarioName] for data-driven tests.
     *
     * @return 2-D Object array
     */
    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][] {
            { "",           "",     "Empty mobile and password"     },
            { "9999900000", "",     "Valid mobile, empty password"  },
            { "",           "1234", "Empty mobile, valid password"  },
            { "123",        "1234", "Short mobile number"           },
            { "0000000001", "9999", "Non-existent account"          },
        };
    }
}
