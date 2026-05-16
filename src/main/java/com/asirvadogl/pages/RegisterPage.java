package com.asirvadogl.pages;

import com.asirvadogl.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * RegisterPage — Page Object for the Asirvad OGL New User Registration screen.
 *
 * <p>Typical registration flow:
 * <ol>
 *   <li>Enter Name, Mobile, Email, and set MPIN</li>
 *   <li>Accept Terms &amp; Conditions</li>
 *   <li>Tap "Register" — OTP is sent to the mobile</li>
 *   <li>Enter OTP on the next screen ({@link OtpVerificationPage})</li>
 * </ol>
 *
 * <p><strong>Locator note:</strong> All {@code resource-id} values below follow the
 * pattern {@code com.asirvadogl:id/<viewId>}. Verify against the live APK using
 * {@code appium-inspector} before running tests.
 */
public class RegisterPage extends BasePage {

    // ──────────────────────────────────────────────────────────────
    // Element Locators
    // ──────────────────────────────────────────────────────────────

    /** Full Name input */
    @AndroidFindBy(accessibility = "Full Name")
    // @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etFullName']")
    private WebElement fullNameField;

    /** Mobile Number input */
    @AndroidFindBy(accessibility = "Mobile Number")
    // @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etMobile']")
    private WebElement mobileNumberField;

    /** Email Address input */
    @AndroidFindBy(accessibility = "Email Address")
    // @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etEmail']")
    private WebElement emailField;

    /** Create MPIN input */
    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etCreateMpin']")
    private WebElement createMpinField;

    /** Confirm MPIN input */
    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etConfirmMpin']")
    private WebElement confirmMpinField;

    /** Referral Code input (optional field) */
    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etReferralCode']")
    private WebElement referralCodeField;

    /** Terms & Conditions checkbox */
    @AndroidFindBy(xpath = "//android.widget.CheckBox[@resource-id='com.asirvadogl:id/cbTerms']")
    private WebElement termsCheckbox;

    /** "Terms & Conditions" link (opens web view) */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Terms')]")
    private WebElement termsLink;

    /** Primary "Register" / "Submit" button */
    @AndroidFindBy(accessibility = "Register")
    // @AndroidFindBy(xpath = "//android.widget.Button[@resource-id='com.asirvadogl:id/btnRegister']")
    private WebElement registerButton;

    /** "Already have an account? Login" link */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Login') or contains(@text,'Sign In')]")
    private WebElement alreadyHaveAccountLink;

    /** Validation / error message label */
    @AndroidFindBy(xpath = "//android.widget.TextView[@resource-id='com.asirvadogl:id/tvRegisterError']")
    private WebElement errorMessageLabel;

    /** Page heading — used to confirm we are on the Register page */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Register') or contains(@text,'Create Account')]")
    private WebElement pageHeading;

    // ──────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────

    /**
     * @param driver AndroidDriver instance provided by {@code BaseTest.getDriver()}
     */
    public RegisterPage(AndroidDriver driver) {
        super(driver);
    }

    // ──────────────────────────────────────────────────────────────
    // Page Verification
    // ──────────────────────────────────────────────────────────────

    /**
     * Verifies that the Register page is currently displayed.
     *
     * @return true if the page heading is visible
     */
    public boolean isRegisterPageDisplayed() {
        log.info("Verifying Register page is displayed");
        return isDisplayed(pageHeading);
    }

    // ──────────────────────────────────────────────────────────────
    // Action Methods (fluent API)
    // ──────────────────────────────────────────────────────────────

    /**
     * Types the applicant's full name.
     *
     * @param fullName applicant name string
     * @return this page (fluent)
     */
    public RegisterPage enterFullName(String fullName) {
        log.info("Entering full name: {}", fullName);
        sendKeys(fullNameField, fullName);
        return this;
    }

    /**
     * Types the mobile number.
     *
     * @param mobileNumber 10-digit mobile number
     * @return this page (fluent)
     */
    public RegisterPage enterMobileNumber(String mobileNumber) {
        log.info("Entering mobile number: {}", mobileNumber);
        sendKeys(mobileNumberField, mobileNumber);
        return this;
    }

    /**
     * Types the email address.
     *
     * @param email valid email string
     * @return this page (fluent)
     */
    public RegisterPage enterEmail(String email) {
        log.info("Entering email: {}", email);
        sendKeys(emailField, email);
        return this;
    }

    /**
     * Types the new MPIN (typically 4–6 digits).
     *
     * @param mpin new MPIN
     * @return this page (fluent)
     */
    public RegisterPage enterCreateMpin(String mpin) {
        log.info("Entering Create MPIN (masked)");
        sendKeys(createMpinField, mpin);
        return this;
    }

    /**
     * Types the MPIN confirmation.
     *
     * @param mpin same MPIN entered in {@link #enterCreateMpin(String)}
     * @return this page (fluent)
     */
    public RegisterPage enterConfirmMpin(String mpin) {
        log.info("Entering Confirm MPIN (masked)");
        sendKeys(confirmMpinField, mpin);
        return this;
    }

    /**
     * Types an optional referral code.
     *
     * @param code referral code string (pass empty string to skip)
     * @return this page (fluent)
     */
    public RegisterPage enterReferralCode(String code) {
        if (code != null && !code.isEmpty()) {
            log.info("Entering referral code: {}", code);
            sendKeys(referralCodeField, code);
        }
        return this;
    }

    /**
     * Checks the Terms &amp; Conditions checkbox (if not already checked).
     *
     * @return this page (fluent)
     */
    public RegisterPage acceptTermsAndConditions() {
        log.info("Accepting Terms & Conditions");
        if (!termsCheckbox.isSelected()) {
            click(termsCheckbox);
        }
        return this;
    }

    /**
     * Taps the "Register" button to submit the form.
     *
     * @return {@link OtpVerificationPage} — next step in registration flow
     */
    public OtpVerificationPage tapRegisterButton() {
        log.info("Tapping Register button");
        click(registerButton);
        return new OtpVerificationPage(driver);
    }

    /**
     * Taps "Already have an account?" link to go back to Login.
     *
     * @return {@link LoginPage}
     */
    public LoginPage tapAlreadyHaveAccountLink() {
        log.info("Tapping 'Already have an account?' link");
        click(alreadyHaveAccountLink);
        return new LoginPage(driver);
    }

    /**
     * Convenience method: fills all mandatory registration fields and submits.
     *
     * @param name     full name
     * @param mobile   10-digit mobile number
     * @param email    email address
     * @param mpin     4–6 digit MPIN
     * @return {@link OtpVerificationPage}
     */
    public OtpVerificationPage registerWith(String name, String mobile,
                                             String email, String mpin) {
        return enterFullName(name)
                .enterMobileNumber(mobile)
                .enterEmail(email)
                .enterCreateMpin(mpin)
                .enterConfirmMpin(mpin)
                .acceptTermsAndConditions()
                .tapRegisterButton();
    }

    /**
     * Returns the error message shown after a failed registration attempt.
     *
     * @return error text, or empty string if no error is visible
     */
    public String getErrorMessage() {
        try {
            String msg = getText(errorMessageLabel);
            log.info("Registration error: {}", msg);
            return msg;
        } catch (Exception e) {
            log.warn("No error message found on Register page.");
            return "";
        }
    }
}
