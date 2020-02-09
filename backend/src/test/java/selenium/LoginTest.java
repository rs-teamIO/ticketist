package selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.Pages.EventsPage;
import selenium.Pages.LoginPage;

import static org.junit.Assert.*;

public class LoginTest {
    private WebDriver browser;

    LoginPage loginPage;
    EventsPage eventsPage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver.exe");
        browser = new ChromeDriver();
        browser.manage().window().maximize();
        browser.navigate().to("http://localhost:4200/login");
        loginPage = PageFactory.initElements(browser, LoginPage.class);
        eventsPage = PageFactory.initElements(browser, EventsPage.class);
    }

    @Test
    public void loginTest() {
        loginPage.ensureIsDisplayed();

        assertEquals("http://localhost:4200/login", browser.getCurrentUrl());

        // Username empty, password empty
        assertFalse(loginPage.getLoginButton().isEnabled());

        // Username filled, password empty
        loginPage.setUsernameInput("user2020");
        loginPage.resetPasswordInput();
        assertFalse(loginPage.getLoginButton().isEnabled());

        // Username empty, password filled
        loginPage.resetUsernameInput();
        loginPage.setPasswordInput("123456");
        assertFalse(loginPage.getLoginButton().isEnabled());

        // All filled, username wrong, password wrong
        loginPage.setUsernameInput("uasdhuagfuag");
        loginPage.setPasswordInput("abc123");
        assertTrue(loginPage.getLoginButton().isEnabled());
        loginPage.submitLogin();
        loginPage.ensureResponseErrorMessageIsEqualToString("User credentials invalid.");
        String errorMessage1 = loginPage.getResponseErrorMessage().getText();
        assertEquals("User credentials invalid.", errorMessage1);

        // All filled, username correct, password wrong
        loginPage.setUsernameInput("user2020");
        loginPage.setPasswordInput("abc123");
        assertTrue(loginPage.getLoginButton().isEnabled());
        loginPage.submitLogin();
        loginPage.ensureResponseErrorMessageIsEqualToString("User credentials invalid.");
        String errorMessage2 = loginPage.getResponseErrorMessage().getText();
        assertEquals("User credentials invalid.", errorMessage2);

        // All filled, username correct, password correct
        loginPage.setUsernameInput("user2020");
        loginPage.setPasswordInput("123456");
        assertTrue(loginPage.getLoginButton().isEnabled());
        loginPage.submitLogin();
        eventsPage.ensureSearchButtonIsDisplayed();
    }

    @After
    public void closeSelenium() {
        browser.quit();
    }
}
