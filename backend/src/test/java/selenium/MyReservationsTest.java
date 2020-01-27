package selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.Pages.EventsPage;
import selenium.Pages.LoginPage;
import selenium.Pages.MyReservationsPage;

import static org.junit.Assert.*;

public class MyReservationsTest {

    private WebDriver browser;

    MyReservationsPage myReservationsPage;
    LoginPage loginPage;
    EventsPage eventsPage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver-mac");
        browser = new ChromeDriver();
        browser.manage().window().maximize();
        browser.navigate().to("http://localhost:4200/login");
        loginPage = PageFactory.initElements(browser, LoginPage.class);
        myReservationsPage = PageFactory.initElements(browser, MyReservationsPage.class);
        eventsPage = PageFactory.initElements(browser, EventsPage.class);
    }

    @Test
    public void myReservationsTest() throws InterruptedException {
        // login page
        loginPage.ensureIsDisplayed();
        assertEquals("http://localhost:4200/login", browser.getCurrentUrl());
        loginPage.setUsernameInput("user2020");
        loginPage.setPasswordInput("123456");
        assertTrue(loginPage.getLoginButton().isDisplayed());
        loginPage.submitLogin();

        // main(event) page
        eventsPage.ensureMyReservationsButtonIsDisplayed();
        assertEquals("http://localhost:4200/events", browser.getCurrentUrl());
        eventsPage.navigateToMyReservations();

        // my-reservation page
        myReservationsPage.ensureIsDisplayed();
        assertEquals("http://localhost:4200/my-reservations", browser.getCurrentUrl());
        assertTrue("next page button is enabled", myReservationsPage.getNextPageButton().isEnabled());
        assertFalse("previous page button is disabled", myReservationsPage.getPreviousPageButton().isEnabled());

        // go to second page
        myReservationsPage.moveToNextPage();
        myReservationsPage.ensureWantedPageLabelIsDisplayed("4 – 4 of 4");
        assertFalse("next page button is disabled", myReservationsPage.getNextPageButton().isEnabled());
        assertTrue("previous page button is enabled", myReservationsPage.getPreviousPageButton().isEnabled());

        // move back to first page
        myReservationsPage.moveToPreviousPage();
        myReservationsPage.ensureWantedPageLabelIsDisplayed("1 – 3 of 4");
        assertTrue("next page button is enabled", myReservationsPage.getNextPageButton().isEnabled());

        // cancel event
        myReservationsPage.cancelFirstReservationInList();
        myReservationsPage.ensureWantedPageLabelIsDisplayed("1 – 3 of 3");
        assertFalse("next page button is disabled", myReservationsPage.getNextPageButton().isEnabled());
        // assert that number of reservations on first page is 3
        assertSame(myReservationsPage.getNumberOfReservationsOnPage(), 3);

        // cancel another event
        myReservationsPage.cancelFirstReservationInList();
        myReservationsPage.ensureWantedPageLabelIsDisplayed("1 – 2 of 2");
        // assert that number of reservations on first page is 2
        assertSame(myReservationsPage.getNumberOfReservationsOnPage(), 2);

        // accept reservation and move to /checkout page
        myReservationsPage.payFirstReservationInList();
        // Todo check navigation to /checkout (wait for component to be done)

    }

    @After
    public void closeSelenium() {
        browser.quit();
    }

}
