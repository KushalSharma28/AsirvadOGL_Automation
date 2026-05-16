package com.asirvadogl.base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BasePage — Parent class for every Page Object.
 *
 * <p>Provides:
 * <ul>
 *   <li>Page Factory initialisation with {@link AppiumFieldDecorator}</li>
 *   <li>Centralised explicit-wait helpers (no {@code Thread.sleep()} anywhere)</li>
 *   <li>Common interaction wrappers (click, sendKeys, getText)</li>
 *   <li>A shared logger for every subclass</li>
 * </ul>
 */
public abstract class BasePage {

    protected static final Logger log = LogManager.getLogger(BasePage.class);

    /** Default wait timeout in seconds. Override in subclass if a page is slow. */
    protected static final int DEFAULT_TIMEOUT = 15;

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    /**
     * Constructs a BasePage, initialises Page Factory with AppiumFieldDecorator,
     * and creates a shared WebDriverWait.
     *
     * @param driver the AndroidDriver instance from BaseTest
     */
    protected BasePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));

        // AppiumFieldDecorator understands @AndroidFindBy / @FindBy annotations
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    // ──────────────────────────────────────────────────────────────
    // Wait Helpers — NO Thread.sleep() anywhere in the framework
    // ──────────────────────────────────────────────────────────────

    /**
     * Waits until the element is visible on screen.
     *
     * @param element target element
     * @return the same element once visible (for method chaining)
     */
    protected WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits until the element is clickable (visible + enabled).
     *
     * @param element target element
     * @return the same element once clickable
     */
    protected WebElement waitForClickability(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits with a custom timeout (use sparingly; prefer DEFAULT_TIMEOUT).
     *
     * @param element       target element
     * @param timeoutSeconds override timeout
     * @return the same element once visible
     */
    protected WebElement waitForVisibility(WebElement element, int timeoutSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return customWait.until(ExpectedConditions.visibilityOf(element));
    }

    // ──────────────────────────────────────────────────────────────
    // Common Interaction Wrappers
    // ──────────────────────────────────────────────────────────────

    /**
     * Waits for an element to be clickable, then clicks it.
     *
     * @param element target element
     */
    protected void click(WebElement element) {
        waitForClickability(element).click();
    }

    /**
     * Waits for visibility, clears any existing text, and types the given value.
     *
     * @param element target input field
     * @param text    value to type
     */
    protected void sendKeys(WebElement element, String text) {
        waitForVisibility(element).clear();
        element.sendKeys(text);
    }

    /**
     * Waits for visibility and returns the element's visible text.
     *
     * @param element target element
     * @return trimmed text content
     */
    protected String getText(WebElement element) {
        return waitForVisibility(element).getText().trim();
    }

    /**
     * Checks whether the element is currently displayed without throwing an exception.
     *
     * @param element target element
     * @return true if visible, false otherwise
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
