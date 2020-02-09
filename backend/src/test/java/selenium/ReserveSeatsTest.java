package selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.Pages.EventDetailsPage;
import selenium.Pages.EventsPage;
import selenium.Pages.LoginPage;

import static org.junit.Assert.*;

public class ReserveSeatsTest {

    private WebDriver driver;

    LoginPage loginPage;
    EventsPage eventsPage;
    EventDetailsPage eventDetailsPage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to("http://localhost:4200/login");
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        eventsPage = PageFactory.initElements(driver, EventsPage.class);
        eventDetailsPage = PageFactory.initElements(driver, EventDetailsPage.class);
    }

    @After
    public void closeSelenium() {
        driver.quit();
    }

    @Test
    public void testSelectSeats() throws InterruptedException {
        login();
        getToEventDetails();
        testReserveNotEnumeratedSeats();

        getToEventDetails();
        testReserveAlreadyReservedSeat();
    }

    private void login() {
        loginPage.ensureIsDisplayed();
        loginPage.setUsernameInput("user2020");
        loginPage.setPasswordInput("123456");
        loginPage.ensureIsDisplayed();
        loginPage.submitLogin();
    }

    private void getToEventDetails() throws InterruptedException {
        eventsPage.ensureEventsAreLoaded();
        eventsPage.clickNextPageButton();
        Thread.sleep(3000);
        eventsPage.clickBuyReserveButton();
        assertEquals("http://localhost:4200/event/12", driver.getCurrentUrl());
    }

    private void testReserveNotEnumeratedSeats() {
        // select date
        eventDetailsPage.clickDateSelect();
        eventDetailsPage.clickDatum();

        // select sector
        eventDetailsPage.clickSectorSelect();
        eventDetailsPage.clickZapad();

        // reserve and buy buttons are disabled
        assertFalse(eventDetailsPage.getReserveButton().isEnabled());
        assertFalse(eventDetailsPage.getBuyButton().isEnabled());

        // select 1 seat
        eventDetailsPage.clickPlusButton();

        // reserve and buy buttons are enabled
        eventDetailsPage.ensureReserveIsClickable();
        assertTrue(eventDetailsPage.getReserveButton().isEnabled());
        assertTrue(eventDetailsPage.getBuyButton().isEnabled());

        // reserve
        eventDetailsPage.clickReserveButton();

        // reservation successful
        eventsPage.ensureEventsAreLoaded();
        assertEquals("http://localhost:4200/events", driver.getCurrentUrl());
    }

    private void testReserveAlreadyReservedSeat() {
        // try to reserve seat when there are no available seats
        eventDetailsPage.clickDateSelect();
        eventDetailsPage.clickDatum();
        eventDetailsPage.clickSectorSelect();
        eventDetailsPage.clickZapad();
        eventDetailsPage.clickPlusButton();
        eventDetailsPage.ensureSwalAlertShowsWarning("No more tickets available for this sector");
    }
}
