package selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.Pages.EventsPage;
import selenium.Pages.LoginPage;
import selenium.Pages.VenuePage;
import selenium.Pages.VenuesPage;

import java.util.List;

import static org.junit.Assert.*;

public class AddVenueTest {

    private WebDriver driver;

    LoginPage loginPage;
    EventsPage eventsPage;
    VenuesPage venuesPage;
    VenuePage venuePage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to("http://localhost:4200/login");
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        eventsPage = PageFactory.initElements(driver, EventsPage.class);
        venuesPage = PageFactory.initElements(driver, VenuesPage.class);
        venuePage = PageFactory.initElements(driver, VenuePage.class);
    }

    @After
    public void closeSelenium() {
        driver.quit();
    }

    @Test
    public void testAddVenue() {
        goToAddVenue();
        testAddNewVenue();
        testAddSameVenue();
    }

    private void goToAddVenue() {
        // login
        loginPage.ensureIsDisplayed();
        loginPage.setUsernameInput("admin");
        loginPage.setPasswordInput("123456");
        loginPage.ensureIsDisplayed();
        loginPage.submitLogin();

        // venues list
        eventsPage.clickVenuesButton();
        venuesPage.ensureIsDisplayed();
        venuesPage.ensureIsDisplayed2();
        venuesPage.pressNewButton();

        venuePage.ensureSubmitButtonIsDisplayed();
        assertEquals("http://localhost:4200/venues/new", driver.getCurrentUrl());
    }

    private void testAddNewVenue() {
        assertFalse(venuePage.getSubmitButton().isEnabled());
        assertFalse(venuePage.getViewMapButton().isEnabled());

        // Add venue
        venuePage.setNameInput("Totally new venue");
        venuePage.setStreetInput("Mise Dimitrijevica 2");
        venuePage.setCityInput("Novi Sad");
        assertTrue(venuePage.getSubmitButton().isEnabled());
        venuePage.clickSubmitButton();

        // check swal, venue must contain sectors
        venuePage.ensureSwalAlertContentEquals("Venue must contain at least 1 sector");
        venuePage.clickSwalOkButton();

        //View map, form valid
        assertTrue(venuePage.getViewMapButton().isEnabled());
        venuePage.submitViewMap();

        // ensure there are no added sectors
        List<WebElement> gridsterItems =
                driver.findElements(By.xpath("//gridster//gridster-item"));
        assertTrue(gridsterItems.isEmpty());

        // Add sector
        assertFalse(venuePage.getAddSectorButton().isEnabled());
        venuePage.enterSectorName("Sector 1");
        venuePage.enterMaxCapacity("30");
        venuePage.ensureAddSectorIsClickable();
        venuePage.clickAddSectorButton();

        gridsterItems = driver.findElements(By.xpath("//gridster//gridster-item"));
        assertEquals(1, gridsterItems.size());

        venuePage.clickSubmitButton();
        venuePage.ensureSwalAlertIsSuccessful("Venue added successfully!");
        venuePage.clickSwalOkButton();

        assertEquals("http://localhost:4200/venues/list", driver.getCurrentUrl());
    }

    private void testAddSameVenue() {
        eventsPage.clickVenuesButton();
        venuesPage.ensureIsDisplayed();
        venuesPage.ensureIsDisplayed2();
        venuesPage.pressNewButton();
        venuePage.ensureSubmitButtonIsDisplayed();

        // Add same venue
        venuePage.setNameInput("Totally new venue");
        venuePage.setStreetInput("Mise Dimitrijevica 2");
        venuePage.setCityInput("Novi Sad");
        venuePage.enterSectorName("Sector 1");
        venuePage.enterMaxCapacity("30");
        venuePage.ensureAddSectorIsClickable();
        venuePage.clickAddSectorButton();
        venuePage.clickSubmitButton();

        // Error message is visible
        venuePage.ensureResponseErrorMessageIsEqualToString("Venue name, street and city combination must be unique!");
        assertEquals("Venue name, street and city combination must be unique!",venuePage.getResponseErrorMessage().getText());
    }
}
