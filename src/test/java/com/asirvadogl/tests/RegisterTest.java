package com.asirvadogl.tests;

import com.asirvadogl.base.BaseTest;
import com.asirvadogl.pages.LoginPage;
import com.asirvadogl.pages.RegisterPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * RegisterTest — TestNG test class for all Registration-screen scenarios.
 *
 * <p>Test coverage:
 * <ol>
 *   <li>Verify Register page is displayed after tapping the register link</li>
 *   <li>All mandatory field validations (empty fields)</li>
 *   <li>Invalid mobile number format</li>
 *   <li>MPIN mismatch</li>
 *   <li>Already-registered mobile number shows an error</li>
 *   <li>Valid registration proceeds to OTP verification</li>
 *   <li>Navigation back to Login page</li>
 * </ol>
 */
public class RegisterTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────
    // Test Data — TODO: move to JSON data file in src/test/resources/testdata/
    // ──────────────────────────────────────────────────────────────

    /** A UNIQUE mobile number for each test run (use a strategy like timestamp suffix) */
    private static final String NEW_MOBILE     = "9876543210"; // replace per run
    private static final String EXISTING_MOBILE = "9999900000"; // already registered

    private static final String VALID_NAME  = "Test User";
    private static final String VALID_EMAIL = "testuser@gmail.com";
    private static final String VALID_MPIN  = "4321";

    // ──────────────────────────────────────────────────────────────
    // Page Objects
    // ──────────────────────────────────────────────────────────────

    private RegisterPage registerPage;

    /**
     * Navigate from Login → Register page before each test.
     * Runs after {@code BaseTest.setUp()}.
     */
    @BeforeMethod(dependsOnMethods = "setUp")
    public void navigateToRegisterPage() {
        LoginPage loginPage = new LoginPage(getDriver());
        registerPage = loginPage.tapRegisterLink();
    }

    // ──────────────────────────────────────────────────────────────
    // Tests
    // ──────────────────────────────────────────────────────────────

    /**
     * TC-R-001: The Register page must load correctly after tapping the link.
     */
    @Test(priority = 1,
          description = "TC-R-001: Register page must be visible",
          groups = {"smoke", "regression"})
    public void testRegisterPageIsDisplayed() {
        Assert.assertTrue(
            registerPage.isRegisterPageDisplayed(),
            "Register page should be displayed after tapping the Register link.");
    }

    /**
     * TC-R-002: Submitting all empty fields should show a validation error.
     */
    @Test(priority = 2,
          description = "TC-R-002: Empty form submission must trigger validation",
          groups = {"regression"})
    public void testEmptyFormShowsError() {
        registerPage.tapRegisterButton(); // submit without filling anything

        String error = registerPage.getErrorMessage();
        Assert.assertFalse(error.isEmpty(),
            "A validation error should appear when form is submitted empty.");
    }

    /**
     * TC-R-003: MPIN and Confirm MPIN mismatch should show an error.
     */
    @Test(priority = 3,
          description = "TC-R-003: MPIN mismatch must show an error",
          groups = {"regression"})
    public void testMpinMismatchShowsError() {
        registerPage.enterFullName(VALID_NAME)
                    .enterMobileNumber(NEW_MOBILE)
                    .enterEmail(VALID_EMAIL)
                    .enterCreateMpin("1111")
                    .enterConfirmMpin("2222") // deliberate mismatch
                    .acceptTermsAndConditions()
                    .tapRegisterButton();

        String error = registerPage.getErrorMessage().toLowerCase();
        boolean isMismatchError =
            error.contains("mpin")    ||
            error.contains("match")   ||
            error.contains("confirm") ||
            error.contains("password");

        Assert.assertTrue(isMismatchError,
            "MPIN mismatch should produce an appropriate error. Got: " + error);
    }

    /**
     * TC-R-004: An already-registered mobile number should show a duplicate error.
     */
    @Test(priority = 4,
          description = "TC-R-004: Existing mobile number must show duplicate error",
          groups = {"regression"})
    public void testAlreadyRegisteredMobileShowsError() {
        registerPage.enterFullName(VALID_NAME)
                    .enterMobileNumber(EXISTING_MOBILE) // already in the system
                    .enterEmail(VALID_EMAIL)
                    .enterCreateMpin(VALID_MPIN)
                    .enterConfirmMpin(VALID_MPIN)
                    .acceptTermsAndConditions()
                    .tapRegisterButton();

        String error = registerPage.getErrorMessage().toLowerCase();
        boolean isDuplicateError =
            error.contains("already")    ||
            error.contains("registered") ||
            error.contains("exists")     ||
            error.contains("duplicate");

        Assert.assertTrue(isDuplicateError,
            "Duplicate mobile error expected. Got: " + error);
    }

    /**
     * TC-R-005: Invalid email format should show a validation error.
     */
    @Test(priority = 5,
          description = "TC-R-005: Invalid email format must fail validation",
          groups = {"regression"})
    public void testInvalidEmailFormatShowsError() {
        registerPage.enterFullName(VALID_NAME)
                    .enterMobileNumber(NEW_MOBILE)
                    .enterEmail("not-an-email") // deliberately invalid
                    .enterCreateMpin(VALID_MPIN)
                    .enterConfirmMpin(VALID_MPIN)
                    .acceptTermsAndConditions()
                    .tapRegisterButton();

        String error = registerPage.getErrorMessage().toLowerCase();
        Assert.assertFalse(error.isEmpty(),
            "An email format error should be shown. Got: " + error);
    }

    /**
     * TC-R-006: Valid form submission should navigate to the OTP screen.
     *
     * <p>This is the "happy path". Change {@code NEW_MOBILE} to a fresh number
     * for each test run (or mock the API in a stub environment).
     */
    @Test(priority = 6,
          description = "TC-R-006: Valid registration advances to OTP screen",
          groups = {"smoke", "regression"})
    public void testValidRegistrationNavigatesToOtpPage() {
        boolean otpPageDisplayed =
            registerPage.registerWith(VALID_NAME, NEW_MOBILE, VALID_EMAIL, VALID_MPIN)
                        .isOtpPageDisplayed();

        Assert.assertTrue(otpPageDisplayed,
            "OTP Verification page should be displayed after successful registration.");
    }

    /**
     * TC-R-007: Tapping "Already have an account" returns to Login page.
     */
    @Test(priority = 7,
          description = "TC-R-007: Back to Login link navigates correctly",
          groups = {"smoke", "regression"})
    public void testBackToLoginNavigation() {
        boolean loginPageDisplayed =
            registerPage.tapAlreadyHaveAccountLink()
                        .isLoginPageDisplayed();

        Assert.assertTrue(loginPageDisplayed,
            "Login page should be displayed after tapping 'Already have an account?'.");
    }

    /**
     * TC-R-008: Data-driven field validation — various invalid inputs.
     */
    @Test(priority = 8,
          dataProvider = "invalidRegistrationData",
          description = "TC-R-008: Data-driven registration validation",
          groups = {"regression"})
    public void testRegistrationFieldValidations(String name, String mobile,
                                                  String email, String mpin,
                                                  String confirmMpin,
                                                  String scenario) {
        registerPage.enterFullName(name)
                    .enterMobileNumber(mobile)
                    .enterEmail(email)
                    .enterCreateMpin(mpin)
                    .enterConfirmMpin(confirmMpin)
                    .acceptTermsAndConditions()
                    .tapRegisterButton();

        String error = registerPage.getErrorMessage();
        Assert.assertFalse(error.isEmpty(),
            "Scenario [" + scenario + "] should trigger a validation error.");
    }

    // ──────────────────────────────────────────────────────────────
    // Data Providers
    // ──────────────────────────────────────────────────────────────

    /**
     * Rows: [name, mobile, email, mpin, confirmMpin, scenarioDescription]
     */
    @DataProvider(name = "invalidRegistrationData")
    public Object[][] invalidRegistrationData() {
        return new Object[][] {
            { "",          "9876543210", "test@mail.com", "1234", "1234", "Empty name"           },
            { "Test User", "123",        "test@mail.com", "1234", "1234", "Short mobile"         },
            { "Test User", "9876543210", "badmail",       "1234", "1234", "Invalid email"        },
            { "Test User", "9876543210", "test@mail.com", "12",   "12",   "MPIN too short"       },
            { "Test User", "9876543210", "test@mail.com", "1234", "5678", "MPIN mismatch"        },
        };
    }
}
