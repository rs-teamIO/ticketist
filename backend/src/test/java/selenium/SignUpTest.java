package selenium;

import selenium.Pages.EventsPage;
import selenium.Pages.SignUpPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import static org.junit.Assert.*;

public class SignUpTest {
    private WebDriver browser;

    SignUpPage signUpPage;
    EventsPage eventsPage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver.exe");
        browser = new ChromeDriver();
        browser.manage().window().maximize();
        browser.navigate().to("http://localhost:4200/signup");
        signUpPage = PageFactory.initElements(browser, SignUpPage.class);
        eventsPage = PageFactory.initElements(browser, EventsPage.class);
    }

    @Test
    public void loginTest() {
        signUpPage.ensureIsDisplayed();

        assertEquals("http://localhost:4200/signup", browser.getCurrentUrl());

        // All fields empty
        assertFalse(signUpPage.getSubmitButton().isEnabled());

        // Username filled, rest empty
        signUpPage.setUsernameInput("asfk");
        assertFalse(signUpPage.getSubmitButton().isEnabled());

        // All filled, username taken
        signUpPage.setFirstNameInput("ahfs");
        signUpPage.setLastNameInput("hasf");
        signUpPage.setUsernameInput("user2020");
        signUpPage.setPasswordInput("123456");
        signUpPage.setEmailInput("email@gmail.com");
        assertTrue(signUpPage.getSubmitButton().isEnabled());
        signUpPage.submitSignUp();
        signUpPage.ensureResponseErrorMessageIsEqualToString("Username 'user2020' is already taken");
        String errorMessage1 = signUpPage.getResponseErrorMessage().getText();
        assertEquals("Username 'user2020' is already taken", errorMessage1);

        // All filled, email format wrong
        signUpPage.setEmailInput("emailgmail");
        assertFalse(signUpPage.getSubmitButton().isEnabled());

        // All filled, email taken
        signUpPage.setUsernameInput("user");
        signUpPage.setEmailInput("kacjica+1@gmail.com");
        assertTrue(signUpPage.getSubmitButton().isEnabled());
        signUpPage.submitSignUp();
        signUpPage.ensureResponseErrorMessageIsEqualToString("User with e-mail 'kacjica+1@gmail.com' already registered");
        String errorMessage2 = signUpPage.getResponseErrorMessage().getText();
        assertEquals("User with e-mail 'kacjica+1@gmail.com' already registered", errorMessage2);

        // All filled, correct input
        signUpPage.setUsernameInput("user");
        signUpPage.setEmailInput("email@gmail.com");
        assertTrue(signUpPage.getSubmitButton().isEnabled());
        signUpPage.submitSignUp();
        eventsPage.ensureIsDisplayed();
    }

    @After
    public void closeSelenium() {
        browser.quit();
    }
}
