package com.asirvadogl.pages;

import com.asirvadogl.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

// ════════════════════════════════════════════════════════════════════════════
//  DashboardPage
//  Entry point after successful login. Extend this class with Gold Loan
//  navigation methods as the E2E suite grows.
// ════════════════════════════════════════════════════════════════════════════
class DashboardPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────

    /** Welcome / greeting message shown after login */
    @AndroidFindBy(xpath = "//android.widget.TextView[@resource-id='com.asirvadogl:id/tvWelcome']")
    private WebElement welcomeMessage;

    /**
     * "Gold Loan" menu item or card on the dashboard.
     * TODO: Update resource-id after inspecting the live app.
     */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Gold Loan')]")
    private WebElement goldLoanMenuItem;

    /** Profile / account icon in the app bar */
    @AndroidFindBy(accessibility = "Profile")
    private WebElement profileIcon;

    /** Logout option (usually inside a side-drawer or profile menu) */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Logout') or contains(@text,'Sign Out')]")
    private WebElement logoutOption;

    // ── Constructor ───────────────────────────────────────────────

    DashboardPage(AndroidDriver driver) {
        super(driver);
    }

    // ── Verification ──────────────────────────────────────────────

    /**
     * Confirms the Dashboard is loaded by checking the welcome message.
     *
     * @return true if the dashboard is visible
     */
    public boolean isDashboardDisplayed() {
        log.info("Verifying Dashboard is displayed");
        return isDisplayed(welcomeMessage);
    }

    // ── Navigation — Gold Loan E2E Placeholder ────────────────────

    /**
     * Navigates to the Gold Loan section.
     *
     * @return {@link GoldLoanPage} (to be implemented for E2E gold loan tests)
     */
    public GoldLoanPage navigateToGoldLoan() {
        log.info("Navigating to Gold Loan section");
        click(goldLoanMenuItem);
        return new GoldLoanPage(driver);
    }

    /**
     * Logs out the current user.
     *
     * @return {@link LoginPage}
     */
    public LoginPage logout() {
        log.info("Logging out");
        click(profileIcon);
        click(logoutOption);
        return new LoginPage(driver);
    }
}


// ════════════════════════════════════════════════════════════════════════════
//  GoldLoanPage — E2E Gold Loan Functionality Placeholder
//  Add locators and action methods here as the gold loan features are tested.
// ════════════════════════════════════════════════════════════════════════════
class GoldLoanPage extends BasePage {

    // ── TODO: Add Gold Loan locators ──────────────────────────────

    /**
     * "Apply for Gold Loan" button — placeholder; update resource-id.
     */
    @AndroidFindBy(xpath = "//android.widget.Button[contains(@text,'Apply')]")
    private WebElement applyGoldLoanButton;

    /**
     * Loan amount input — placeholder.
     */
    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etLoanAmount']")
    private WebElement loanAmountField;

    /**
     * Gold weight input — placeholder.
     */
    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etGoldWeight']")
    private WebElement goldWeightField;

    /**
     * "Check Eligibility" button — placeholder.
     */
    @AndroidFindBy(xpath = "//android.widget.Button[contains(@text,'Eligibility')]")
    private WebElement checkEligibilityButton;

    /**
     * "Track Loan" option — placeholder.
     */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Track')]")
    private WebElement trackLoanOption;

    GoldLoanPage(AndroidDriver driver) {
        super(driver);
    }

    // ── TODO: Implement E2E Gold Loan Action Methods ───────────────

    /**
     * Initiates a new gold loan application.
     * TODO: Implement after inspecting the Apply Loan screen.
     */
    public GoldLoanPage tapApplyForGoldLoan() {
        log.info("[PLACEHOLDER] Tapping Apply for Gold Loan");
        click(applyGoldLoanButton);
        return this;
    }

    /**
     * Enters the desired loan amount.
     * TODO: Implement with actual field inspection.
     *
     * @param amount loan amount in INR
     */
    public GoldLoanPage enterLoanAmount(String amount) {
        log.info("[PLACEHOLDER] Entering loan amount: {}", amount);
        sendKeys(loanAmountField, amount);
        return this;
    }

    /**
     * Enters the gold weight in grams.
     *
     * @param weightInGrams gold weight string
     */
    public GoldLoanPage enterGoldWeight(String weightInGrams) {
        log.info("[PLACEHOLDER] Entering gold weight: {} g", weightInGrams);
        sendKeys(goldWeightField, weightInGrams);
        return this;
    }

    /**
     * Taps Check Eligibility to see loan offer.
     */
    public GoldLoanPage checkEligibility() {
        log.info("[PLACEHOLDER] Checking loan eligibility");
        click(checkEligibilityButton);
        return this;
    }

    /**
     * Navigates to the Track Loan screen.
     */
    public GoldLoanPage trackLoan() {
        log.info("[PLACEHOLDER] Navigating to Track Loan");
        click(trackLoanOption);
        return this;
    }
}


// ════════════════════════════════════════════════════════════════════════════
//  OtpVerificationPage — Screen shown after registration / forgot-password
// ════════════════════════════════════════════════════════════════════════════
class OtpVerificationPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etOtp']")
    private WebElement otpField;

    @AndroidFindBy(xpath = "//android.widget.Button[@resource-id='com.asirvadogl:id/btnVerifyOtp']")
    private WebElement verifyOtpButton;

    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Resend')]")
    private WebElement resendOtpLink;

    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'OTP') or contains(@text,'Verify')]")
    private WebElement pageHeading;

    OtpVerificationPage(AndroidDriver driver) {
        super(driver);
    }

    /** @return true if the OTP screen is visible */
    public boolean isOtpPageDisplayed() {
        return isDisplayed(pageHeading);
    }

    /**
     * Enters the OTP and taps Verify.
     *
     * @param otp one-time password received via SMS
     * @return {@link DashboardPage} on successful verification
     */
    public DashboardPage verifyOtp(String otp) {
        log.info("Entering OTP (masked)");
        sendKeys(otpField, otp);
        click(verifyOtpButton);
        return new DashboardPage(driver);
    }

    /** Taps the "Resend OTP" link. */
    public OtpVerificationPage resendOtp() {
        log.info("Requesting OTP resend");
        click(resendOtpLink);
        return this;
    }
}


// ════════════════════════════════════════════════════════════════════════════
//  ForgotPasswordPage — Reset MPIN flow
// ════════════════════════════════════════════════════════════════════════════
class ForgotPasswordPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.asirvadogl:id/etForgotMobile']")
    private WebElement mobileNumberField;

    @AndroidFindBy(xpath = "//android.widget.Button[@resource-id='com.asirvadogl:id/btnSendOtp']")
    private WebElement sendOtpButton;

    ForgotPasswordPage(AndroidDriver driver) {
        super(driver);
    }

    /**
     * Submits the mobile number to receive a reset OTP.
     *
     * @param mobile registered mobile number
     * @return {@link OtpVerificationPage}
     */
    public OtpVerificationPage requestPasswordReset(String mobile) {
        log.info("Requesting password reset for mobile: {}", mobile);
        sendKeys(mobileNumberField, mobile);
        click(sendOtpButton);
        return new OtpVerificationPage(driver);
    }
}
