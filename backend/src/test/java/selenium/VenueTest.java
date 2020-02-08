package selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.Pages.EventsPage;
import selenium.Pages.LoginPage;
import selenium.Pages.VenuePage;
import selenium.Pages.VenuesPage;

import static org.junit.Assert.*;

public class VenueTest {
    private WebDriver browser;

    LoginPage loginPage;
    EventsPage eventsPage;
    VenuesPage venuesPage;
    VenuePage venuePage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver.exe");
        browser = new ChromeDriver();
        browser.manage().window().maximize();
        browser.navigate().to("http://localhost:4200/login");
        venuePage = PageFactory.initElements(browser, VenuePage.class);
        eventsPage = PageFactory.initElements(browser, EventsPage.class);
        loginPage = PageFactory.initElements(browser, LoginPage.class);
        venuesPage = PageFactory.initElements(browser, VenuesPage.class);
    }

    @Test
    public void updateTest() throws InterruptedException {
        loginPage.ensureIsDisplayed();

        assertEquals("http://localhost:4200/login", browser.getCurrentUrl());

        loginPage.setUsernameInput("admin");
        loginPage.setPasswordInput("123456");
        assertTrue(loginPage.getLoginButton().isEnabled());
        loginPage.submitLogin();
        eventsPage.ensureSearchButtonIsDisplayed();
        eventsPage.ensureVenuesButtonIsDisplayed();
        eventsPage.clickVenuesButton();
        venuesPage.ensureIsDisplayed();
        venuesPage.ensureIsDisplayed2();

        //Table loaded
        assertFalse(venuesPage.getName().getText().equalsIgnoreCase(""));

        //Activation works
        if(venuesPage.getStatus().getText().equals("Active")){
            venuesPage.pressStatus();
            venuesPage.ensureStatusChanged("Inactive");
        }else{
            venuesPage.pressStatus();
            venuesPage.ensureStatusChanged("Active");
        }

        //Add venue, empty form
        venuesPage.pressNewButton();
        venuePage.ensureSubmitButtonIsDisplayed();
        assertFalse(venuePage.getSubmitButton().isEnabled());
        assertFalse(venuePage.getViewMapButton().isEnabled());

        //Add venue, venue exists
        venuePage.setNameInput("Spens");
        venuePage.setStreetInput("Sutjeska 2");
        venuePage.setCityInput("Novi Sad");
        assertTrue(venuePage.getSubmitButton().isEnabled());
        venuePage.clickSubmitButton();
        venuePage.ensureResponseErrorMessageIsEqualToString("Venue name, street and city combination must be unique!");
        assertEquals("Venue name, street and city combination must be unique!",venuePage.getResponseErrorMessage().getText());

        //View map, form valid
        assertTrue(venuePage.getViewMapButton().isEnabled());
        venuePage.submitViewMap();

        //Add venue, form valid
        venuePage.setNameInput("Tasmajdan");
        venuePage.clickSubmitButton();
        venuePage.ensureResponsePassed();
        venuePage.pressVenues();

        //View venue
        venuesPage.ensureIsDisplayed();
        venuesPage.ensureIsDisplayed2();
        String temp = venuesPage.getName().getText();
        venuesPage.pressView();
        venuePage.ensureIsDisplayed2();
        venuePage.clickSubmitButton();
        venuePage.ensureResponseErrorMessageIsEqualToString("Venue name, street and city combination must be unique!");
        assertEquals("Venue name, street and city combination must be unique!",venuePage.getResponseErrorMessage().getText());
        venuePage.pressVenues();

        //Pagination works
        venuesPage.ensureIsDisplayed();
        venuesPage.ensureIsDisplayed2();
        assertTrue(venuesPage.getNextPage().isEnabled());
        assertFalse(venuesPage.getPreviousPage().isEnabled());
        venuesPage.pressNextPage();
        assertTrue(venuesPage.getPreviousPage().isEnabled());
    }

    @After
    public void closeSelenium() {
        browser.quit();
    }

}
