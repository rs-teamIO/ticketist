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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;

public class SearchTest {

    private WebDriver driver;

    EventsPage eventsPage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to("http://localhost:4200/events");

        eventsPage = PageFactory.initElements(driver, EventsPage.class);
    }

    @After
    public void closeSelenium() {
        driver.quit();
    }

    @Test
    public void searchTest() throws InterruptedException {

        eventsPage.ensureEventsAreLoaded();

        testSearchEventsByName();
        testSearchEmptyFields();
        testSearchEventsByCategory();
        testSearchEventsByVenueName();
        testSearchEventsByStartDate();
        testSearchEventsByEndDate();
        testSearchNonExistingEvent();

    }

    private void testSearchEmptyFields() throws InterruptedException {
        eventsPage.clearAllSearchFields();
        eventsPage.clickSearchButton();
        eventsPage.ensureNumberOfResultsIs("Results found: 12");
        assertThat("All events should be present",
                eventsPage.getNumberOfResults().getText().contains("Results found: 12"));
        Thread.sleep(5000);
    }

    private void testSearchEventsByName() throws InterruptedException {
        eventsPage.clearAllSearchFields();
        eventsPage.enterEventName("EXIT");
        eventsPage.clickSearchButton();
        eventsPage.ensureNumberOfResultsIs("Results found: 1");
        assertThat("There is 1 event with EXIT in the name",
                eventsPage.getNumberOfResults().getText().contains("Results found: 1"));

        driver.findElements(By.xpath("//app-event-item//mat-card-title")).forEach(eventName ->
                assertThat("Search result item contains EXIT in its name.", eventName.getText().toUpperCase().contains("EXIT")));
        Thread.sleep(5000);

    }

    private void testSearchEventsByCategory() throws InterruptedException {
        eventsPage.clearAllSearchFields();
        eventsPage.selectCategoryEntertainment();
        eventsPage.clickSearchButton();
        eventsPage.ensureNumberOfResultsIs("Results found: 4");
        assertThat("There are 4 events with ENTERTAINMENT category",
                eventsPage.getNumberOfResults().getText().contains("Results found: 4"));
        Thread.sleep(5000);
    }

    private void testSearchEventsByVenueName() throws InterruptedException {
        eventsPage.clearAllSearchFields();
        eventsPage.selectVenueSpens();
        eventsPage.clickSearchButton();
        eventsPage.ensureNumberOfResultsIs("Results found: 10");
        assertThat("There are 10 events with 'Spens' venue",
                eventsPage.getNumberOfResults().getText().contains("Results found: 10"));
        Thread.sleep(5000);
    }

    private void testSearchEventsByStartDate() throws InterruptedException {
        eventsPage.clearAllSearchFields();
        eventsPage.enterStarDate("6/14/2020");
        eventsPage.clickSearchButton();
        eventsPage.ensureNumberOfResultsIs("Results found: 1");
        assertThat("There is 1 event that starts after 06/14/2020",
                eventsPage.getNumberOfResults().getText().contains("Results found: 1"));
        List<WebElement> searchResultDates =
                driver.findElements(By.xpath("//app-event-item//mat-card-subtitle//div"));

        searchResultDates.forEach(eventStartDate -> {
            Date searchItemStartDate = null;
            Date givenDate = null;
            try {
                searchItemStartDate = parseDate(eventStartDate.getText().split(" - ", 2)[0]);
                givenDate = parseDate("Jun 14, 2020");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertThat("Search item start date starts after 06/14/2020",
                    searchItemStartDate.after(givenDate) || searchItemStartDate.equals(givenDate));
        });
        Thread.sleep(5000);

    }

    private void testSearchEventsByEndDate() throws InterruptedException {
        eventsPage.clearAllSearchFields();
        eventsPage.enterEndDate("6/20/2020");
        eventsPage.clickSearchButton();
        eventsPage.ensureNumberOfResultsIs("Results found: 12");
        assertThat("All 12 events start before 06/20/2020",
                eventsPage.getNumberOfResults().getText().contains("Results found: 12"));
        List<WebElement> searchResultDates =
                driver.findElements(By.xpath("//app-event-item//mat-card-subtitle//div"));

        searchResultDates.forEach(eventStartDate -> {
            Date searchItemStartDate = null;
            Date givenDate = null;
            try {
                searchItemStartDate = parseDate(eventStartDate.getText().split(" - ", 2)[0]);
                givenDate = parseDate("Jun 20, 2020");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertThat("Search item start date starts before 06/20/2020",
                    searchItemStartDate.before(givenDate) || searchItemStartDate.equals(givenDate));
        });
        Thread.sleep(5000);
    }

    private void testSearchNonExistingEvent() throws InterruptedException {
        eventsPage.clearAllSearchFields();
        eventsPage.enterEventName("Nepostojeci");
        eventsPage.clickSearchButton();
        eventsPage.ensureNumberOfResultsIs("Results found: 0");
        assertThat("No events",
                eventsPage.getNumberOfResults().getText().contains("Results found: 0"));
        Thread.sleep(5000);
    }

    private Date parseDate(String stringDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        return sdf.parse(stringDate);

    }
}
