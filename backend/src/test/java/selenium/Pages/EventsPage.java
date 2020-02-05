package selenium.Pages;

import com.siit.ticketist.model.Category;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class EventsPage extends BasePage {

    @FindBy(xpath = "//button[@routerlink=\"/my-reservations\"]")
    private WebElement myReservationsButton;

    @FindBy(xpath = "//button[@routerlink=\"/profile\"]")
    private WebElement profileButton;

    @FindBy(xpath = "//button[@routerlink=\"/venues/list\"]")
    private WebElement venuesButton;

    @FindBy(xpath = "//form/button")
    private WebElement searchButton;

    @FindBy(id = "search-event-name")
    private WebElement searchEventName;

    @FindBy(id = "search-category")
    private WebElement searchCategory;

    @FindBy(id = "search-venue-name")
    private WebElement searchVenueName;

    @FindBy(id = "search-start-date")
    private WebElement searchStarDate;

    @FindBy(id = "search-end-date")
    private WebElement searchEndDate;

    @FindBy(id = "loaded-events-list")
    private WebElement eventsList;

    @FindBy(id = "number-of-results")
    private WebElement numberOfResults;

    public EventsPage(WebDriver driver) { super(driver); }

    public WebElement getSearchButton() {
        return searchButton;
    }

    public WebElement getProfileButton() { return this.profileButton; }

    public WebElement getVenuesButton() { return this.venuesButton; }

    public WebElement getNumberOfResults() { return this.numberOfResults; }

    public void ensureSearchButtonIsDisplayed() {
        ensureIsDisplayed(searchButton);
    }

    public void ensureMyReservationsButtonIsDisplayed() {
        ensureIsDisplayed(myReservationsButton);
    }

    public void ensureIsDisplayed2(){
        ensureIsDisplayed(profileButton);
    }

    public void ensureVenuesButtonIsDisplayed(){
        ensureIsDisplayed(venuesButton);
    }

    public void ensureEventsAreLoaded() { ensureIsDisplayed(eventsList); }

    public void ensureNumberOfResultsIs(String text) {
        ensureElementContainsText(numberOfResults, text);
    }

    public void navigateToMyReservations() {
        myReservationsButton.click();
    }

    public void submitProfile() {
        WebElement el = getProfileButton();
        el.click();
    }

    public void clickVenuesButton() { clickElement(venuesButton); }

    public void clickSearchButton() { clickElement(searchButton); }

    public void enterEventName(String text) { enterText(searchEventName, text); }

    public void enterCategory(Category text) {
        ensureIsDisplayed(searchCategory);
        clear(searchCategory);
        searchCategory.sendKeys(text.toString());
    }

    public void enterVenueName(String text) {
        ensureIsDisplayed(searchVenueName);
//        clear(searchVenueName);
//        searchVenueName.sendKeys(text);
        Select select = new Select(searchVenueName);
        select.deselectAll();
        select.selectByValue(text);
    }

    public void enterStarDate(String text) { enterText(searchStarDate, text); }

    public void enterEndDate(String text) { enterText(searchEndDate, text); }

    public void clearAllSearchFields() {
        searchEventName.clear();
        searchEventName.sendKeys("A");
        searchEventName.sendKeys(Keys.BACK_SPACE);

//        clear(searchCategory);
//
//        Select venueNameSelect = new Select(searchVenueName);
//        venueNameSelect.deselectAll();

        clear(searchStarDate);
        searchStarDate.sendKeys("A");
        searchStarDate.sendKeys(Keys.BACK_SPACE);

        clear(searchEndDate);
        searchEndDate.sendKeys("A");
        searchEndDate.sendKeys(Keys.BACK_SPACE);
    }
}
