package selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.Pages.EventsPage;
import selenium.Pages.LoginPage;
import selenium.Pages.ProfilePage;

import static org.junit.Assert.*;

public class ProfileTest {
    private WebDriver browser;

    LoginPage loginPage;
    EventsPage eventsPage;
    ProfilePage profilePage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver-mac");
        browser = new ChromeDriver();
        browser.manage().window().maximize();
        browser.navigate().to("http://localhost:4200/login");
        profilePage = PageFactory.initElements(browser, ProfilePage.class);
        eventsPage = PageFactory.initElements(browser, EventsPage.class);
        loginPage = PageFactory.initElements(browser, LoginPage.class);
    }

    @Test
    public void updateTest() {
        loginPage.ensureIsDisplayed();

        assertEquals("http://localhost:4200/login", browser.getCurrentUrl());

        loginPage.setUsernameInput("user2020");
        loginPage.setPasswordInput("123456");
        assertTrue(loginPage.getLoginButton().isEnabled());
        loginPage.submitLogin();
        eventsPage.ensureSearchButtonIsDisplayed();
        eventsPage.ensureIsDisplayed2();
        eventsPage.submitProfile();
        profilePage.ensureIsDisplayed();

        // Password empty
        assertFalse(profilePage.getUpdateButton().isEnabled());

        // All filled, Email format wrong
        profilePage.setOldPasswordInput("123456");
        profilePage.setEmailInput("emailgmail");
        assertFalse(profilePage.getUpdateButton().isEnabled());

        // All filled, email taken
        profilePage.setEmailInput("f.ivkovic16+1@gmail.com");
        assertTrue(profilePage.getUpdateButton().isEnabled());
        profilePage.submitUpdate();
        profilePage.ensureResponseErrorMessageIsEqualToString("User with e-mail 'f.ivkovic16+1@gmail.com' already registered");
        String errorMessage1 = profilePage.getResponseErrorMessage().getText();
        assertEquals("User with e-mail 'f.ivkovic16+1@gmail.com' already registered", errorMessage1);

        // All filled, incorrect password
        profilePage.setEmailInput("kacjica+1@gmail.com");
        profilePage.setOldPasswordInput("123");
        assertTrue(profilePage.getUpdateButton().isEnabled());
        profilePage.submitUpdate();
        profilePage.ensureResponseErrorMessageIsEqualToString("Incorrect password.");
        String errorMessage2 = profilePage.getResponseErrorMessage().getText();
        assertEquals("Incorrect password.", errorMessage2);

        // All filled, new passwords don't match
        profilePage.setEmailInput("f.ivkovic16+1@gmail.com");
        profilePage.setOldPasswordInput("123456");
        profilePage.setNewPasswordInput("123");
        profilePage.setNewPasswordRepeatInput("12");
        assertTrue(profilePage.getUpdateButton().isEnabled());
        profilePage.submitUpdate();
        profilePage.ensureResponseErrorMessageIsEqualToString("New passwords don't match.");
        String errorMessage3 = profilePage.getResponseErrorMessage().getText();
        assertEquals("New passwords don't match.", errorMessage3);

        // All filled, correct input
        profilePage.setNewPasswordInput("123");
        profilePage.setNewPasswordRepeatInput("123");
        profilePage.setOldPasswordInput("123456");
        assertTrue(profilePage.getUpdateButton().isEnabled());
        profilePage.submitUpdate();
        profilePage.ensureResponsePassed();
    }

    @After
    public void closeSelenium() {
        browser.quit();
    }

}
