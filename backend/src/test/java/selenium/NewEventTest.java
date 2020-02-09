package selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.Pages.EventsPage;
import selenium.Pages.LoginPage;
import selenium.Pages.NewEventPage;

import static org.junit.Assert.*;

public class NewEventTest {

    private WebDriver browser;

    LoginPage loginPage;
    EventsPage eventsPage;
    NewEventPage newEventPage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver-mac");
        browser = new ChromeDriver();
        browser.manage().window().maximize();
        browser.navigate().to("http://localhost:4200/login");
        loginPage = PageFactory.initElements(browser, LoginPage.class);
        eventsPage = PageFactory.initElements(browser, EventsPage.class);
        newEventPage = PageFactory.initElements(browser, NewEventPage.class);
    }

    @After
    public void closeSelenium() {
        browser.quit();
    }

    @Test
    public void testAddNewEvent() {
        goToNewEventPage();
        testErrorMessage();
        fillBasicForm();
        fillSectorTable();
        submitAndNavigationBack();
    }

    private void goToNewEventPage() {
        // login
        loginPage.ensureIsDisplayed();
        assertEquals("http://localhost:4200/login", browser.getCurrentUrl());
        loginPage.setUsernameInput("admin");
        loginPage.setPasswordInput("123456");
        assertTrue(loginPage.getLoginButton().isDisplayed());
        loginPage.submitLogin();

        // main page
        eventsPage.ensureIsDisplayedNewEventButton();
        assertEquals("http://localhost:4200/events", browser.getCurrentUrl());
        eventsPage.navigateToNewEvent();

        // new event page
        newEventPage.ensureTableSectorIsDisplayed();
        newEventPage.ensureEventNameInputIsDisplayed();
        newEventPage.ensureVenueSelectIsDisplayed();
        assertEquals("http://localhost:4200/events/new", browser.getCurrentUrl());
    }

    private void testErrorMessage() {
        assertFalse(newEventPage.getSubmitButton().isEnabled());
        newEventPage.clickOnEventNameInput();
        newEventPage.clickOnReservationLimit();
        newEventPage.ensureEventNameErrorMessageIsDisplayed();
        assertEquals("Event name is required!", newEventPage.getNewEventInputError().getText());
    }

    private void fillBasicForm() {
        // enter all basic data for new event
        newEventPage.enterEventName("Super Bowl");
        newEventPage.chooseCategory();
        newEventPage.enterReservationDeadline("2/14/2020");
        newEventPage.enterStartDate("2/15/2020");
        newEventPage.enterEndDate("2/15/2020");
        newEventPage.chooseHours();
        newEventPage.enterReservationLimit("3");
        newEventPage.chooseMinutes();
        newEventPage.enterDescription("Cool event!");
    }

    private void fillSectorTable() {
        // fill sector table
        newEventPage.chooseVenue();
        newEventPage.clickSector1();
        newEventPage.setSector1Price("15");
        newEventPage.setSector1Numerated();
        newEventPage.setSector1Capacity("3");
        newEventPage.clickSector2();
        newEventPage.setSector2Price("22");
    }

    private void submitAndNavigationBack() {
        newEventPage.ensureSubmitIsDisplayed();
        assertTrue(newEventPage.getSubmitButton().isEnabled());
        newEventPage.submitForm();
        eventsPage.ensureIsDisplayedPage();
        assertEquals("http://localhost:4200/events", browser.getCurrentUrl());
        eventsPage.logout();
        loginPage.ensureIsDisplayed();
        assertEquals("http://localhost:4200/login", browser.getCurrentUrl());
    }

}
