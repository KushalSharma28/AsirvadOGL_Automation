# Asirvad OGL — Appium Mobile Automation Framework

> **Stack:** Appium 9.x · Java 11 · Maven · TestNG 7 · Page Object Model + Page Factory  
> **Target App:** [Asirvad OGL](https://play.google.com/store/apps/details?id=com.asirvadogl) (Android)

---

## Directory Structure

```
AsirvadOGL_Framework/                    ← Eclipse project root
│
├── pom.xml                              ← Maven build file (all dependencies)
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/asirvadogl/
│   │           ├── base/
│   │           │   ├── BaseTest.java    ← Driver init, setUp, tearDown (ThreadLocal)
│   │           │   └── BasePage.java   ← Page Factory init, wait helpers, click/sendKeys
│   │           └── pages/
│   │               ├── LoginPage.java        ← Login screen POM
│   │               ├── RegisterPage.java     ← Registration screen POM
│   │               └── SupportPages.java     ← Dashboard, GoldLoanPage, OTP, ForgotPassword
│   │
│   └── test/
│       ├── java/
│       │   └── com/asirvadogl/
│       │       └── tests/
│       │           ├── LoginTest.java        ← Login test cases (7 scenarios)
│       │           ├── RegisterTest.java     ← Registration test cases (8 scenarios)
│       │           └── GoldLoanTest.java     ← E2E gold loan placeholder (6 flows)
│       │
│       └── resources/
│           ├── config.properties        ← Appium capabilities (device, package, URL)
│           ├── testng.xml               ← TestNG suite (Smoke + Regression + E2E)
│           └── log4j2.xml              ← Logging configuration
│
└── logs/                               ← Auto-created; contains automation.log
```

---

## Prerequisites

| Tool            | Version       | Install                               |
|-----------------|---------------|---------------------------------------|
| Java JDK        | 25.0.3        | https://adoptium.net                  |
| Maven           | 3.9+          | https://maven.apache.org/download.cgi |
| Node.js         | 24.15.0       | https://nodejs.org                    |
| Appium Server   | 3.4.2         | `npm install -g appium`               |
| UiAutomator2    | latest        | `appium driver install uiautomator2`  |
| Android SDK     | API 28+       | Via Android Studio                    |
| Eclipse IDE     | 2026-03+      | https://www.eclipse.org               |

---

## Quick Start

### 1 — Start the Appium Server
```bash
appium --port 4723 --relaxed-security
```

### 2 — Connect Device / Start Emulator
```bash
adb devices          # confirm device is listed
```

### 3 — Configure `config.properties`
```properties
# src/test/resources/config.properties
device.name=<your-device-name>
device.udid=<udid-if-multiple-devices>
platform.version=14.0
app.package=com.asirvadogl
app.activity=com.asirvadogl.ui.splash.SplashActivity
```

### 4 — Find the Correct Activity
```bash
# While the app is open on device:
adb shell dumpsys window | grep -E 'mCurrentFocus|mFocusedApp'
```

### 5 — Run Tests
```bash
# All tests via Maven
mvn test

# Smoke tests only
mvn test -Dgroups=smoke

# Specific class
mvn test -Dtest=LoginTest

# Specific method
mvn test -Dtest=LoginTest#testValidLogin
```

---

## Import into Eclipse

1. **File → Import → Maven → Existing Maven Projects**
2. Select the `AsirvadOGL_Framework` folder → Finish
3. Right-click `pom.xml` → **Maven → Update Project** (Alt + F5)
4. Install the **TestNG for Eclipse** plugin (Help → Eclipse Marketplace → search "TestNG")
5. Run tests: right-click `testng.xml` → **Run As → TestNG Suite**

---

## Updating Locators

All element locators use `@AndroidFindBy` annotations in the Page Objects.  
Inspect the live APK with:

```bash
# Option 1 — Appium Inspector (GUI, recommended)
# https://github.com/appium/appium-inspector/releases

# Option 2 — uiautomatorviewer (bundled with Android SDK)
$ANDROID_HOME/tools/bin/uiautomatorviewer
```

**Locator Priority (most stable → least stable):**
1. `accessibility` (content-desc) — preferred
2. `resourceId` (resource-id)
3. `xpath` with `@text` or `contains(@text,...)`
4. `xpath` with position index — avoid

---

## Extending the Gold Loan E2E Tests

1. Open `GoldLoanTest.java`
2. Set `enabled = true` on the @Test you are implementing
3. Open `SupportPages.java` → `GoldLoanPage` class
4. Add locators inspected from the live app
5. Implement the action methods
6. Run `mvn test -Dtest=GoldLoanTest`

Planned Gold Loan flows:
- [ ] Apply for new Gold Loan
- [ ] Check Eligibility (gold weight + amount → offer)
- [ ] Track existing loan status
- [ ] View repayment schedule
- [ ] Make partial repayment (sandbox)
- [ ] Download loan closure certificate

---

## Key Design Decisions

| Decision | Choice | Reason |
|---|---|---|
| Wait strategy | `WebDriverWait` (explicit) only | Predictable; no race conditions |
| `Thread.sleep()` | **Never used** | Replaced by `ExpectedConditions` |
| Driver lifecycle | `ThreadLocal<AndroidDriver>` | Safe for parallel test runs |
| Capabilities API | `UiAutomator2Options` | Replaces deprecated `DesiredCapabilities` |
| Page Factory | `AppiumFieldDecorator` | Understands `@AndroidFindBy` |
| Config | `config.properties` | Environment-agnostic; override in CI |
| Logging | Log4j2 | Structured logs; file rolling |

---

## CI/CD Integration (Jenkins / GitHub Actions)

```yaml
# .github/workflows/appium.yml (example)
- name: Run Appium Tests
  run: |
    appium &
    sleep 5
    mvn test -Dappium.url=http://127.0.0.1:4723 \
             -Ddevice.udid=${{ secrets.DEVICE_UDID }}
```

For BrowserStack / SauceLabs, override `appium.url` and `device.udid` via
environment variables or Maven `-D` flags.

---

## Reports

After `mvn test`:
- **TestNG HTML report:** `target/surefire-reports/index.html`
- **Framework log:**       `logs/automation.log`

To add ExtentReports (already in pom.xml), create an `ExtentReportListener`
class in `src/main/java/com/asirvadogl/listeners/` and register it in `testng.xml`.
