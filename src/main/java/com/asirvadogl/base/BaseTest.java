package com.asirvadogl.base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

/**
 * BaseTest — Foundation class for all test classes.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Read capability values from {@code config.properties}</li>
 *   <li>Build {@link UiAutomator2Options} and start {@link AndroidDriver}</li>
 *   <li>Expose the driver via {@link #getDriver()} for Page Objects</li>
 *   <li>Quit the driver after every test method via {@link #tearDown()}</li>
 * </ul>
 *
 * <p>All test classes must extend this class.
 */
public class BaseTest {

    // ──────────────────────────────────────────────────────────────
    // Fields
    // ──────────────────────────────────────────────────────────────

    /** Logger — replace System.out.println with structured log statements. */
    private static final Logger log = LogManager.getLogger(BaseTest.class);

    /**
     * ThreadLocal driver ensures each test thread gets its own driver instance,
     * supporting parallel execution without state conflicts.
     */
    private static final ThreadLocal<AndroidDriver> driverThreadLocal = new ThreadLocal<>();

    /** Appium server URL — reads from config.properties; default localhost. */
    private static final String DEFAULT_APPIUM_URL = "http://127.0.0.1:4723";

    /** Loaded from src/test/resources/config.properties */
    private Properties config;

    // ──────────────────────────────────────────────────────────────
    // TestNG Lifecycle
    // ──────────────────────────────────────────────────────────────

    /**
     * Runs before every @Test method.
     * Loads config, builds capabilities, and starts the Appium driver.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        log.info("========== Test Setup Started ==========");
        loadConfig();
        AndroidDriver driver = initDriver();
        driverThreadLocal.set(driver);
        log.info("AndroidDriver initialised successfully.");
    }

    /**
     * Runs after every @Test method.
     * Quits the driver and removes it from ThreadLocal to prevent memory leaks.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        AndroidDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("AndroidDriver quit successfully.");
            } catch (Exception e) {
                log.error("Error while quitting the driver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
        log.info("========== Test Teardown Complete ==========");
    }

    // ──────────────────────────────────────────────────────────────
    // Public API
    // ──────────────────────────────────────────────────────────────

    /**
     * Returns the {@link AndroidDriver} bound to the current thread.
     * Page Objects and test classes call this method to interact with the app.
     *
     * @return current thread's AndroidDriver instance
     * @throws IllegalStateException if the driver has not been initialised
     */
    public AndroidDriver getDriver() {
        AndroidDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                "AndroidDriver is null. Ensure setUp() ran before calling getDriver().");
        }
        return driver;
    }

    // ──────────────────────────────────────────────────────────────
    // Private Helpers
    // ──────────────────────────────────────────────────────────────

    /**
     * Reads {@code src/test/resources/config.properties} into {@link #config}.
     */
    private void loadConfig() {
        config = new Properties();
        String configPath = "src/test/resources/config.properties";
        try (FileInputStream fis = new FileInputStream(configPath)) {
            config.load(fis);
            log.info("Config loaded from: {}", configPath);
        } catch (IOException e) {
            log.error("Failed to load config.properties — using defaults. Error: {}", e.getMessage());
            // Framework will fall back to default values; tests can still run
            // against a locally connected device with default caps.
        }
    }

    /**
     * Builds {@link UiAutomator2Options} from config and creates the driver.
     *
     * <p>Key capabilities:
     * <ul>
     *   <li>{@code platformName} — Android</li>
     *   <li>{@code deviceName}   — read from config or default</li>
     *   <li>{@code appPackage}   — com.asirvadogl</li>
     *   <li>{@code appActivity}  — entry-point activity</li>
     *   <li>{@code automationName} — UiAutomator2</li>
     *   <li>{@code noReset}     — true (preserve app state between tests)</li>
     * </ul>
     *
     * @return a ready-to-use {@link AndroidDriver}
     */
    private AndroidDriver initDriver() {
        // Read capabilities from config (with sensible defaults)
        String deviceName   = getProp("device.name",    "Android Emulator");
        String udid         = getProp("device.udid",    "");
        String platformVer  = getProp("platform.version","14.0");
        String appPackage   = getProp("app.package",    "com.asirvadogl");
        String appActivity  = getProp("app.activity",   "com.asirvadogl.ui.splash.SplashActivity");
        String appiumUrl    = getProp("appium.url",     DEFAULT_APPIUM_URL);
        boolean noReset     = Boolean.parseBoolean(getProp("no.reset", "true"));
        int implicitWait    = Integer.parseInt(getProp("implicit.wait.seconds", "0"));

        // Build UiAutomator2Options (replaces deprecated DesiredCapabilities)
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName(deviceName);
        options.setPlatformVersion(platformVer);
        options.setAppPackage(appPackage);
        options.setAppActivity(appActivity);
        options.setAutomationName("UiAutomator2");
        options.setNoReset(noReset);

        // Optionally set UDID (needed when multiple devices are connected)
        if (udid != null && !udid.isEmpty()) {
            options.setUdid(udid);
        }

        // Implicit wait = 0 so ExplicitWaits (WebDriverWait) remain fully in control.
        // Setting both causes unpredictable wait stacking — keep implicit at 0.
        if (implicitWait > 0) {
            options.setImplicitWaitTimeout(Duration.ofSeconds(implicitWait));
        }

        log.info("Capabilities → device: {}, package: {}, activity: {}", deviceName, appPackage, appActivity);

        try {
            URL serverUrl = new URL(appiumUrl);
            return new AndroidDriver(serverUrl, options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + appiumUrl, e);
        }
    }

    /**
     * Safe property reader — returns {@code defaultValue} when the key is absent.
     *
     * @param key          property key
     * @param defaultValue fallback value
     * @return property value or default
     */
    private String getProp(String key, String defaultValue) {
        if (config == null) return defaultValue;
        return config.getProperty(key, defaultValue);
    }
}
