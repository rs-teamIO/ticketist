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
import selenium.Pages.ReportsPage;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class ReportsTest {

    private WebDriver driver;

    LoginPage loginPage;
    EventsPage eventsPage;
    ReportsPage reportsPage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to("http://localhost:4200/login");
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        eventsPage = PageFactory.initElements(driver, EventsPage.class);
        reportsPage = PageFactory.initElements(driver, ReportsPage.class);
    }

    @After
    public void closeSelenium() {
        driver.quit();
    }

    @Test
    public void testReports() {
        /*
            This test is meant to be run with full.sql
         */
        getToReports();
        testCheckIfReportsWork();
    }

    private void getToReports() {
        // login
        loginPage.ensureIsDisplayed();
        loginPage.setUsernameInput("admin");
        loginPage.setPasswordInput("123456");
        loginPage.ensureIsDisplayed();
        loginPage.submitLogin();

        // go to reports
        eventsPage.clickReportsButton();
        assertEquals("http://localhost:4200/reports", driver.getCurrentUrl());
    }

    private void testCheckIfReportsWork() {
        // all venues should be on bar chart
        reportsPage.ensureChartLegendIsDisplayed();
        List<WebElement> venueNames =
                driver.findElements(By.xpath("//app-report-chart//ngx-charts-legend//span[contains(@class, 'legend-label-text')]"));
        assertThat(venueNames, hasSize(4));

        // all events should be in the table
        reportsPage.ensureEventsTableIsDisplayed();
        reportsPage.ensureTableDataIsDisplayed();
        List<WebElement> eventNames =
                driver.findElements(By.xpath("//td[contains(@class, 'event-name-td')]"));
        assertThat(eventNames, hasSize(10));

        // show details about first venue
        reportsPage.clickOnFirstVenueName();
        reportsPage.ensureChartLegendIsDisplayed();
        venueNames = driver.findElements(By.xpath("//app-report-chart//ngx-charts-legend//span[contains(@class, 'legend-label-text')]"));
        assertThat(venueNames, hasSize(1));

        // there are 3 events in table for first venue
        reportsPage.ensureEventsTableIsDisplayed();
        reportsPage.ensureTableDataIsDisplayed();
        eventNames = driver.findElements(By.xpath("//td[contains(@class, 'event-name-td')]"));
        assertThat(eventNames, hasSize(3));

        // clicking on all venues button all venues are visible in chart
        reportsPage.clickOnAllVenuesButton();
        reportsPage.ensureChartLegendIsDisplayed();
        venueNames = driver.findElements(By.xpath("//app-report-chart//ngx-charts-legend//span[contains(@class, 'legend-label-text')]"));
        assertThat(venueNames, hasSize(4));

        // and all events are in the table
        reportsPage.ensureEventsTableIsDisplayed();
        reportsPage.ensureTableDataIsDisplayed();
        eventNames = driver.findElements(By.xpath("//td[contains(@class, 'event-name-td')]"));
        assertThat(eventNames, hasSize(10));

    }

}
