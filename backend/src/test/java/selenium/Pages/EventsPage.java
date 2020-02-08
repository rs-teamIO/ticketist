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

    @FindBy(xpath = "//button[@routerlink=\"/events/new\"]")
    private WebElement newEventButton;

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

    @FindBy(css = "button.mat-paginator-navigation-next")
    private WebElement nextPageButton;

    @FindBy(xpath = "//div[contains(@class, 'card-item')][4]/app-event-item//button")
    private WebElement buyReserveEXITButton;

    @FindBy(xpath = "/html/body/app-root/app-header/mat-toolbar/div/button[4]")
    private WebElement logoutButton;

    @FindBy(xpath = "/html/body/app-root/app-event/div/app-event-list/div/mat-card/mat-card-header/p[1]")
    private WebElement futureEventsPar;

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

    public void navigateToNewEvent() { newEventButton.click(); }

    public void ensureIsDisplayedNewEventButton() { ensureIsDisplayed(newEventButton);}

    public void submitProfile() {
        WebElement el = getProfileButton();
        el.click();
    }



    public void clickVenuesButton() { clickElement(venuesButton); }

    public void clickSearchButton() { clickElement(searchButton); }

    public void clickNextPageButton() { clickElement(nextPageButton); }

    public void clickBuyReserveButton() { clickElement(buyReserveEXITButton); }

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

    public void ensureIsDisplayedLogout() { ensureIsDisplayed(logoutButton);}

    public void ensureIsDisplayedPage() { ensureIsDisplayed(futureEventsPar); }

    public void logout() { clickElement(logoutButton);}

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
