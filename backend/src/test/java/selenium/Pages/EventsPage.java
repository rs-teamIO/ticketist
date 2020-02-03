package selenium.Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EventsPage {
    private WebDriver driver;

    @FindBy(xpath = "//button[@routerlink=\"/my-reservations\"]")
    private WebElement myReservationsButton;

    @FindBy(xpath = "//button[@routerlink=\"/profile\"]")
    private WebElement profileButton;

    @FindBy(xpath = "/html/body/app-root/app-header/mat-toolbar/div/button[3]")
    private WebElement venuesButton;

    @FindBy(xpath = "//form/button")
    private WebElement searchButton;

    public EventsPage(WebDriver driver) { this.driver = driver; }

    public WebElement getSearchButton() {
        return searchButton;
    }

    public WebElement getProfileButton() { return this.profileButton; }

    public WebElement getVenuesButton() { return this.venuesButton; }

    public void ensureIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(searchButton));
    }

    public void ensureMyReservationsButtonIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(myReservationsButton));
    }

    public void ensureIsDisplayed2(){
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(profileButton));
    }

    public void ensureIsDisplayed3(){
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(venuesButton));
    }

    public void navigateToMyReservations() {
        myReservationsButton.click();
    }

    public void submitProfile() {
        WebElement el = getProfileButton();
        el.click();
    }

    public void submitVenues() {
        WebElement el = getVenuesButton();
        el.click();
    }
}