package com.asirvadogl.pages;

import com.asirvadogl.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * LoginPage — Page Object for the Asirvad OGL Login screen.
 *
 * <p>Locator strategy:
 * <ul>
 *   <li>Prefer Accessibility ID (content-desc) — most stable across app updates.</li>
 *   <li>Fall back to XPath with text() or @resource-id when content-desc is absent.</li>
 *   <li>Avoid index-based XPath (e.g., //android.widget.EditText[1]) — brittle.</li>
 * </ul>
 *
 * <p><strong>Note:</strong> Update the resource-id / content-desc values below by
 * inspecting the live APK with {@code appium-inspector} or {@code uiautomatorviewer}.
 */
public class LoginPage extends BasePage {

    // ──────────────────────────────────────────────────────────────
    // Element Locators — @AndroidFindBy (Page Factory)
    // ──────────────────────────────────────────────────────────────

    /** Mobile number / username input field */
    @AndroidFindBy(accessibility = "Mobile Number")
    // Fallback XPath (uncomment if accessibility ID is unavailable):
    // @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etMobileNumber']")
    private WebElement mobileNumberField;

    /** Password / MPIN input field */
    @AndroidFindBy(accessibility = "Password")
    // @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etPassword']")
    private WebElement passwordField;

    /** Primary login / submit button */
    @AndroidFindBy(accessibility = "Login")
    // @AndroidFindBy(xpath = "//android.widget.Button[@resource-id='com.asirvadogl:id/btnLogin']")
    private WebElement loginButton;

    /** "Forgot Password?" / "Reset MPIN" link */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Forgot')]")
    private WebElement forgotPasswordLink;

    /** "Register" / "New User? Sign Up" link */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Register') or contains(@text,'Sign Up')]")
    private WebElement registerLink;

    /** Error / validation toast or label shown after failed login */
    @AndroidFindBy(xpath = "//android.widget.TextView[@resource-id='com.asirvadogl:id/tvErrorMessage']")
    private WebElement errorMessageLabel;

    /** App logo — used to assert the Login page is fully loaded */
    @AndroidFindBy(xpath = "//android.widget.ImageView[@resource-id='com.asirvadogl:id/ivLogo']")
    private WebElement appLogo;

    // ──────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────

    /**
     * @param driver AndroidDriver instance provided by {@code BaseTest.getDriver()}
     */
    public LoginPage(AndroidDriver driver) {
        super(driver);
    }

    // ──────────────────────────────────────────────────────────────
    // Page Verification
    // ──────────────────────────────────────────────────────────────

    /**
     * Verifies the Login page is displayed by checking the app logo visibility.
     *
     * @return true if the Login page is loaded
     */
    public boolean isLoginPageDisplayed() {
        log.info("Verifying Login page is displayed");
        return isDisplayed(appLogo);
    }

    // ──────────────────────────────────────────────────────────────
    // Action Methods
    // ──────────────────────────────────────────────────────────────

    /**
     * Enters the mobile number / username in the input field.
     *
     * @param mobileNumber 10-digit mobile number
     * @return this LoginPage (fluent API)
     */
    public LoginPage enterMobileNumber(String mobileNumber) {
        log.info("Entering mobile number: {}", mobileNumber);
        sendKeys(mobileNumberField, mobileNumber);
        return this;
    }

    /**
     * Enters the MPIN / password in the password field.
     *
     * @param password MPIN or password string
     * @return this LoginPage (fluent API)
     */
    public LoginPage enterPassword(String password) {
        log.info("Entering password (masked)");
        sendKeys(passwordField, password);
        return this;
    }

    /**
     * Taps the Login button.
     *
     * @return the next expected page — {@link DashboardPage} on success
     */
    public DashboardPage tapLoginButton() {
        log.info("Tapping Login button");
        click(loginButton);
        return new DashboardPage(driver);
    }

    /**
     * Convenience method: fills credentials and submits in one call.
     *
     * @param mobileNumber user's mobile number
     * @param password     user's MPIN / password
     * @return {@link DashboardPage} (returned even on failure so callers can assert)
     */
    public DashboardPage loginWith(String mobileNumber, String password) {
        return enterMobileNumber(mobileNumber)
                .enterPassword(password)
                .tapLoginButton();
    }

    /**
     * Taps the "Forgot Password" link.
     *
     * @return {@link ForgotPasswordPage}
     */
    public ForgotPasswordPage tapForgotPassword() {
        log.info("Tapping Forgot Password link");
        click(forgotPasswordLink);
        return new ForgotPasswordPage(driver);
    }

    /**
     * Taps the "Register" / "Sign Up" link to navigate to registration.
     *
     * @return {@link RegisterPage}
     */
    public RegisterPage tapRegisterLink() {
        log.info("Tapping Register link");
        click(registerLink);
        return new RegisterPage(driver);
    }

    /**
     * Returns the error message shown after an invalid login attempt.
     *
     * @return error message text, or empty string if not visible
     */
    public String getErrorMessage() {
        try {
            String msg = getText(errorMessageLabel);
            log.info("Error message displayed: {}", msg);
            return msg;
        } catch (Exception e) {
            log.warn("No error message element found on screen.");
            return "";
        }
    }

    /**
     * Checks whether an error message is displayed on the screen.
     *
     * @return true if an error label is visible
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessageLabel);
    }
}
